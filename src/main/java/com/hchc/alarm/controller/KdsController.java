package com.hchc.alarm.controller;

import com.hchc.alarm.dao.hchc.BranchInfoBaseDao;
import com.hchc.alarm.dao.hchc.KdsOperationLogDao;
import com.hchc.alarm.dao.rocket.BranchKdsBaseDao;
import com.hchc.alarm.dao.rocket.KdsMessageBaseDao;
import com.hchc.alarm.entity.kds.TBranchKds;
import com.hchc.alarm.pack.biz.BranchInfo;
import com.hchc.alarm.pack.output.Output;
import com.hchc.alarm.pack.output.KdsConsoleInfo;
import com.hchc.alarm.service.RemoteService;
import com.hchc.alarm.util.DatetimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by wangrong 2020/5/12
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
    private BranchInfoBaseDao branchInfoDao;
    @Autowired
    private KdsOperationLogDao kdsOperationLogDao;

    @GetMapping("/errKdsInfo")
    public Output getErrorKdsInfo(int hqId, int branchId) {
        log.info("[getErrorKdsInfo] request params hqId:{}, branchId:{}", hqId, branchId);
        List<TBranchKds> kdsList = branchKdsDao.query(hqId, branchId);
        List<KdsConsoleInfo> errKdsInfoList = new ArrayList<>();
        List<KdsConsoleInfo> offLineKds = new ArrayList<>();
        List<KdsConsoleInfo> onLineKds = new ArrayList<>();
        List<String> kdsQueueOrders;
        KdsConsoleInfo kdsConsoleInfo;
        BranchInfo branchInfo;
        Date start = DatetimeUtil.dayBegin(new Date());
        Date end;
        try {
            for (TBranchKds kds : kdsList) {
                kdsConsoleInfo = new KdsConsoleInfo();
                kdsConsoleInfo.setWxCount(remoteService.getWxQueueCount(kds.getHqId(), kds.getBranchId()));
                end = new Date();
                kdsQueueOrders = kdsMessageDao.queryAllPushed(kds.getBranchId(), kds.getUuid(), start, end)
                        .stream()
                        .distinct() // 去重
                        .collect(Collectors.toList());
                log.info("[getErrorKdsInfo] branchId:{}, kdsQueueOrders:{}", kds.getBranchId(), kdsQueueOrders);
                if (kdsQueueOrders.size() != kdsConsoleInfo.getWxCount()) {
                    kdsConsoleInfo.setKdsCount(kdsQueueOrders.size());
                    kdsConsoleInfo.setUuid(kds.getUuid());
                    if (kds.getHeartTime() != null) {
                        kdsConsoleInfo.setHeartTime(kds.getHeartTime());
                    }
                    kdsConsoleInfo.setOffLine(checkOffLine(kds.getHeartTime()));
                    branchInfo = branchInfoDao.query(kds.getHqId(), kds.getBranchId());
                    kdsConsoleInfo.setBrandName(branchInfo.getBrandName());
                    kdsConsoleInfo.setBranchName(branchInfo.getBranchName());
                    kdsConsoleInfo.setVersionCode(kdsOperationLogDao.queryVersionCode(hqId, branchId, DatetimeUtil.format(start), DatetimeUtil.format(end)));
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
        return new Date().getTime() - DatetimeUtil.parse(heartTime).getTime() > 60000;
    }

}
