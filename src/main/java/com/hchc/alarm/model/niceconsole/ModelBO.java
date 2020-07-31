package com.hchc.alarm.model.niceconsole;

import lombok.Getter;
import lombok.Setter;

/**
 * @author wangrong
 * @date 2020-06-18
 */
@Getter
@Setter
public class ModelBO {

    private HqBO hq;

    private BranchBO branch;

    private SuperAdminBO superAdmin;

    private String adminPassword;
}
