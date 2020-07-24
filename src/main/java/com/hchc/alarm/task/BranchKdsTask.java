package com.hchc.alarm.task;

import com.hchc.alarm.dao.hchc.BranchKdsBaseDao;
import com.hchc.alarm.service.RemoteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class BranchKdsTask {

    @Autowired
    private BranchKdsBaseDao branchKdsDao;
    @Autowired
    private RemoteService remoteService;

    @Scheduled(cron = "0 */10 * * * ?")
    public void checkKdsOrders() {
        log.info("[checkKdsOrders] start check");
        List<Integer[]> infoList = branchKdsDao.queryCheckInfos();
        int size;
        for (Integer[] info : infoList) {
            size = remoteService.getWxQueueCount(info[1], info[2]);
            if (size >= 15 && info[3] == 1) {
                log.info("[checkKdsOrders] close kds fId:{}, hqId:{}, branch:{}", info[0], info[1], info[2]);
                branchKdsDao.updateOpenState(info[0], 0);
            } else if (size < 15 && info[3] == 0) {
                log.info("[checkKdsOrders] open kds fId:{}, hqId:{}, branch:{}", info[0], info[1], info[2]);
                branchKdsDao.updateOpenState(info[0], 1);
            }
        }
    }
}
