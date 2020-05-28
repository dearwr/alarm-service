package com.hchc.alarm.dao.hchc;

import com.alibaba.fastjson.JSON;
import com.hchc.alarm.model.BranchCheckBO;
import com.hchc.alarm.pack.Output;
import com.hchc.alarm.service.MallRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author wangrong
 * @date 2020-05-28
 */
@RestController
@RequestMapping("/mall")
@Slf4j
public class TestController {

    @Autowired
    private MallRecordService mallRecordService;

    @PostMapping("/check")
    public Output mallCheck(@RequestBody BranchCheckBO branchCheckBO) {
        log.info("[mallCheck] param:{}", JSON.toJSONString(branchCheckBO));
        String result = mallRecordService.checkMallDataAndSave(branchCheckBO);
        if ("suc".equals(result)) {
            return Output.ok();
        }else {
            return Output.fail(result);
        }
    }
}
