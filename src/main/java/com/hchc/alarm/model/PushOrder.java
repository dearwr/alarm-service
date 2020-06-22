package com.hchc.alarm.model;

import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

/**
 * @author wangrong
 * @date 2020-06-22
 */
@Data
@ToString
public class PushOrder {

    private long hqId;
    private long branchId;
    private Date startTime;
    private Date endTime;
    private List<String> orderList;

    public PushOrder(long hqId, long branchId, Date start, Date end, List<String> orderList) {
        this.hqId = hqId;
        this.branchId =  branchId;
        this.startTime = start;
        this.endTime = end;
        this.orderList = orderList;
    }
}
