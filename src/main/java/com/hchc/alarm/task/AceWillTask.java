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

    @Autowired
    private RestTemplate restTemplate;

//    public static final String AceWill_DISH_LOSS_URL = "http://120.78.232.8:9500/sync/dishLoss";
//    public static final String AceWill_PUSH_URL = "http://120.78.232.8:9500/sync/push?hqId={1}&branchId={2}&date={3}";
//    public static final String AceWill_DAY_DONE_URL = "http://120.78.232.8:9500/sync/done?hqId={1}&branchId={2}&date={3}";

    public static final String AceWill_DISH_LOSS_URL = "http://localhost:9500/sync/dishLoss";
    public static final String AceWill_PUSH_URL = "http://localhost:9500/sync/push?hqId={1}&branchId={2}&date={3}";
    public static final String AceWill_DAY_DONE_URL = "http://localhost:9500/sync/done?hqId={1}&branchId={2}&date={3}";

//    @Scheduled(cron = "0 50 22 * * ?")
//    public void scientist() throws ParseException {
//        long hqId = 1932L;
//        String branches = "2480,2481,2482,2483,2484,2485,4907,5059,5450,5451";
//        String hqName = "scientist";
//        String startDay = DatetimeUtil.dayText(new Date());
//        syncDishLoss(hqId, branches, hqName, startDay);
//    }

//    @Scheduled(cron = "0 20 1 * * ?")
//    public void zhuYe() throws ParseException {
//        long hqId = 3558L;
//        String branches = "5376,5377,5378,5379,5380,5381,5382,5383,5384,5385,5386,5387,6875";
//        String hqName = "zhuye";
//        String startDay = DatetimeUtil.dayText(DatetimeUtil.addDay(new Date(), -1));
//        syncDishLoss(hqId, branches, hqName, startDay);
//    }

    @Scheduled(cron = "0 10 1 * * ?")
    public void zhuYe() {
        long hqId = 1516;
        List<Long> branchIds = Arrays.asList(6731L);
        Date startDay = DatetimeUtil.addDay(new Date(), -1);
        branchIds.forEach(branchId -> syncOrders(hqId, branchId, startDay));
    }

    private void syncDishLoss(long hqId, String branches, String hqName, String startDay) throws ParseException {
        String[] branchList = branches.split(",");
        SyncDishLossReqPack syncPack;
        Date startDate = DatetimeUtil.parseDayText(startDay);
        Output result;
        for (String branchId : branchList) {
            syncPack = new SyncDishLossReqPack(hqId, branchId, DatetimeUtil.format(startDate), DatetimeUtil.format(DatetimeUtil.dayEnd(startDate)));
            result = restTemplate.postForEntity(AceWill_DISH_LOSS_URL, syncPack, Output.class).getBody();
            if (result == null || !"0".equals(result.getCode())) {
                log.info("{} {} syncDishLoss fail, result:{}", hqName, branchId, JSON.toJSON(result));
            } else {
                log.info("{} {} syncDishLoss success, result:{}", hqName, branchId, JSON.toJSON(result));
            }
        }
    }

    private void syncOrders(long hqId, long branchId, Date startDate) {
        String methodName = "syncOrders";
        String dayText = DatetimeUtil.dayText(startDate);
        try {
            Output output = restTemplate.getForObject(AceWill_PUSH_URL, Output.class, hqId, branchId, DatetimeUtil.format(startDate));
            if (output == null || !"0".equals(output.getCode())) {
                log.info("[{}] day->{}, {} fail, result:{}", methodName, dayText, branchId, JSON.toJSONString(output));
            } else {
                log.info("[{}] day->{}, {} success, result:{}", methodName, dayText, branchId, JSON.toJSON(output));
            }
        } catch (Exception e) {
            log.info("[{}] day->{}, {} happen error :{}", methodName, dayText, branchId, e.getMessage());
        }
    }

}
