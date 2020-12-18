package com.hchc.alarm.dao.hchc;

import com.hchc.alarm.dao.HcHcBaseDao;
import com.hchc.alarm.entity.Product;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author wangrong
 * @date 2020-07-09
 */
@Repository
public class MergeDao extends HcHcBaseDao {

    public int saveAirport(String date, String time, BigDecimal pushAmount, String flow) {
        String sql = "insert into t_chanyan_airport (f_date, f_time, f_money,f_flow) values(?, ?, ?,?)";
        return hJdbcTemplate.update(sql, date, time, pushAmount, flow);
    }

    public void updateWaiMaiCode(String name, String code) {
        String sql = "update t_product set code = ? where hq_id = 3880 and platform in ('eleme','meituan') " +
                "and name  = ?";
        hJdbcTemplate.update(sql, code, name);
    }

    public boolean queryExistName(String name) {
        String sql = "select f_id from t_waimai_name_code_mapping where f_product_name = ? ";
        List<Long> ids = hJdbcTemplate.query(sql, (r, i) -> r.getLong(1), name);
        return CollectionUtils.isEmpty(ids) ? false : true;
    }

    public void saveWaimaiNameCode(String productStr, String codeStr) {
        String sql = "insert into t_waimai_name_code_mapping(f_hqid,f_product_name,f_code,f_create_time) values (?,?,?,now())";
        hJdbcTemplate.update(sql, 3880, productStr, codeStr);
    }

    public List<Product> queryNoCodeProducts() {
        String sql = "select id, name from t_product where hq_id = 3880 and platform in ('eleme','meituan') and code = ''";
        return hJdbcTemplate.query(sql, (r, i) -> {
            Product p = new Product();
            p.setId(r.getInt(1));
            p.setName(r.getString(2));
            return p;
        });
    }

    public List<Product> queryProductMapping() {
        String sql = "select f_product_name,f_code from t_waimai_name_code_mapping ";
        return hJdbcTemplate.query(sql, (r, i) -> {
            Product p = new Product();
            p.setName(r.getString(1));
            p.setCode(r.getString(2));
            return p;
        });
    }
}
