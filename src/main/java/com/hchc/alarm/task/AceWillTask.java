package com.hchc.alarm.task;

import com.alibaba.fastjson.JSON;
import com.hchc.alarm.pack.Output;
import com.hchc.alarm.pack.SyncDishLossReqPack;
import com.hchc.alarm.util.DatetimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
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

    public static final String AceWill_DISH_LOSS_URL = "http://120.78.232.8:9500/sync/dishLoss";
    public static final String AceWill_PUSH_URL = "http://120.78.232.8:9500/sync/push?hqId={1}&branchId={2}&date={3}";
    public static final String AceWill_DAY_DONE_URL = "http://120.78.232.8:9500/sync/done?hqId={1}&branchId={2}&date={3}";

//    public static final String AceWill_DISH_LOSS_URL = "http://localhost:9500/sync/dishLoss";
//    public static final String AceWill_PUSH_URL = "http://localhost:9500/sync/push?hqId={1}&branchId={2}&date={3}";
//    public static final String AceWill_DAY_DONE_URL = "http://localhost:9500/sync/done?hqId={1}&branchId={2}&date={3}";

    @Scheduled(cron = "0 55 22 * * ?")
    public void scientist() throws ParseException {
        long hqId = 1932L;
        String branches = "2480,2481,2482,2483,2484,2485,4907,5059";
        String hqName = "scientist";
        String startDay = DatetimeUtil.dayText(new Date());
        String endDay = startDay;
        doSync(hqId, branches, hqName, startDay, endDay);
    }

    private void doSync(long hqId, String branches, String hqName, String startDay, String endDay) throws ParseException {
        String[] branchList = branches.split(",");
        SyncDishLossReqPack syncPack;
        Date startDate = DatetimeUtil.parseDayText(startDay);
        Output result;
        for (String branchId : branchList) {
            syncPack = new SyncDishLossReqPack(hqId, branchId, DatetimeUtil.format(startDate), DatetimeUtil.format(DatetimeUtil.dayEnd(startDate)));
            result = restTemplate.postForEntity(AceWill_DISH_LOSS_URL, syncPack, Output.class).getBody();
            if (result == null || !"0".equals(result.getCode())) {
                log.info("branchId:{} push dishLoss fail, result:{}", branchId, JSON.toJSON(result));
            }else {
                log.info("branchId:{} push dishLoss success, result:{}", branchId, JSON.toJSON(result));
            }
        }
    }

}
