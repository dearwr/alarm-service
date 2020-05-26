package com.hchc.alarm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Created by wangrong 2020/5/13
 * @author wangrong
 */
@Controller
public class PageController {

    @GetMapping("/kds")
    public String toMain() {
        return "kds";
    }

    @GetMapping("/mall")
    public String toMall() {
        return "mall";
    }
}
