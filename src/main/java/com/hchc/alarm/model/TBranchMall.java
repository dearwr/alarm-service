package com.hchc.alarm.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author: A
 * @date: 2019/4/1 17:09
 **/
@Getter
@Setter
public class TBranchMall {
    private long id;
    private long hqId;
    private long branchId;
    private String type;
    private String mall;
    private boolean enable;
    private String config;
}