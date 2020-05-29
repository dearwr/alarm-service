package com.hchc.alarm.controller;

import com.alibaba.fastjson.JSON;
import com.hchc.alarm.model.BranchCheckBO;
import com.hchc.alarm.pack.Output;
import com.hchc.alarm.service.MallCheckService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author wangrong
 * @date 2020-05-28
 */
@RestController
@RequestMapping("/mallCheck")
@Slf4j
public class MallCheckController {

    @Autowired
    private MallCheckService mallCheckService;

    @PostMapping("/checkOne")
    public Output checkOne(@RequestBody BranchCheckBO branchCheckBO) {
        log.info("[checkOne] param:{}", JSON.toJSONString(branchCheckBO));
        return Output.ok(mallCheckService.checkDataAndSaveToFile(branchCheckBO));
    }

    @PostMapping("/queryCheckData")
    public Output queryCheckData(int hqId, int branchId, int month) {
        log.info("[queryCheckData] param hqId:{}, branchId:{}, month:{}", hqId, branchId, month);
        // todo
        return null;
    }

}
