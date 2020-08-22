package com.hchc.alarm.dao.hchc;

import com.hchc.alarm.dao.HcHcBaseDao;
import org.springframework.stereotype.Repository;

/**
 * @author wangrong
 * @date 2020-08-22
 */
@Repository
public class ShangWeiMchDao extends HcHcBaseDao {

    public boolean update(long hqId, String data) {
        String sql = "update t_shangwei_prepaid_mch set f_data = ? where f_hqid = ?";
        return hJdbcTemplate.update(sql, data, hqId) > 0;
    }
}
