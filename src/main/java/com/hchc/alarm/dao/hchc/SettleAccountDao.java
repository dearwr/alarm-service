package com.hchc.alarm.dao.hchc;

import com.hchc.alarm.dao.HcHcBaseDao;
import com.hchc.alarm.model.niceconsole.AccountBO;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * @author wangrong
 * @date 2020-06-30
 */
@Repository
public class SettleAccountDao extends HcHcBaseDao {

    public int save(AccountBO account) {
        String sql = "INSERT INTO t_settlement_account(f_hqid, f_branchid, f_paytype, f_account, f_idcard, f_type, " +
                "f_truename, f_mobile, f_daylimit, f_status, f_bankname, f_iscompany, f_bankno, f_settlebankno, " +
                "id_card_front_url, id_card_back_url, business_lcs_url, business_scene_url, business_facade_url, audit_status, " +
                "account_lcs_url, bank_branch_name, f_createtime, f_updatetime, f_flow) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now(), now(), ?)";
        Object[] params = new Object[]{
                account.getF_hqid(), account.getF_branchid(), account.getF_paytype(), account.getF_account(), account.getF_idcard(),
                account.getF_type(), account.getF_truename(), account.getF_mobile(), account.getF_daylimit(), account.getF_status(),
                account.getF_bankname(), account.isF_iscompany(), account.getF_bankno(), account.getF_settlebankno(), account.getId_card_front_url(),
                account.getId_card_back_url(), account.getBusiness_lcs_url(), account.getBusiness_scene_url(), account.getBusiness_facade_url(),
                account.getAudit_status(), account.getAccount_lcs_url(), account.getBank_branch_name(), account.getF_flow()
        };
        return hJdbcTemplate.update(sql, params);
    }

    public int update(AccountBO account) {
        String sql = "UPDATE t_settlement_account SET f_account=?, f_idcard=?, f_type=?, f_truename=?, f_mobile=?, f_daylimit=?, f_status=?, " +
                "f_bankname=?, f_iscompany=?, f_bankno=?, f_settlebankno=?, id_card_front_url=?, id_card_back_url=?, business_lcs_url=?, " +
                "business_scene_url=?, business_facade_url=?, audit_status=?, account_lcs_url=?, bank_branch_name=?, f_updatetime=?, f_flow=? " +
                "WHERE f_id=?";
        Object[] params = new Object[]{
                account.getF_account(), account.getF_idcard(), account.getF_type(), account.getF_truename(), account.getF_mobile(),
                account.getF_daylimit(), account.getF_status(), account.getF_bankname(), account.isF_iscompany(), account.getF_bankno(),
                account.getF_settlebankno(), account.getId_card_front_url(), account.getId_card_back_url(), account.getBusiness_lcs_url(),
                account.getBusiness_scene_url(), account.getBusiness_facade_url(), account.getAudit_status(), account.getAccount_lcs_url(),
                account.getBank_branch_name(), new Date(), account.getF_flow(), account.getF_id()
        };
        return hJdbcTemplate.update(sql, params);
    }
}
