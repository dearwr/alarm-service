package com.hchc.alarm.pack;

import com.hchc.alarm.model.PushService;
import lombok.Data;

import java.util.List;

@Data
public class MallConsoleInfo {

    private String name;

    private String cityName;

    private List<PushService> services;

}
