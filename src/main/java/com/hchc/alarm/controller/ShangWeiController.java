package com.hchc.alarm.controller;

import com.alibaba.fastjson.JSON;
import com.hchc.alarm.dao.hchc.ShangWeiMchDao;
import com.hchc.alarm.entity.ShangWeiMch;
import com.hchc.alarm.pack.Output;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping("update")
    public Output updateConfig(@RequestBody ShangWeiMch mch) {
        try {
            String data = JSON.toJSONString(mch);
            log.info("[updateConfig] recv mch:{}", data);
            if (shangWeiMchDao.update(mch.getHqId(), data)) {
                return Output.ok();
            }else {
                return Output.fail("update fail");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("updateConfig happen error:{}", e.getMessage());
            return Output.fail(e.getMessage());
        }
    }
}
