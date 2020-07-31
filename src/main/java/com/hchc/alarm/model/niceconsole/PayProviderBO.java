package com.hchc.alarm.model.niceconsole;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author wangrong
 * @date 2020-06-29
 */
@Getter
@Setter
public class PayProviderBO {

    private List<BranchBO> branches;
    private List<String> payTypes;
    private ProviderBO provider;
    private MchBO mch;
    private AccountBO account;

}
