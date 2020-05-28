package com.hchc.alarm.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author wangrong
 * @date 2020-05-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class BranchCheckBO extends BranchBO {

    private String mall;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    private String startText;

    private String endText;

    public BranchCheckBO(MallBranchBO branchBO) {
        super(branchBO);
    }
}
