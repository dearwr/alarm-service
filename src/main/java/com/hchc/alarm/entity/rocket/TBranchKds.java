package com.hchc.alarm.entity.rocket;

import lombok.Data;

import java.util.Date;

@Data
public class TBranchKds {

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

}
