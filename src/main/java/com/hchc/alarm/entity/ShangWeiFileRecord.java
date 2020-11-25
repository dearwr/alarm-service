package com.hchc.alarm.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author wangrong
 * @date 2020-08-19
 */
@Setter
@Getter
public class ShangWeiFileRecord {

    private long id;
    private long hqId;
    private String file;
    private String uploadFlowNo;
    private String abbDate;
    private String state;
    private Date createTime;

    private String md5;
    private long length;
    private double totalBalance;
    private int cardSize;
}
