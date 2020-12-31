package com.hchc.alarm.controller;

import com.alibaba.fastjson.JSON;
import com.hchc.alarm.dao.hchc.MallRecordDao;
import com.hchc.alarm.model.BranchCheckBO;
import com.hchc.alarm.pack.Output;
import com.hchc.alarm.service.MallCheckService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

/**
 * @author wangrong
 * @date 2020-05-28
 */
@RestController
@RequestMapping("/mallCheck")
@Slf4j
public class MallCheckController {

    @Autowired
    private MallRecordDao mallRecordDao;
    @Autowired
    private MallCheckService mallCheckService;

    @PostMapping("/checkOne")
    public Output checkOne(@RequestBody BranchCheckBO branchCheckBO) {
        log.info("[checkOne] param:{}", JSON.toJSONString(branchCheckBO));
        return Output.ok(mallCheckService.saveFile(branchCheckBO, mallRecordDao.queryPushFailOrders(branchCheckBO)));
    }

    @PostMapping("/queryCheckInfos")
    public Output queryCheckInfos(int hqId, int branchId, String year, String month) {
        log.info("[queryCheckInfos] recv param {} {} {} {}", hqId, branchId, year, month);
        BranchCheckBO checkBO = new BranchCheckBO();
        checkBO.setHqId(hqId);
        checkBO.setBranchId(branchId);
        checkBO.setStartText(year + month);
        File recordFile = mallCheckService.getRecordFile(checkBO);
        if (!recordFile.exists()) {
            return Output.fail("未找到文件");
        }
        return Output.ok(mallCheckService.parseFile(recordFile));
    }

}
