package com.hchc.alarm.controller;

import com.hchc.alarm.dao.hchc.PricePolicyDao;
import com.hchc.alarm.pack.ResPack;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wangrong
 * @date 2020-09-16
 */
@RestController
@RequestMapping("price/policy")
@Slf4j
public class PricePolicyController {

    @Autowired
    private PricePolicyDao pricePolicyDao;

    @GetMapping("query/{hqId}/{branchId}")
    public ResPack queryPricePolicies(@PathVariable long hqId, @PathVariable long branchId) {
        log.info("[queryPricePolicies] recv hqId:{}, branchId:{}", hqId, branchId);
        try {
            return ResPack.ok(pricePolicyDao.queryAll(hqId, branchId));
        } catch (Exception e) {
            e.printStackTrace();
            return ResPack.fail(e.getMessage());
        }
    }
}
