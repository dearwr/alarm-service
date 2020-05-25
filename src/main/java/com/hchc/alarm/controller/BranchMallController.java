package com.hchc.alarm.controller;

import com.alibaba.fastjson.JSON;
import com.hchc.alarm.constant.MallConstant;
import com.hchc.alarm.model.BranchInfo;
import com.hchc.alarm.pack.Output;
import com.hchc.alarm.service.BranchMallService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by wangrong 2020/5/18
 */
@RestController
@RequestMapping("/mallConsole")
@Slf4j
public class BranchMallController {

    @Autowired
    private BranchMallService branchMallService;

    @GetMapping("/mallInfos")
    public Output queryMallConsoleInfos() {
        log.info("[queryMallConsoleInfos]");
        return Output.ok(branchMallService.queryMallConsoleInfos());
    }

    @PostMapping("/pushBranchInfos")
    public Output pushBranchInfos(@RequestBody List<BranchInfo> branchInfos) {
        log.info("[pushBranchInfos] params :{}", JSON.toJSONString(branchInfos));
        if (branchInfos != null) {
            MallConstant.FLIP_MALL_BRANCH_DATA = branchInfos;
        }
        return Output.ok();
    }
}
