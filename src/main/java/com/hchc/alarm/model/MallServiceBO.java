package com.hchc.alarm.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author wangrong
 */
@Getter
@Setter
public class MallServiceBO {

    private String mark;

    private String name;

    private String city;

    private List<String> types;

    private List<String> methods;

    private List<MallBranchBO> mallBranches;

}
