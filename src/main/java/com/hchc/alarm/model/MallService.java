package com.hchc.alarm.model;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class MallService {

    private String mark;

    private String name;

    private String city;

    private List<String> types;

    private List<String> methods;

    private Set<BranchInfo> branchInfos;

}
