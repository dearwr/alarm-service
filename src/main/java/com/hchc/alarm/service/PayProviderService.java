package com.hchc.alarm.service;

import com.hchc.alarm.dao.hchc.MchDao;
import com.hchc.alarm.dao.hchc.PayProviderDao;
import com.hchc.alarm.dao.hchc.SettleAccountDao;
import com.hchc.alarm.model.niceconsole.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * @author wangrong
 * @date 2020-06-30
 */
@Service
public class PayProviderService {

    @Autowired
    private PayProviderDao payProviderDao;
    @Autowired
    private MchDao mchDao;
    @Autowired
    private SettleAccountDao settleAccountDao;

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, value = "hTransactionManager")
    public void batchSave(PayProviderBO payProvider) {
        ProviderBO provider = payProvider.getProvider();
        MchBO mch = payProvider.getMch();
        mch.setF_hqid(provider.getF_hq_id());
        AccountBO account = payProvider.getAccount();
        account.setF_hqid(provider.getF_hq_id());
        long branchId;
        boolean updateMch;
        boolean updateAccount;
        String providerNo;
        String flowNo;
        for (BranchBO branch : payProvider.getBranches()) {
            branchId = branch.getId();
            for (String payType : payProvider.getPayTypes()){
                provider.setF_branch_id(branchId);
                provider.setF_pay_type(payType);

                updateAccount = account.getF_flow() != null && account.getF_branchid() == provider.getF_branch_id();
                account.setF_branchid(branchId);
                account.setF_paytype("");

                updateMch = mch.getF_no() != null && mch.getF_branchid() == provider.getF_branch_id() && mch.getF_paytype().equals(provider.getF_pay_type());
                mch.setF_branchid(branchId);
                mch.setF_paytype(payType);

                if (!"NICE_MP".equals(payType)){
                    mch.setF_subappid("");
                    mch.setSub_app_id("");
                }

                if (updateAccount) {
                    flowNo = account.getF_flow();
                    settleAccountDao.update(account);
                }else {
                    flowNo = UUID.randomUUID().toString();
                    account.setF_flow(flowNo);
                    settleAccountDao.save(account);
                }

                mch.setAccount_flow(flowNo);
                mch.setF_account_flow(flowNo);
                if (updateMch) {
                    providerNo = mch.getF_no();
                    mchDao.update(provider.getF_provider(), mch);
                }else {
                    providerNo = UUID.randomUUID().toString();
                    mch.setF_no(providerNo);
                    mchDao.save(provider.getF_provider(), mch);
                }

                provider.setF_providerno(providerNo);
                payProviderDao.save(provider);

            }
        }
    }
}
