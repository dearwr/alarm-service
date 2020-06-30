package com.hchc.alarm.dao.hchc;

import com.hchc.alarm.dao.HcHcBaseDao;
import com.hchc.alarm.model.niceconsole.MchBO;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * @author wangrong
 * @date 2020-06-30
 */
@Repository
public class MchDao extends HcHcBaseDao {

    public int save(String provider, MchBO mch) {
        String sql = null;
        Object[] params = new Object[0];
        switch (provider) {
            case "WX_MCH":
                sql = "INSERT INTO t_wx_sub_mch(hq_id, branch_id, pay_type, sub_mch_id, `desc`, mch_key, " +
                        "sub_app_id, rate, account_flow, enabled, f_no) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, 1, ?)";
                params = new Object[]{
                        mch.getF_hqid(), mch.getF_branchid(), mch.getF_paytype(), mch.getSub_mch_id(), mch.getDesc(),
                        mch.getMch_key(), mch.getSub_app_id(), mch.getRate(), mch.getAccount_flow(), mch.getF_no()
                };
                break;
            case "ALIPAY_MCH":
                sql = "INSERT INTO t_alipay_sub_mch(f_hqid, f_branchid, f_mchkey, f_appid, f_authtoken, " +
                        "f_createtime, f_rate, f_account_flow, f_enabled, f_no) VALUES(?, ?, ?, ?, ?, now(), ?, ?, 1, ?)";
                params = new Object[]{
                        mch.getF_hqid(), mch.getF_branchid(), mch.getF_mchkey(), mch.getF_appid(), mch.getF_authtoken(),
                        mch.getF_rate(), mch.getF_account_flow(), mch.getF_no()
                };
                break;
            case "FUBEI":
                sql = "INSERT INTO t_fubei_sub_mch(f_hqid, f_branchid, f_paytype, f_appid, f_md5key, f_createtime, " +
                        "f_subappid, f_storeid, f_rate, f_account_flow, f_enabled, f_no) VALUES(?, ?, ?, ?, ?, now(), ?, ?, ?, ?, 1, ?)";
                params = new Object[]{
                        mch.getF_hqid(), mch.getF_branchid(), mch.getF_paytype(), mch.getF_appid(), mch.getF_md5key(),
                        mch.getF_subappid(), mch.getF_storeid(), mch.getF_rate(), mch.getF_account_flow(), mch.getF_no()
                };
                break;
            case "SHOUQIANBA":
                sql = "INSERT INTO t_shouqianbasubmch(f_hqid, f_branchid, f_paytype, f_mchkey, f_deviceid, f_terminalkey, f_createtime, f_updatetime, " +
                        "f_subappid, f_storeid, f_rate, f_account_flow, f_enabled, f_no) VALUES(?, ?, ?, ?, ?, ?, now(), now(), ?, ?, ?, ?, 1, ?)";
                params = new Object[]{
                        mch.getF_hqid(), mch.getF_branchid(), mch.getF_paytype(), mch.getF_mchkey(), mch.getF_deviceid(), mch.getF_terminalkey(),
                        mch.getF_subappid(), mch.getF_storeid(), mch.getF_rate(), mch.getF_account_flow(), mch.getF_no()
                };
                break;
            default:
                break;
        }
        return hJdbcTemplate.update(sql, params);
    }

    public int update(String provider, MchBO mch) {
        String sql = null;
        Object[] params = new Object[0];
        switch (provider) {
            case "WX_MCH":
                sql = "UPDATE t_wx_sub_mch SET sub_mch_id = ?, `desc` = ?, mch_key = ?, sub_app_id = ?, rate = ?, account_flow = ? , `enabled` = 1 WHERE id = ?";
                params = new Object[]{
                        mch.getSub_mch_id(), mch.getDesc(), mch.getMch_key(), mch.getSub_app_id(), mch.getRate(), mch.getAccount_flow(), mch.getF_id()
                };
                break;
            case "ALIPAY_MCH":
                sql = "UPDATE t_alipay_sub_mch SET f_mchkey = ?, f_appid = ?, f_authtoken = ?, f_rate = ?, f_account_flow = ?, f_enabled = 1 WHERE f_id = ?";
                params = new Object[]{
                        mch.getMch_key(), mch.getF_appid(), mch.getF_authtoken(), mch.getF_rate(), mch.getF_account_flow(), mch.getF_id()
                };
                break;
            case "FUBEI":
                sql = "UPDATE t_fubei_sub_mch SET f_appid = ?, f_md5key = ?, f_subappid = ?, f_storeid = ?, f_rate = ?, f_account_flow = ?, f_enabled = 1 WHERE f_id = ?";
                params = new Object[]{
                        mch.getF_appid(), mch.getF_md5key(), mch.getF_subappid(), mch.getF_storeid(), mch.getF_rate(), mch.getF_account_flow(), mch.getF_id()
                };
                break;
            case "SHOUQIANBA":
                sql = "UPDATE t_shouqianbasubmch SET f_mchkey = ?, f_deviceid = ?, f_terminalsn = ?, f_terminalkey = ?, f_updatetime = ?, f_subappid = ?, " +
                        "f_storeid = ?, f_rate =?, f_account_flow = ?, f_enabled = 1, WHERE f_id = ?";
                params = new Object[]{
                        mch.getF_mchkey(), mch.getF_deviceid(), mch.getF_terminalsn(), mch.getF_terminalkey(), new Date(), mch.getF_subappid(),
                        mch.getF_storeid(), mch.getF_rate(), mch.getF_account_flow(), mch.getF_id()
                };
                break;
            default:
                break;
        }
        return hJdbcTemplate.update(sql, params);
    }
}
