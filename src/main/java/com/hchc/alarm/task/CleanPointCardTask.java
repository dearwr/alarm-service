//package com.hchc.alarm.task;
//
//import com.hchc.alarm.dao.hchc.VipPointDao;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//import org.springframework.util.StringUtils;
//
//import java.util.*;
//
///**
// * @author wangrong
// * @date 2020-06-17
// */
//@Service
//@Slf4j
//public class CleanPointCardTask {
//
//    private static final int QUERY_SIZE = 10000;
//
//    @Autowired
//    private VipPointDao vipPointDao;
//
//    @Scheduled(cron = " 0 21 18 * * ? ")
//    public void cleanPointCard() throws InterruptedException {
//        log.info("**************start clean Point Card*********");
//        boolean hasNumber = true;
//        int count = 1;
//        int currentCount;
//        int start = 0;
//        List<String> numberList;
//        List<String[]> pointCards;
//        List<String> idList;
//        Set<String> exitSet;
//        while (hasNumber) {
//            currentCount = count++;
//            numberList = vipPointDao.queryNumbers(start, QUERY_SIZE);
//            log.info("第{}次处理数据{}开始", currentCount, numberList.size());
//            if (numberList.size() < QUERY_SIZE) {
//                hasNumber = false;
//            }
//            for (String vimNumber : numberList) {
//                pointCards = vipPointDao.queryPointCardsByNumber(vimNumber);
//                exitSet = new HashSet<>(pointCards.size() << 1);
//                idList = new ArrayList<>();
//                for (String[] obj : pointCards) {
//                    if (!exitSet.add(obj[1])) {
//                        idList.add(obj[0]);
//                    }
//                }
//                if (idList.isEmpty()) {
//                    continue;
//                }
//                log.info("处理会员{}的数据{}", vimNumber, String.join(",", idList));
//                int result = vipPointDao.updatePointCardInvalid(idList);
//                if (result != idList.size()) {
//                    log.info("处理会员{}的数据不成功", vimNumber);
//                }
//            }
//            log.info("第{}次处理数据{}结束", currentCount, numberList.size());
//            start += QUERY_SIZE;
//            Thread.sleep(500);
//        }
//        log.info("**************end clean Point Card*********");
//    }
//
//}
