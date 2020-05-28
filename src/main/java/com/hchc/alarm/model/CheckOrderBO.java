package com.hchc.alarm.model;

import lombok.Data;

import java.util.Date;

/**
 * @author wangrong
 * @date 2020-05-28
 */
@Data
public class CheckOrderBO {

    private long hqId;

    private long branchId;

    private String mall;

    private String orderNo;

    private String platform;

    private String abbDate;

    private String status;

    private String remark;

    private Date createTime;

}
