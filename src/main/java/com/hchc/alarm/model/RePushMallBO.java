package com.hchc.alarm.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author wangrong
 * @date 2020-06-22
 */
@Getter
@Setter
public class RePushMallBO {

    private long hqId;

    private long branchId;

    private String mallName;

    public RePushMallBO() {

    }

    public RePushMallBO(int hqId, int branchId, String mallName) {
        this.hqId = hqId;
        this.branchId = branchId;
        this.mallName = mallName;
    }
}
