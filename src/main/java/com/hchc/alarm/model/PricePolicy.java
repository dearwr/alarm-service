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
    /**
     * 适用门店标志,-1 部分门店 0 全部门店 >0 单个门店
     */
    private long branchId;
    private String name;
    private String desc;
    private Date beginDate;
    private Date endDate;
    private int platform;
    private int week;
    private String useRuleData;

    /**
     * 满足条件的商品数量
     */
    private int productCount;
    /**
     * 使用价格策略商品数量需要满足的条件（大于:">"、大于等于:">="、等于:"="）三种
     */
    private String condition;
    private String status;
    private String createTime;
    private String updateTime;

    /**
     * 适用人群数组(空数组表示适用所有人, 非空为适用具体哪些等级的会员)
     */
    private List<Long> levels;
    /**
     * 策略商品数组
     */
    private List<PricePolicyProduct> products;
    /**
     *  适用平台数组（长度4） 0位置代表"pos点单"、1位置代表"手机点单"、2位置代表"手机外送"、3位置表示"商城"；
     * 	"1"表示适用该平台,"0"表示不适用该平台
     */
    private List<Character> platformLimit;

}
