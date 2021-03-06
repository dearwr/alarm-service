package com.hchc.alarm.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author wangrong
 */
@Getter
@Setter
public class MallProductCode {

    private int id;
    private int hqId;
    private int branchId;
    private String mall;
    /**
     * 关联我们系统的商品sku
     */
    private String sku;
    /**
     * 第三方系统商品编码code
     */
    private String code;
    private String mallId;
    private Date createTime;

}
