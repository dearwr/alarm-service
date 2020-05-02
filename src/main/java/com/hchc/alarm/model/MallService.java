package com.hchc.alarm.model;

import lombok.Data;

import java.util.List;

@Data
public class MallService {

    private String mark;

    private String name;

    private String city;

    private String pushType;

    private String pushMethod;

    private List<BranchInfo> branchInfos;

}
