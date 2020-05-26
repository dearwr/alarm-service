package com.hchc.alarm.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author wangrong
 */
@Data
public class KdsMessageTb {

    private int id;
    private int branchId;
    private String messageId;
    private String uuid;
    private String orderNo;
    private String logAction;
    private String data;
    private boolean pushed;
    private boolean valid;
    private Date createTime;
    private Date pushedTime;
    private Date invalidTime;
    private String type;

}
