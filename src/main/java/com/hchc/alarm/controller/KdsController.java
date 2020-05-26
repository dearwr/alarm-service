package com.hchc.alarm.controller;

import com.hchc.alarm.dao.hchc.BranchDao;
import com.hchc.alarm.dao.hchc.KdsOperationLogDao;
import com.hchc.alarm.dao.rocket.BranchKdsBaseDao;
import com.hchc.alarm.dao.rocket.KdsMessageBaseDao;
import com.hchc.alarm.entity.BranchKdsTb;
import com.hchc.alarm.model.Branch;
import com.hchc.alarm.pack.Output;
import com.hchc.alarm.pack.KdsConsoleInfo;
import com.hchc.alarm.service.RemoteService;
import com.hchc.alarm.util.DatetimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by wangrong 2020/5/12
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
    @Autowired
    private KdsOperationLogDao kdsOperationLogDao;

    @GetMapping("/errKdsInfo")
    public Output getErrorKdsInfo(int hqId, int branchId) {
        log.info("[getErrorKdsInfo] recv request params hqId:{}, branchId:{}", hqId, branchId);
        List<BranchKdsTb> kdsList = branchKdsDao.query(hqId, branchId);
        List<KdsConsoleInfo> errKdsInfoList = new ArrayList<>();
        List<KdsConsoleInfo> offLineKds = new ArrayList<>();
        List<KdsConsoleInfo> onLineKds = new ArrayList<>();
        List<String> kdsQueueOrders;
        KdsConsoleInfo kdsConsoleInfo;
        Branch branch;
        Date start = DatetimeUtil.dayBegin(new Date());
        Date end;
        try {
            for (BranchKdsTb kds : kdsList) {
                kdsConsoleInfo = new KdsConsoleInfo();
                kdsConsoleInfo.setWxCount(remoteService.getWxQueueCount(kds.getHqId(), kds.getBranchId()));
                end = DatetimeUtil.addSecond(new Date(), 15);
                kdsQueueOrders = kdsMessageDao.queryAllPushed(kds.getBranchId(), kds.getUuid(), start, end)
                        .stream()
                        // 去重
                        .distinct()
                        .collect(Collectors.toList());
                log.info("[getErrorKdsInfo] branchId:{}, kdsQueueOrders:{}", kds.getBranchId(), kdsQueueOrders);
                if (kdsQueueOrders.size() != kdsConsoleInfo.getWxCount()) {
                    kdsConsoleInfo.setKdsCount(kdsQueueOrders.size());
                    kdsConsoleInfo.setUuid(kds.getUuid());
                    branch = branchDao.query(kds.getHqId(), kds.getBranchId());
                    kdsConsoleInfo.setBrandName(branch.getBrandName());
                    kdsConsoleInfo.setBranchName(branch.getBranchName());
                    kdsConsoleInfo.setVersionCode(kdsOperationLogDao.queryVersionCode(hqId, branchId, DatetimeUtil.format(start), DatetimeUtil.format(end)));
                    if (kds.getHeartTime() != null) {
                        kdsConsoleInfo.setHeartTime(kds.getHeartTime());
                    }
                    kdsConsoleInfo.setOffLine(checkOffLine(kds.getHeartTime()));
                    if (kdsConsoleInfo.isOffLine()) {
                        offLineKds.add(kdsConsoleInfo);
                    } else {
                        onLineKds.add(kdsConsoleInfo);
                    }
                }
            }
            errKdsInfoList.addAll(offLineKds);
            errKdsInfoList.addAll(onLineKds);
        } catch (Exception e) {
            log.info("[getErrorKdsInfo] happen exception :{}", e.getMessage());
            return Output.fail(e.getMessage());
        }
        return Output.ok(errKdsInfoList);
    }

    private boolean checkOffLine(String heartTime) throws ParseException {
        if (heartTime == null) {
            return true;
        }
        return System.currentTimeMillis() - DatetimeUtil.parse(heartTime).getTime() > 60000;
    }

}
