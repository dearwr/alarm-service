package com.hchc.alarm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Created by wangrong 2020/5/13
 */
@Controller
public class PageController {

    @GetMapping("/")
    public String toMain() {
        return "index";
    }
}
