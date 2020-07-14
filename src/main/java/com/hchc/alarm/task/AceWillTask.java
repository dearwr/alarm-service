package com.hchc.alarm.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.DateUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author wangrong
 * @date 2020-07-13
 */
@Service
@Slf4j
public class AceWillTask {

    private static final String AceWill_URL = "http://120.25.75.153:9500/";


//    @Scheduled(cron = "0 10 23 * * ?")
//    public void scientist() {
//        AceWillPushUtil.sync(1932L);
//    }
//
//    @Scheduled(cron = "0 0 23 * * ?")
//    public void upload2305() {
//        long hqId = 1932;
//        List<Long> branchIds = Arrays.asList(2480L, 2481L, 2482L, 2483L, 2484L, 2485L, 4907L, 5059L);
//
//        Date date = new Date();
//        String today = DateUtils.format(date, "yyyy-MM-dd");
//        AceWillPushUtil.upload("Scientist", hqId, branchIds, today);
//    }
}
