package com.hchc.alarm.constant;

import java.text.Collator;

/**
 * @author wangrong
 */
public class CommonConstant {

    /**
     * 商场补传订单
     */
    public static final String MALL_ORDER_URL = "http://localhost:9501/pushOrderToMall/pushToTestMall";

    /**
     * 中文比较器
     */
    public static final Collator CHINESE_COMPARATOR = Collator.getInstance(java.util.Locale.CHINA);

}
