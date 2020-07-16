package com.hchc.alarm.task;

import com.hchc.alarm.pack.Output;
import com.hchc.alarm.util.DatetimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

/**
 * @author wangrong
 * @date 2020-07-13
 */
@Service
@Slf4j
public class AceWillTask {

    @Autowired
    private RestTemplate restTemplate;

    public static final String AceWill_DISH_URL = "http://120.78.232.8:9500/sync/basic?hqId={}&type=dishkind,dishunit,dish,menudish";
    public static final String AceWill_DISH_LOSS_URL = "http://120.78.232.8:9500/sync/dishLoss";
    public static final String AceWill_DONE_URL = "http://120.78.232.8:9500/sync/rePushAndDone?hqId={1}&branches={2}&startDay={3}&endDay={4}";


    @Scheduled(cron = "0 50 23 * * ?")
    public void scientist() {
        long hqId = 1932L;
        String branches = "2480,2481,2482,2483,2484,2485,4907,5059";
        String hqName = "scientist";
        String startDay = DatetimeUtil.dayText(new Date());
        String endDay = startDay;
        doSync(hqId, branches, hqName, startDay, endDay);
    }

    private void doSync(long hqId, String branches, String hqName, String startDay, String endDay) {
        log.info("{} start push dish", hqName);
        Output result = restTemplate.getForObject(AceWill_DISH_URL, Output.class, hqId);
        if (result != null && "0".equals(result.getCode())) {
            log.info("{} push dish success", hqName);
        } else {
            log.info("{} push dish fail, result :{}", hqName, result);
            return;
        }
        log.info("{} start push order and done", hqName);
        String response = restTemplate.getForObject(AceWill_DONE_URL, String.class, hqId, branches, startDay, endDay);
        if (response != null && "ok".equals(response)) {
            log.info("{} push order and done success", hqName);
        } else {
            log.info("{} push order and done fail, result:{}", hqName, response);
        }
    }

}
