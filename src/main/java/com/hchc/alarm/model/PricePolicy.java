package com.hchc.alarm.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * @author wangrong
 * @date 2020-09-15
 */
@Setter
@Getter
public class PricePolicy {

    private long id;
    private long hqId;
    private long branchId;
    private String name;
    private String desc;
    private Date beginDate;
    private Date endDate;
    private int platform;
    private int week;
    private int productCount;
    private String useRuleData;
    private String condition;
    private String status;
    private String createTime;
    private String updateTime;

    private List<Long> branches;
    private List<Long> levels;
    private List<PricePolicyProduct> products;

    private List<Character> platformLimit;
    private List<Character> dayLimit;

}
