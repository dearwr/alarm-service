package com.hchc.alarm.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author wangrong
 * @date 2020-05-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CheckOrderBO extends BranchBO{

    private String orderNo;

    private Date createdAt;

    private String platform;

    private String orderStatus;

    private String mall;

    private String abbDate;

    private String pushStatus;

    private String pushRemark;

    private Date pushTime;

}
