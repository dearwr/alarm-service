package com.hchc.alarm.task;

import com.hchc.alarm.pack.Output;
import com.hchc.alarm.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author wangrong
 * @date 2020-11-13
 */
@Service
@Slf4j
public class SyncMenuTask {

    @Autowired
    private RestTemplate restTemplate;

    private final String PUSH_DATA_URL = "http://47.112.150.247:8013/menu/allBranch/sync/3880";

    @Scheduled(cron = " 0 35 7,8,9,10,11,12,13,14,15,16,17,18,19,20,21 * * ? ")
    public void syncMenu() {
        log.info("##################### Sync menu task ########################");
        log.info("[Sync menu] start");
        Output result = restTemplate.getForObject(PUSH_DATA_URL, Output.class);
        log.info("[Sync menu] result :{}", JsonUtils.toJson(result));
        if (result == null || !"0".equals(result.getCode())) {
            log.info("[Sync menu] fail");
        } else {
            log.info("[Sync menu] success");
        }
        log.info("[Sync menu] end");
    }
}
