package com.hchc.alarm.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hchc.alarm.dao.hchc.BranchDao;
import com.hchc.alarm.dao.hchc.ErpConfigDao;
import com.hchc.alarm.dao.hchc.SqlDao;
import com.hchc.alarm.pack.Output;
import com.hchc.alarm.pack.SyncDishLossReqPack;
import com.hchc.alarm.util.DatetimeUtil;
import com.hchc.alarm.util.JsonUtils;
import com.hchc.alarm.util.StringUtil;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.hchc.alarm.task.AceWillTask.*;

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
    @Autowired
    private SqlDao sqlDao;
    @Autowired
    private BranchDao branchDao;
    @Autowired
    private ErpConfigDao erpConfigDao;

    @PostMapping("sql/execute")
    public String ChangeAceWillUrl(String sql) {
        if (StringUtils.isEmpty(sql)) {
            return "sql is empty";
        }
        try {
            sqlDao.execute(sql);
            return "ok";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

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
        while (startDate.getTime() <= endDate.getTime()) {
            dayText = DatetimeUtil.format(startDate, pattern);
            log.info("[{}] day->{} start push", methodName, dayText);
            for (String branchId : branchIdList) {
                try {
                    syncPack = new SyncDishLossReqPack(hqId, branchId, DatetimeUtil.format(startDate), DatetimeUtil.format(DatetimeUtil.dayEnd(startDate)));
                    output = restTemplate.postForEntity(AceWill_DISH_LOSS_URL, syncPack, Output.class).getBody();
                    if (output == null || !"0".equals(output.getCode())) {
                        log.info("[{}] day->{}, branchId:{} push dishLoss fail, result:{}", methodName, dayText, branchId, JSON.toJSONString(output));
                    }else {
                        log.info("[{}] day->{}, branchId:{} push dishLoss success, result:{}", methodName, dayText, branchId, JSON.toJSONString(output));
                    }
                    output = restTemplate.getForObject(AceWill_PUSH_URL, Output.class, hqId, branchId, DatetimeUtil.format(startDate));
                    if (output == null || !"0".equals(output.getCode())) {
                        log.info("[{}] day->{}, branchId:{} push order fail, result:{}", methodName, dayText, branchId, JSON.toJSONString(output));
                        return "day->" + dayText + ", branchId:" + branchId + " push order fail, result:" + JSON.toJSONString(output);
                    }else {
                        log.info("[{}] day->{}, branchId:{} push order success, result:{}", methodName, dayText, branchId, JSON.toJSON(output));
                    }
                    output = restTemplate.getForObject(AceWill_DAY_DONE_URL, Output.class, hqId, branchId, DatetimeUtil.format(startDate));
                    if (output == null || !"0".equals(output.getCode())) {
                        log.info("[{}] day->{}, branchId:{} push day done fail, result:{}", methodName, dayText, branchId, JSON.toJSONString(output));
                        return "day->" + dayText + ", branchId:" + branchId + " push day done fail, result:" + JSON.toJSONString(output);
                    }else {
                        log.info("[{}] day->{}, branchId:{} push day done success, result:{}", methodName, dayText, branchId, JSON.toJSON(output));
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

    @PostMapping("addErpShopConfig")
    public Output addErpShopConfig(@RequestBody WagasErpConfig erpConfig) {
        log.info("[addErpShopConfig] rec erpConfig:{}", JsonUtils.toJson(erpConfig));
        long hqId = Long.parseLong(erpConfig.getHqId());
        String erpName = erpConfig.getErpName();
        List<Long> branchIds = new ArrayList<>();
        if (StringUtil.isBlank(erpConfig.getBranches())) {
            branchIds = branchDao.queryBranchIds(hqId);
        }else {
            List<String> branches = Arrays.asList(erpConfig.getBranches().split(","));
            for (String id : branches) {
                branchIds.add(Long.valueOf(id));
            }
        }
        erpConfig.setHqId(null);
        erpConfig.setErpName(null);
        String config = JsonUtils.toJson(erpConfig);
        log.info("[addErpShopConfig] config is :{}", config);
        try {
            for (Long bId : branchIds) {
                if (erpConfigDao.queryExist(hqId, bId, erpName)) {
                    continue;
                }
                erpConfigDao.add(hqId, bId, erpName, config);
            }
            return Output.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return Output.fail(e.getMessage());
        }
    }

    @Setter
    @Getter
    private static class WagasErpConfig {

        /**
         * 门店编号
         */
        private String url;
        private Service service;
        private DataKey dataKey;

        private String hqId;
        private String erpName;
        private String branches;

    }

    @Setter
    @Getter
    private static class Service {

        /**
         * 固定值
         */
        private String prod = "T100";

        /**
         * 服务名
         */
        private String name;

        /**
         * 服务名
         */
        private String ip;

        /**
         * 环境名
         */
        private String id;

    }

    @Data
    @JsonAutoDetect(getterVisibility=JsonAutoDetect.Visibility.NONE)
    private static class DataKey {

        /**
         * 企业编号
         */
        @JSONField(name = "EntId")
        @JsonProperty(value = "EntId", required = true)
        private String EntId;

        /**
         * 公司
         */
        @JSONField(name = "CompanyId")
        @JsonProperty(value = "CompanyId", required = true)
        private String CompanyId;

    }

}
