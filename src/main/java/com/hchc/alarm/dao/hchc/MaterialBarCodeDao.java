package com.hchc.alarm.dao.hchc;

import com.hchc.alarm.dao.HcHcBaseDao;
import org.springframework.stereotype.Repository;

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
}
