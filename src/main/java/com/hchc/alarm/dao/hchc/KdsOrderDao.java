package com.hchc.alarm.dao.hchc;

import com.hchc.alarm.dao.HcHcBaseDao;
import org.springframework.stereotype.Repository;

/**
 * @author wangrong
 * @date 2020-07-23
 */
@Repository
public class KdsOrderDao extends HcHcBaseDao {

    public int orderComplete(long branchId, String startTime, String endTime) {
        String sql = "update t_kds_order set f_log_action = 'TAKE_COMPLETE' " +
                "where f_branchid = ? and f_create_time > ? and f_create_time < ?";
        return hJdbcTemplate.update(sql, branchId, startTime, endTime);
    }

}
