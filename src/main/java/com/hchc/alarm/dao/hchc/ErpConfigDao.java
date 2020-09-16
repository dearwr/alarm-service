package com.hchc.alarm.dao.hchc;

import com.hchc.alarm.dao.HcHcBaseDao;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author wangrong
 * @date 2020-09-16
 */
@Repository
public class ErpConfigDao extends HcHcBaseDao {

    public int add(long hqId, long branchId, String erpName, String config) {
        String sql = "insert into t_erp_config(f_hqid,f_branchid,f_erp,f_config,f_createtime) values(?,?,?,?,now())";
        return hJdbcTemplate.update(sql, hqId, branchId, erpName, config);
    }

    public boolean queryExist(long hqId, Long bId, String erpName) {
        String sql = "select f_id from t_erp_config where f_hqid = ? and f_branchid = ? and f_erp = ?";
        List<Long> ids = hJdbcTemplate.query(sql, (rs, i) -> rs.getLong("f_id"), hqId, bId, erpName);
        if (CollectionUtils.isEmpty(ids)) {
            return false;
        }
        return true;
    }
}
