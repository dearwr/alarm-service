package com.hchc.alarm.dao.hchc;

import com.hchc.alarm.dao.HcHcBaseDao;
import com.hchc.alarm.model.niceconsole.ProviderBO;
import org.springframework.stereotype.Repository;

/**
 * @author wangrong
 * @date 2020-06-30
 */
@Repository
public class PayProviderDao extends HcHcBaseDao {

    public int save(ProviderBO provider) {
        String sql = "INSERT INTO t_pay_provider(f_hq_id, f_branch_id, f_pay_type, f_provider, f_enabled, f_providerno) " +
                "VALUES (?, ?, ?, ?, 1, ?)";
        Object[] params = new Object[]{
                provider.getF_hq_id(), provider.getF_branch_id(), provider.getF_pay_type(),
                provider.getF_provider(), provider.getF_providerno()
        };
        return hJdbcTemplate.update(sql, params);
    }

}
