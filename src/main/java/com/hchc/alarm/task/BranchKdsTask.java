//package com.hchc.alarm.task;
//
//import com.hchc.alarm.dao.hchc.BranchKdsBaseDao;
//import com.hchc.alarm.dao.hchc.KdsOrderDao;
//import com.hchc.alarm.util.DatetimeUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import java.util.Date;
//import java.util.List;
//
//@Service
//@Slf4j
//public class BranchKdsTask {
//
//    @Autowired
//    private BranchKdsBaseDao branchKdsDao;
//    @Autowired
//    private KdsOrderDao kdsOrderDao;
//
//    @Scheduled(cron = "0 50 14 * * ?")
//    public void checkKdsOrders() {
//        log.info("[checkKdsOrders] start check");
//        List<Integer[]> infoList = branchKdsDao.queryCheckInfos();
//        Date startTime = DatetimeUtil.dayBegin(new Date());
//        Date endTime = DatetimeUtil.addMinute(new Date(), -35);
//        for (Integer[] info : infoList) {
//            log.info("[checkKdsOrders] complete order kds fId:{}, hqId:{}, branch:{}", info[0], info[1], info[2]);
//            kdsOrderDao.orderComplete(info[2], startTime, endTime);
//        }
//    }
//}
