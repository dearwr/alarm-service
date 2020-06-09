package com.hchc.alarm.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author wangrong
 */
@Data
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
