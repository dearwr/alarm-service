package com.hchc.alarm.dao.hchc;

import com.hchc.alarm.dao.HcHcBaseDao;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

/**
 * @author wangrong
 * @date 2020-07-09
 */
@Repository
public class MergeDao extends HcHcBaseDao {

    public int saveAirport(String number, String cardId, int needPush) {
        String sql = "insert into t_shangwei_prepaid_card_mapping (f_number, f_card_id, f_need_push) values(?, ?, ?)";
        return hJdbcTemplate.update(sql, number, cardId, needPush);
    }

    public int saveAirport(String kidStr, String cidStr) {
        String sql = "insert into t_shangwei_prepaid_new_card (f_number, f_card_id, f_createtime) values(?, ?, now())";
        return hJdbcTemplate.update(sql, kidStr, cidStr);
    }

    public int saveAirport(String date, String time, BigDecimal pushAmount, String flow) {
        String sql = "insert into t_chanyan_airport (f_date, f_time, f_money,f_flow) values(?, ?, ?,?)";
        return hJdbcTemplate.update(sql, date, time, pushAmount, flow);
    }

    public void saveNewCard(String kidStr, String cidStr, BigDecimal balance) {
        String sql = "insert into t_shangwei_prepaid_new_card(f_number, f_card_id, f_balance, f_createtime) " +
                "values(?,?,?,now())";
        hJdbcTemplate.update(sql, kidStr, cidStr, balance);
    }

    public void saveCardMapping(String kidStr, String cidStr, int needPush) {
        String sql = "insert into t_shangwei_prepaid_card_mapping(f_number, f_card_id, f_need_push) " +
                "values(?,?,?)";
        hJdbcTemplate.update(sql, kidStr, cidStr, needPush);
    }

    public void updateWaiMaiCode(String productStr, String codeStr) {
        String sql = "update t_product set code = ? where hq_id = 3880 and platform in ('eleme','meituan') " +
                "and name like '%" + productStr + "%'";
        hJdbcTemplate.update(sql, codeStr);
    }
}
