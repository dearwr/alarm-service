package com.hchc.alarm.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author wangrong
 */
@Getter
@Setter
public class BranchKdsDO {

    private int id;
    private int hqId;
    private int branchId;
    private String name;
    private String uuid;
    private boolean open;
    private Date createTime;
    private Date offLineTime;
    private Date onlineTime;
    private String heartTime;
    private String version;

}
