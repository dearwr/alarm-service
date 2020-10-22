package com.hchc.alarm.dao.hchc;

import com.hchc.alarm.dao.HcHcBaseDao;
import org.springframework.stereotype.Repository;

/**
 * @author wangrong
 * @date 2020-07-09
 */
@Repository
public class MaterialBarCodeDao extends HcHcBaseDao {

    public void delete(int i) {
        String sql = "delete from t_shangwei_prepaid_history_card where f_id = ?";
        hJdbcTemplate.update(sql, i);

    }

    public int save(String number, String cardId, int needPush) {
        String sql = "insert into t_shangwei_prepaid_card_mapping (f_number, f_card_id, f_need_push) values(?, ?, ?)";
        return hJdbcTemplate.update(sql, number, cardId, needPush);
    }

    public int save(String kidStr, String cidStr) {
        String sql = "insert into t_shangwei_prepaid_new_card (f_number, f_card_id, f_createtime) values(?, ?, now())";
        return hJdbcTemplate.update(sql, kidStr, cidStr);
    }
}
