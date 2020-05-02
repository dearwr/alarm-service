package com.hchc.alarm.pack;

import com.hchc.alarm.model.MallService;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class MallConsoleInfo {

    private Set<String> cities;

    private List<MallService> services;

}
