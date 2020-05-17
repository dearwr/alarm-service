package com.hchc.alarm.pack;

import lombok.Data;

/**
 * Created by wangrong 2020/5/13
 */
@Data
public class QueueInfo {

    private long hqId; // 品牌id
    private long branchId; // 门店id
    private int queueCount; // 排队人数
    private boolean busy; // 门店是否繁忙（出杯数大于70即为繁忙）
    private int cups;   // 出杯数
    private int waitMinutes;    // 等待时间,平均每一杯时间是70秒（单位：分钟）

}
