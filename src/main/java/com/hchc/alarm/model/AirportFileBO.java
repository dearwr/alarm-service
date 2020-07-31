package com.hchc.alarm.model;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wangrong
 */
@Getter
@Setter
public class AirportFileBO {

    /**
     * 商品编号
     */
    private String code;

    /**
     * 商品条码
     */
    private String sku;

    /**
     * 往来店铺编号
     */
    private String shopCode;

    /**
     * 合同号
     */
    private String licenseCode;

    /**
     * 商品名称
     */
    private String goodName;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 零售单价
     */
    private double goodPrice;

    /**
     * 需要解析excel的列名到类属性名映射集合
     */
    public static Map<String, String> nameToParamMap;

    static {
        nameToParamMap = new HashMap<>();
        nameToParamMap.put("商品编号", "code");
        nameToParamMap.put("商品条码", "sku");
        nameToParamMap.put("往来店铺编号", "shopCode");
        nameToParamMap.put("合同号", "licenseCode");
        nameToParamMap.put("商品名称", "goodName");
        nameToParamMap.put("店铺名称", "shopName");
        nameToParamMap.put("零售单价", "goodPrice");
    }

}
