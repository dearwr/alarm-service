package com.hchc.alarm.model;

import lombok.Data;

import java.util.List;

@Data
public class MallService {

    private String mark;

    private String name;

    private String city;

    private List<String> types;

    private List<String> methods;

    private List<BranchInfo> branchInfos;

}
