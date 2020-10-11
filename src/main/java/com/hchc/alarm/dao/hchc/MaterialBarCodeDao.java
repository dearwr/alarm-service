package com.hchc.alarm.dao.hchc;

import com.hchc.alarm.dao.HcHcBaseDao;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author wangrong
 * @date 2020-07-09
 */
@Repository
public class MaterialBarCodeDao extends HcHcBaseDao {

    public int save(long groupId, String barCode) {
        String sql = "insert into t_product_material_group_barcode (f_group_id, f_bar_code) values(?, ?)";
        return hJdbcTemplate.update(sql, groupId, barCode);
    }

    public int save(long groupId, String barCode, String suite) {
        String sql = "insert into t_product_material_group_barcode (f_group_id, f_bar_code, f_product_type) values(?, ?, ?)";
        return hJdbcTemplate.update(sql, groupId, barCode, suite);
    }

    public void delete(int i) {
        String sql = "delete from t_product_material_group_barcode where f_id >= ?";
        hJdbcTemplate.update(sql, i);

    }

    public boolean queryExist(long groupId, String cValue) {
        String sql = "select f_id from t_product_material_group_barcode where f_bar_code = ? and f_group_id = ?";
        List<Integer> idList = hJdbcTemplate.query(sql, (rs, i) -> rs.getInt("f_id"), cValue, groupId);
        if (CollectionUtils.isEmpty(idList)) {
            return false;
        }
        return true;
    }

    public int save(String number, String cardId, int needPush) {
        String sql = "insert into t_shangwei_prepaid_card_mapping (f_number, f_card_id, f_need_push) values(?, ?, ?)";
        return hJdbcTemplate.update(sql, number, cardId, needPush);
    }

    public int save(String cardId, BigDecimal balance) {
        String sql = "insert into t_shangwei_prepaid_history_card (f_card_no, f_balance) values(?, ?)";
        return hJdbcTemplate.update(sql, cardId, balance);
    }

    public int save(String name, String sku) {
        String sql = "update t_product set code = ? where hq_id = 3880 and platform in ('ELEME','MEITUAN') and  name like '" + name + "%'";
        return hJdbcTemplate.update(sql, sku);
    }

}
