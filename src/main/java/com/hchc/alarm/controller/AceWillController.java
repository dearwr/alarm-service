package com.hchc.alarm.controller;

import com.alibaba.fastjson.JSON;
import com.hchc.alarm.pack.Output;
import com.hchc.alarm.pack.SyncDishLossReqPack;
import com.hchc.alarm.util.DatetimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.hchc.alarm.task.AceWillTask.AceWill_DISH_LOSS_URL;
import static com.hchc.alarm.task.AceWillTask.AceWill_DONE_URL;

/**
 * @author wangrong
 * @date 2020-07-16
 */
@RestController
@RequestMapping("acewill")
@Slf4j
public class AceWillController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("pushData")
    public String pushData(long hqId, String branches, String startDay, String endDay) throws ParseException {
        String methodName = "rePushAndDone";
        log.info("[{}] recv param hqId:{}, branches:{}, startDay:{}, endDay:{}", methodName, hqId, branches, startDay, endDay);
        List<String> branchIdList = Arrays.asList(branches.split(","));
        String pattern = "yyyyMMdd";
        Date startDate = DatetimeUtil.parse(startDay, pattern);
        Date endDate = DatetimeUtil.parse(endDay, pattern);
        String dayText;
        SyncDishLossReqPack syncPack;
        Output output;
        String response;
        while (startDate.getTime() <= endDate.getTime()) {
            dayText = DatetimeUtil.format(startDate, pattern);
            log.info("[{}] day->{} start push", methodName, dayText);
            for (String branchId : branchIdList) {
                try {
                    syncPack = new SyncDishLossReqPack(hqId, branchId, DatetimeUtil.format(startDate), DatetimeUtil.format(DatetimeUtil.dayEnd(startDate)));
                    output = restTemplate.postForEntity(AceWill_DISH_LOSS_URL, syncPack, Output.class).getBody();
                    if (output == null || !"0".equals(output.getCode())) {
                        log.info("[{}] day->{}, branchId:{} push dishLoss fail, result:{}", methodName, dayText, branchId, JSON.toJSON(output));
                    }else {
                        log.info("[{}] day->{}, branchId:{} push dishLoss success, result:{}", methodName, dayText, branchId, JSON.toJSON(output));
                    }
                    log.info("[{}] day->{}, branchId:{} push dishLoss success", methodName, dayText, branchId);
                    response = restTemplate.getForObject(AceWill_DONE_URL, String.class, hqId, String.valueOf(branchId), dayText, dayText);
                    if (!"ok".equals(response)) {
                        log.info("[{}] day->{}, branchId:{} rePushAndDone fail, result:{}", methodName, dayText, branchId, response);
                        return "day->" + dayText + ", branchId:" + branchId + " rePushAndDone fail, result:" + response;
                    } else {
                        log.info("[{}] day->{}, branchId:{} rePushAndDone success", methodName, dayText, branchId);
                    }
                } catch (Exception e) {
                    log.info("[{}] day->{}, branchId:{} push fail, error:{}", methodName, dayText, branchId, e.getMessage());
                    return "day->" + dayText + ", branchId:" + branchId + ", push happen error:" + e.getMessage();
                }
            }
            log.info("[{}] day->{} end push", methodName, dayText);
            startDate = DatetimeUtil.addDay(startDate, 1);
        }
        return "ok";
    }

}
