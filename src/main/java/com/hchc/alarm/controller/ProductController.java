package com.hchc.alarm.controller;

import com.hchc.alarm.dao.hchc.ProductDao;
import com.hchc.alarm.pack.Output;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wangrong
 * @date 2020-07-01
 */
@RestController
@RequestMapping("product")
@Slf4j
public class ProductController {

    @Autowired
    private ProductDao productDao;

    @GetMapping("disable")
    public Output disable(long productId){
        log.info("[disable] recv param productId:{}", productId);
        try {
            productDao.disable(productId);
            return Output.ok();
        } catch (Exception e) {
            log.info("[disable] happen error :{}", e.getMessage());
            return Output.fail(e.getMessage());
        }
    }
}
