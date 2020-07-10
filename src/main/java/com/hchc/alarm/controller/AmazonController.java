package com.hchc.alarm.controller;

import com.hchc.alarm.dao.hchc.BranchDao;
import com.hchc.alarm.pack.Output;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wangrong
 * @date 2020-07-10
 */
@RestController
@RequestMapping("amazon")
@Slf4j
public class AmazonController {

    @Autowired
    private BranchDao branchDao;

    @GetMapping("branch/changeCode")
    public Output changeBranchCode(int branchId, String code) {
        try {
            if (branchDao.changeCode(branchId, code) != 1) {
                return Output.fail("change Fail");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("[changeBranchCode] happen error:{}", e.getMessage());
            return Output.fail(e.getMessage());
        }
        return Output.ok();
    }
}
