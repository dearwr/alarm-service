package com.hchc.alarm.pack;

import lombok.Data;

/**
 * Created by wangrong 2020/5/13
 * @author wangrong
 */
@Data
public class QueueInfo {

    /**
     * 品牌id
     */
    private long hqId;
    /**
     * 门店id
     */
    private long branchId;
    /**
     * 排队人数
     */
    private int queueCount;
    /**
     * 门店是否繁忙（出杯数大于70即为繁忙）
     */
    private boolean busy;
    /**
     * 出杯数
     */
    private int cups;
    /**
     * 等待时间,平均每一杯时间是70秒（单位：分钟）
     */
    private int waitMinutes;

}
