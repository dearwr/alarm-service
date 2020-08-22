package com.hchc.alarm.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @author wangrong
 */
@Getter
@Setter
public class KdsOrder implements Serializable {

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
