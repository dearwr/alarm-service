package com.hchc.alarm.controller;

import com.alibaba.fastjson.JSON;
import com.hchc.alarm.dao.hchc.ShangWeiMchDao;
import com.hchc.alarm.entity.ShangWeiMch;
import com.hchc.alarm.pack.Output;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wangrong
 * @date 2020-08-22
 */
@RestController
@RequestMapping("shangwei")
@Slf4j
public class ShangWeiController {

    @Autowired
    private ShangWeiMchDao shangWeiMchDao;

    @PostMapping("updat")
    public Output updateConfig(ShangWeiMch mch) {
        log.info("[updateConfig] recv mch:{}", JSON.toJSONString(mch));
        try {
            shangWeiMchDao.update(mch.getHqId(), JSON.toJSONString(mch));
            return Output.ok();
        } catch (Exception e) {
            e.printStackTrace();
            log.info("updateConfig happen error:{}", e.getMessage());
            return Output.fail(e.getMessage());
        }
    }
}
