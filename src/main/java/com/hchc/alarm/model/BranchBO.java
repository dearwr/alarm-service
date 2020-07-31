package com.hchc.alarm.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created 2020/5/13
 * @author wangrong
 */
@Getter
@Setter
@NoArgsConstructor
public class BranchBO {

    protected long hqId;

    protected String brandName;

    protected long branchId;

    protected String branchName;

    protected String address;

    public BranchBO(BranchBO branchBO) {
        this.hqId = branchBO.hqId;
        this.brandName = branchBO.getBrandName();
        this.branchId = branchBO.getBranchId();
        this.branchName = branchBO.getBranchName();
    }
}
