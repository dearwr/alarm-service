package com.hchc.alarm.task;

import com.alibaba.fastjson.JSON;
import com.hchc.alarm.pack.Output;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author wangrong
 * @date 2020-08-20
 */
@Service
@Slf4j
public class FengNiaoTokenTask {

    @Autowired
    private RestTemplate restTemplate;
    private static final String REFRESH_TOKEN_URL = "https://delivery.51hchc.com/fengniao/token/fresh";

    @Scheduled(cron = " 0 0 6,23 * * ? ")
    public void refreshFengNiaoToken() {
        log.info("[refreshFengNiaoToken]->start ");
        try {
            Output output = restTemplate.getForObject(REFRESH_TOKEN_URL, Output.class);
            log.info("[refreshFengNiaoToken] result:{}", JSON.toJSONString(output));
            assert output != null;
            if ("0".equals(output.getCode())) {
                log.info("[refreshFengNiaoToken] success");
            } else {
                log.info("[refreshFengNiaoToken] fail");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("[refreshFengNiaoToken] error:{}", e.getMessage());
        }

    }

}
