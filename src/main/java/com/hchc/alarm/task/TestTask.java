//package com.hchc.alarm.task;
//
//import com.hchc.alarm.dao.hchc.TestDao;
//import com.hchc.alarm.util.JsonUtils;
//import lombok.Data;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.sql.Timestamp;
//import java.util.*;
//
///**
// * @author wangrong
// * @date 2020-10-13
// */
//@Slf4j
//@Service
//public class TestTask {
//
//    @Autowired
//    private TestDao testDao;
//
//    @Scheduled(cron = "0 46 12 * * ?")
//    public void scientist() {
//        List<POrder> allOrders;
//        List<COrder> receivedOrders;
//        String abbDate;
//        for (int startAbbDate = 20200901; startAbbDate <= 20200930; startAbbDate++) {
//            abbDate = String.valueOf(startAbbDate);
//            abbDate = abbDate.substring(0, 4) + "-" + abbDate.substring(4, 6) + "-" + abbDate.substring(6);
//            allOrders = testDao.queryAllOrders(4885, abbDate);
//            abbDate = "2020-9-" + ('0' == abbDate.charAt(8) ? abbDate.charAt(9) : abbDate.substring(8));
//            receivedOrders = testDao.queryReceived(abbDate);
//            boolean exist;
//            List<POrder> unPushOrders = new ArrayList<>();
//            List<COrder> removeOrders;
//
//            for (POrder p : allOrders) {
//
//                removeOrders = new ArrayList<>();
//                exist = false;
//                for (COrder c : receivedOrders) {
//                    if (c.getDealTime().getTime() > p.getCreated().getTime()) {
//                        break;
//                    }
//                    if (p.getCreated().getTime() == c.getDealTime().getTime() && p.getPrice().compareTo(c.getMoney()) == 0) {
//                        exist = true;
//                        removeOrders.add(c);
//                        testDao.update(c.getId(), p);
//                    }
//                }
//                if (exist) {
//                    receivedOrders.removeAll(removeOrders);
//                }else {
//                    unPushOrders.add(p);
//                    testDao.add(p, abbDate);
//                }
//            }
//            log.info("未上传订单：" + JsonUtils.toJson(unPushOrders));
//        }
//    }
//
//    @Data
//    public static class POrder {
//        private String bill;
//        private Timestamp created;
//        private BigDecimal price;
//    }
//
//    @Data
//    public static class COrder {
//        private int id;
//        private Timestamp dealTime;
//        private BigDecimal money;
//    }
//
//}
