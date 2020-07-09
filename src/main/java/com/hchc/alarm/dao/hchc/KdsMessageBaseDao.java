package com.hchc.alarm.dao.hchc;

import com.hchc.alarm.dao.HcHcBaseDao;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by wangrong 2020/5/13
 *
 * @author wangrong
 */
@Repository
public class KdsMessageBaseDao extends HcHcBaseDao {

    public List<String[]> queryAllPushed(int branchId, String uuid, Date start, Date end) {
        String sql = "select f_order_no, f_log_action from t_kds_message where f_branchid=? and f_uuid=? and f_create_time between ? and ? and f_push_status=1";
        return hJdbcTemplate.query(sql, (rs, i) -> {
            String[] info = new String[2];
            info[0] = rs.getString(1);
            info[1] = rs.getString(2);
            return info;
        }, branchId, uuid, start, end);
    }

}
