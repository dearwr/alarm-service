package com.hchc.alarm.model.niceconsole;

import lombok.Data;

import java.util.List;

/**
 * @author wangrong
 * @date 2020-06-29
 */
@Data
public class PayProviderBO {

    private List<BranchBO> branches;
    private List<String> payTypes;
    private ProviderBO provider;
    private MchBO mch;
    private AccountBO account;

}
