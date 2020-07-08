package com.hchc.alarm.dao.hchc;

import com.hchc.alarm.dao.HcHcBaseDao;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wangrong
 * @date 2020-07-08
 */
@Repository
public class OrderItemOptionDao extends HcHcBaseDao {

    public int delete(List<Long> idList) {
        StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM t_order_item_option WHERE id IN (");
        for (Long id : idList) {
            sb.append(id).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")");
        return hJdbcTemplate.update(sb.toString());
    }
}
