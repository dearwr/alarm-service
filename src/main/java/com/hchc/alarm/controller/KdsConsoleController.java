package com.hchc.alarm.controller;

import com.hchc.alarm.dao.hchc.BranchDao;
import com.hchc.alarm.dao.hchc.BranchKdsBaseDao;
import com.hchc.alarm.entity.BranchKds;
import com.hchc.alarm.model.BranchBO;
import com.hchc.alarm.pack.KdsConsoleInfo;
import com.hchc.alarm.pack.Output;
import com.hchc.alarm.service.RemoteService;
import com.hchc.alarm.util.DatetimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangrong 2020/5/12
 *
 * @author wangrong
 */
@RestController
@RequestMapping("/kdsConsole")
@Slf4j
public class KdsConsoleController {

    @Autowired
    private BranchKdsBaseDao branchKdsDao;
    @Autowired
    private RemoteService remoteService;
    @Autowired
    private BranchDao branchDao;

    @GetMapping("/kdsInfo")
    public Output getKdsInfo(int hqId, int branchId) {
        log.info("[getKdsInfo] recv request params hqId:{}, branchId:{}", hqId, branchId);
        List<BranchKds> kdsList = branchKdsDao.query(hqId, branchId);
        List<KdsConsoleInfo> kdsConsoleInfos = new ArrayList<>();
        List<KdsConsoleInfo> offLineKds = new ArrayList<>();
        List<KdsConsoleInfo> onLineKds = new ArrayList<>();
        KdsConsoleInfo kdsConsoleInfo;
        BranchBO branchBO;
        try {
            for (BranchKds kds : kdsList) {
                if (kds.getHeartTime() == null) {
                    continue;
                }
                kdsConsoleInfo = new KdsConsoleInfo();
                kdsConsoleInfo.setHeartTime(kds.getHeartTime());
                kdsConsoleInfo.setOffLine(checkOffLine(kds.getHeartTime()));
                if (kds.isOpen()) {
                    kdsConsoleInfo.setWxCount(remoteService.getWxQueueCount(kds.getHqId(), kds.getBranchId()));
                    kdsConsoleInfo.setOpenState("开启");
                }else {
                    kdsConsoleInfo.setWxCount(0);
                    kdsConsoleInfo.setOpenState("关闭");
                }
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
        return offTime > 1000 * 60 ;
    }

}
