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

    private String mall;

    private String name;

    private String city;

    private String type;

    private String method;

    private List<MallBranchBO> mallBranches;

}
