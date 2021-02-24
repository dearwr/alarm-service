package com.hchc.alarm.constant;

/**
 * 配送物流公司
 *
 * @author wangrong
 * @date 2021-02-23
 */
public enum DeliveryCompany {

    /*达达*/
    DADA("dada", "达达", "t_delivery_dada_order", 1, 2, 4),
    /*顺丰*/
    SHUNFENG("shunfeng", "顺丰", "t_delivery_sf_order", 1, 10, 17),
    /*美团*/
    MEITUAN("meituan", "美团", "t_delivery_meituan_order", 0, 20, 50),
    /*侠刻送*/
    XIAKESONG("xiakesong", "侠刻送", "t_delivery_xks_order", 0, 1, 3),
    /*蜂鸟*/
    FENGNIAO("fengniao", "蜂鸟", "t_delivery_fn_order", 1, 20, 3),
    ;

    /**
     * 物流公司简称
     */
    private String name;
    /**
     * 平台名
     */
    private String platform;
    /**
     * 对应数据库表名
     */
    private String table;
    /**
     * 发起配送状态值
     */
    private int pushState;
    /**
     * 骑手已接单状态值
     */
    private int pickState;
    /**
     * 配送完成状态值
     */
    private int completeState;


    DeliveryCompany(String name, String platform, String table, int pushState, int pickState, int completeState) {
        this.name = name;
        this.platform = platform;
        this.table = table;
        this.pushState = pushState;
        this.pickState = pickState;
        this.completeState = completeState;
    }

    public static DeliveryCompany fetchCompanyByName(String name) {
        for (DeliveryCompany company : values()) {
            if (company.name.equals(name)) {
                return company;
            }
        }
        return null;
    }

    public static String fetchPlatform(String name) {
        for (DeliveryCompany company : values()) {
            if (company.name.equals(name)) {
                return company.platform;
            }
        }
        return null;
    }

    public String getTable() {
        return table;
    }

    public int getPushState() {
        return pushState;
    }

    public int getPickState() {
        return pickState;
    }

    public int getCompleteState() {
        return completeState;
    }
}
