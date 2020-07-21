package com.hchc.alarm.controller;

import com.hchc.alarm.dao.hchc.BranchDao;
import com.hchc.alarm.pack.Output;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * @author wangrong
 * @date 2020-07-21
 */
@RestController
@RequestMapping("/branch")
@Slf4j
public class BranchController {

    @Autowired
    private BranchDao branchDao;

    @PostMapping("/updateFeatureData/{hqId}/{branchId}")
    public Output updateFeatureData(@PathVariable long hqId, @PathVariable long branchId, @RequestBody String data) {
        log.info("[updateFeatureData] recv hqId:{}, branchId:{}, data:{}", hqId, branchId, data);
        if (StringUtils.isEmpty(data)) {
            return Output.fail("data is empty");
        }
        try {
            if (branchDao.updateFeatureData(hqId, branchId, "DELIVERY", data) > 0) {
                return Output.ok();
            } else {
                return Output.fail("update fail");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("[updateFeatureData] happen error:{}", e.getMessage());
            return Output.fail(e.getMessage());
        }
    }
}
