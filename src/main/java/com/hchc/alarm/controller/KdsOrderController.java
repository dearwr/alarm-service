package com.hchc.alarm.controller;

import com.hchc.alarm.dao.hchc.KdsOrderDao;
import com.hchc.alarm.pack.Output;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wangrong
 */
@RestController
@RequestMapping("kds")
@Slf4j
public class KdsOrderController {

    @Autowired
    private KdsOrderDao kdsOrderDao;

    @GetMapping("/order/complete")
    public Output orderComplete(int branchId, String startTime, String endTime) {
        log.info("[orderComplete] recv param branchId:{}, startTime:{}, endTime:{}", branchId, startTime, endTime);
        return Output.ok(kdsOrderDao.orderComplete(branchId, startTime, endTime));
    }

}
