package com.hchc.alarm.controller;

import com.hchc.alarm.pack.Output;
import com.hchc.alarm.service.BranchMallService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by wangrong 2020/5/18
 * @author wangrong
 */
@RestController
@RequestMapping("/mallConsole")
@Slf4j
public class MallConsoleController {

    @Autowired
    private BranchMallService branchMallService;

    @GetMapping("/mallInfos")
    public Output queryMallConsoleInfos() {
        log.info("[queryMallConsoleInfos]");
        return Output.ok(branchMallService.queryMallConsoleInfos());
    }

}
