package com.hchc.alarm.dao.hchc;

import com.hchc.alarm.dao.HcHcBaseDao;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

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
}
