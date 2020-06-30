package com.hchc.alarm.model.niceconsole;

import lombok.Data;

/**
 * @author wangrong
 * @date 2020-06-29
 */
@Data
public class    ProviderBO {

    private long f_hq_id;
    private long f_branch_id;
    private String f_pay_type;
    private String f_provider;
    private boolean f_enabled;
    private String f_providerno;
    
}
