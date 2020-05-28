package com.hchc.alarm.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author wangrong
 * @date 2020-05-28
 */
@Data
@NoArgsConstructor
public class BranchCheckBO {

    private long hqId;

    private long branchId;

    private String mall;

    private Date startTime;

    private Date endTime;

    private String startText;

    private String endText;

    public BranchCheckBO(MallBranchBO mallBranch) {
        this.hqId = mallBranch.getHqId();
        this.branchId = mallBranch.getBranchId();
        this.mall = mallBranch.getMark();
    }
}
