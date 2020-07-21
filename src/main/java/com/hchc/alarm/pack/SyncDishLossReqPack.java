package com.hchc.alarm.pack;

import lombok.Getter;
import lombok.Setter;

/**
 * @author wangrong
 * @date 2020-07-21
 */
@Setter
@Getter
public class SyncDishLossReqPack {

    private long hqId;
    private long branchId;
    private String startTime;
    private String endTime;


    public SyncDishLossReqPack(long hqId, String branchId, String dayBegin, String dayEnd) {
        this.hqId = hqId;
        this.branchId = Long.parseLong(branchId);
        this.startTime = dayBegin;
        this.endTime = dayEnd;
    }
}
