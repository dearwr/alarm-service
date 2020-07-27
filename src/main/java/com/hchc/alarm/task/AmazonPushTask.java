//package com.hchc.alarm.task;
//
//import com.alibaba.fastjson.JSON;
//import com.hchc.alarm.util.DatetimeUtil;
//import lombok.Data;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.Date;
//
///**
// * @author wangrong
// * @date 2020-07-09
// */
//@Service
//@Slf4j
//public class AmazonPushTask {
//
//    @Autowired
//    private RestTemplate restTemplate;
//
//    private final String PUSH_DATA_URL = "http://120.78.232.8:9503/amazon/schedule/pushAllData/{1}?branchIds={2}&start={3}&end={4}";
//
//    // amazon品牌号
//    private final String AMAZON_HQID = "2382";
//    //  门店
//    private Integer[] branchIds = new Integer[]{3127, 4971};
//
//    @Scheduled(cron = " 0 35 1 * * ? ")
//    public void pushData() {
//        log.info("##################### amazon push data ########################");
//        Date end = DatetimeUtil.getDayStart(new Date());
//        Date start = DatetimeUtil.addDay(end, -1);
//        Msg msgList;
//        for (int i = 0; i < branchIds.length; i++) {
//            log.info("[amazon] start push branch :{}", branchIds[i]);
//            msgList = restTemplate.getForObject(PUSH_DATA_URL, Msg.class, AMAZON_HQID, branchIds[i], DatetimeUtil.format(start), DatetimeUtil.format(end));
//            log.info("[amazon] end push branch :{}, result => {}", branchIds[i], JSON.toJSONString(msgList));
//        }
//    }
//
//    @Data
//    private static class Msg {
//        private int code;
//        private String message;
//        private Object data;
//    }
//}
