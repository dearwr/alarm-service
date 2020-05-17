package com.hchc.alarm.model;

import lombok.Data;

import java.util.List;

@Data
public class PushService {

    private String mark;

    private String pushType;

    private String pushMethod;

    private List<BranchConfig> branchConfigs;

}
