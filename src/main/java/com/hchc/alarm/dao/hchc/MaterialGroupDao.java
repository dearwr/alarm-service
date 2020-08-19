package com.hchc.alarm.dao.hchc;

import com.hchc.alarm.dao.HcHcBaseDao;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wangrong
 * @date 2020-07-09
 */
@Repository
public class MaterialGroupDao extends HcHcBaseDao {

    public List<Long> queryIdByCode(String code, int hqId) {
        String sql = "select id from t_product_material_group where code = ? and hq_id = ?";
        return hJdbcTemplate.query(sql, (rs, i) -> rs.getLong("id"), code, hqId);
    }

    public List<Long> querySuitProductIdBySku(String sku, int hqId) {
        String sql = "select `id` from  t_product  where `spu` = ? and product_type = 'SUITE' and hq_id = ?";
        return hJdbcTemplate.query(sql, (rs, i) -> rs.getLong("id"), sku, hqId);
    }

}
