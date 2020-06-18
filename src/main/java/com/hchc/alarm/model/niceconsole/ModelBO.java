package com.hchc.alarm.model.niceconsole;

import lombok.Data;

/**
 * @author wangrong
 * @date 2020-06-18
 */
@Data
public class ModelBO {

    private HqBO hq;

    private BranchBO branch;

    private SuperAdminBO superAdmin;

    private String adminPassword;
}
