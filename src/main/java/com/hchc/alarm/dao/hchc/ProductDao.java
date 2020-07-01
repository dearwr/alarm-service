package com.hchc.alarm.dao.hchc;

import com.hchc.alarm.dao.HcHcBaseDao;
import org.springframework.stereotype.Repository;

/**
 * @author wangrong
 * @date 2020-07-01
 */
@Repository
public class ProductDao extends HcHcBaseDao {

    public int disable(long productId){
        String sql = "update t_product set `status`= ? where id = ?";
        return hJdbcTemplate.update(sql, "DISABLED", productId);
    }
}
