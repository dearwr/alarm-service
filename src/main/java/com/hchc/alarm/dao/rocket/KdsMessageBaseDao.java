package com.hchc.alarm.dao.rocket;

import com.hchc.alarm.dao.RocketBaseDao;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by wangrong 2020/5/13
 */
@Repository
public class KdsMessageBaseDao extends RocketBaseDao {

    public List<String> queryAllPushed(int branchId, String uuid, Date start, Date end) {
        String sql = "select f_order_no from t_kds_message where f_branchid=? and f_uuid=? and f_create_time between ? and ? and f_push_status=1 " +
                "and f_order_no not in " +
                "(select f_order_no from t_kds_message where f_branchid=? and f_uuid=? and f_create_time between ? and ? and f_push_status=1 " +
                "and f_log_action in ('ORDER_CALL','ORDER_DELIVERYING','TAKE_COMPLETE','ORDER_REFUND'))";
        Object[] params = new Object[]{branchId, uuid, start, end, branchId, uuid, start, end};
        List<String> orderList = rJdbcTemplate.query(sql, (rs, i) -> rs.getString("f_order_no"), params);
        if (CollectionUtils.isEmpty(orderList)) {
            return Collections.emptyList();
        }
        return orderList;
    }

}
