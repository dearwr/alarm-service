package com.hchc.alarm.controller;

import com.hchc.alarm.dao.hchc.BranchDao;
import com.hchc.alarm.dao.rocket.BranchKdsBaseDao;
import com.hchc.alarm.dao.rocket.KdsMessageBaseDao;
import com.hchc.alarm.entity.BranchKdsDO;
import com.hchc.alarm.model.BranchBO;
import com.hchc.alarm.pack.Output;
import com.hchc.alarm.pack.KdsConsoleInfo;
import com.hchc.alarm.service.RemoteService;
import com.hchc.alarm.util.DatetimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.*;

/**
 * Created by wangrong 2020/5/12
 *
 * @author wangrong
 */
@RestController
@RequestMapping("/kdsConsole")
@Slf4j
public class KdsController {

    @Autowired
    private BranchKdsBaseDao branchKdsDao;
    @Autowired
    private KdsMessageBaseDao kdsMessageDao;
    @Autowired
    private RemoteService remoteService;
    @Autowired
    private BranchDao branchDao;

    @GetMapping("/kdsInfo")
    public Output getKdsInfo(int hqId, int branchId) {
        log.info("[getKdsInfo] recv request params hqId:{}, branchId:{}", hqId, branchId);
        List<BranchKdsDO> kdsList = branchKdsDao.query(hqId, branchId);
        List<KdsConsoleInfo> kdsConsoleInfos = new ArrayList<>();
        List<KdsConsoleInfo> offLineKds = new ArrayList<>();
        List<KdsConsoleInfo> onLineKds = new ArrayList<>();
        List<String[]> orderList;
        Set<String> waitList;
        Set<String> completedList;
        KdsConsoleInfo kdsConsoleInfo;
        BranchBO branchBO;
        Date start = DatetimeUtil.dayBegin(new Date());
        Date end;
        try {
            for (BranchKdsDO kds : kdsList) {
                kdsConsoleInfo = new KdsConsoleInfo();
                if (kds.getHeartTime() == null) {
                    continue;
                }
                kdsConsoleInfo.setHeartTime(kds.getHeartTime());
                kdsConsoleInfo.setOffLine(checkOffLine(kds.getHeartTime()));
                end = DatetimeUtil.addSecond(new Date(), 15);
                orderList = kdsMessageDao.queryAllPushed(kds.getBranchId(), kds.getUuid(), start, end);
                if (CollectionUtils.isEmpty(orderList)) {
                    continue;
                }
                waitList = new HashSet<>(orderList.size());
                completedList = new HashSet<>(orderList.size());
                for (String[] order : orderList) {
                    if ("ORDER_NEW".equals(order[1]) || "ORDER_MAKE".equals(order[1]) || "ORDER_PRE".equals(order[1])) {
                        waitList.add(order[0]);
                    } else {
                        completedList.add(order[0]);
                    }
                }
                for (String completedNo : completedList) {
                    waitList.remove(completedNo);
                }
                kdsConsoleInfo.setWxCount(remoteService.getWxQueueCount(kds.getHqId(), kds.getBranchId()));
                kdsConsoleInfo.setKdsCount(waitList.size());
                kdsConsoleInfo.setUuid(kds.getUuid());
                branchBO = branchDao.query(kds.getHqId(), kds.getBranchId());
                kdsConsoleInfo.setBrandName(branchBO.getBrandName());
                kdsConsoleInfo.setBranchName(branchBO.getBranchName());
                kdsConsoleInfo.setVersionCode(kds.getVersion());
                if (kdsConsoleInfo.isOffLine()) {
                    offLineKds.add(kdsConsoleInfo);
                } else {
                    onLineKds.add(kdsConsoleInfo);
                }
            }
            kdsConsoleInfos.addAll(offLineKds);
            kdsConsoleInfos.addAll(onLineKds);
        } catch (Exception e) {
            log.info("[getKdsInfo] happen exception :{}", e.getMessage());
            return Output.fail(e.getMessage());
        }
        return Output.ok(kdsConsoleInfos);
    }

    private boolean checkOffLine(String heartTime) throws ParseException {
        long offTime = System.currentTimeMillis() - DatetimeUtil.parse(heartTime).getTime();
        return offTime > 1000 * 60 && offTime < 1000 * 60 * 60 * 24 * 2;
    }

}
