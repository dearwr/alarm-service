package com.hchc.alarm.controller;

import com.hchc.alarm.model.niceconsole.PayProviderBO;
import com.hchc.alarm.pack.Output;
import com.hchc.alarm.service.PayProviderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author wangrong
 * @date 2020-06-29
 */
@CrossOrigin
@RestController
@RequestMapping("pay-provider")
@Slf4j
public class PayProviderController {

    @Autowired
    private PayProviderService payProviderService;

    @PostMapping("batchSave")
    public Output batchSave(@RequestBody PayProviderBO payProvider){
        payProviderService.batchSave(payProvider);
        return Output.ok();
    }
}
