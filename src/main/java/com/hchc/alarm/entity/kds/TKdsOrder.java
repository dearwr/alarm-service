package com.hchc.alarm.entity.kds;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TKdsOrder implements Serializable {

    private int id;
    private int hqId;
    private int branchId;
    private String no;
    private String grade;
    private String type;
    private String logAction;
    private String data;
    private boolean completed;
    private Date createTime;
    private Date updateTime;

}
