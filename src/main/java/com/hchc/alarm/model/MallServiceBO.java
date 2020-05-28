package com.hchc.alarm.model;

import lombok.Data;

import java.util.List;

/**
 * @author wangrong
 */
@Data
public class MallServiceBO {

    private String mark;

    private String name;

    private String city;

    private List<String> types;

    private List<String> methods;

    private List<MallBranchBO> mallBranches;

}
