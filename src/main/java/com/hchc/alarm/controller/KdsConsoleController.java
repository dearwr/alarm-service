package com.hchc.alarm.controller;

import com.hchc.alarm.dao.hchc.BranchInfoBaseDao;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Created by wangrong 2020/5/12
 */
@RestController
@RequestMapping("/kdsConsole")
@Slf4j
public class KdsConsoleController {

    @Autowired
    private BranchKdsBaseDao branchKdsDao;
    @Autowired
    private KdsMessageBaseDao kdsMessageDao;
    @Autowired
    private RemoteService remoteService;
    @Autowired
    private BranchInfoBaseDao branchInfoDao;

    @GetMapping("/errKdsInfo")
    public Output getErrorKdsInfo(int hqId, int branchId) {
        log.info("[getErrorKdsInfo] params hqId:{}, branchId:{}", hqId, branchId);
        List<TBranchKds> kdsList = branchKdsDao.query(hqId, branchId);
        List<KdsConsoleInfo> errKdsInfoList = new ArrayList<>();
        List<String> kdsQueueOrders = new ArrayList<>();
        KdsConsoleInfo kdsConsoleInfo;
        BranchInfo branchInfo;
        Date start;
        Date end;
        try {
            for (TBranchKds kds : kdsList) {
                kdsConsoleInfo = new KdsConsoleInfo();
                end = new Date();
                start = DatetimeUtil.dayBegin(end);
                kdsConsoleInfo.setWxCount(remoteService.getWxQueueCount(kds.getHqId(), kds.getBranchId()));
                kdsQueueOrders = kdsMessageDao.queryAllPushed(kds.getBranchId(), kds.getUuid(), start, end)
                        .stream()
                        .distinct() // 去重
                        .collect(Collectors.toList());
                log.info("[getErrorKdsInfo] kdsQueueOrders:{}", kdsQueueOrders);
                kdsConsoleInfo.setKdsCount(kdsQueueOrders.size());
                if (kdsConsoleInfo.getKdsCount() != kdsConsoleInfo.getWxCount()) {
                    branchInfo = branchInfoDao.query(kds.getHqId(), kds.getBranchId());
                    kdsConsoleInfo.setBrandName(branchInfo.getBrandName());
                    kdsConsoleInfo.setBranchName(branchInfo.getBranchName());
                    kdsConsoleInfo.setUuid(kds.getUuid());
                    errKdsInfoList.add(kdsConsoleInfo);
                }
            }
        } catch (Exception e) {
            log.info("[getErrorKdsInfo] happen exception :{}", e.getMessage());
            return Output.fail(e.getMessage());
        }
        return Output.ok(errKdsInfoList);
    }
}
