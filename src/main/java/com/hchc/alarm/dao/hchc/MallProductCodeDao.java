package com.hchc.alarm.dao.hchc;

import com.hchc.alarm.dao.HcHcBaseDao;
import com.hchc.alarm.entity.MallProductCodeDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author wangrong
 */
@Repository
@Slf4j
public class MallProductCodeDao extends HcHcBaseDao {

    private MallProductCodeDO toMapping(ResultSet rs, int num) throws SQLException {
        MallProductCodeDO mallProductCodeDO = new MallProductCodeDO();
        mallProductCodeDO.setId(rs.getInt("f_id"));
        mallProductCodeDO.setHqId(rs.getInt("f_hqid"));
        mallProductCodeDO.setBranchId(rs.getInt("f_branchid"));
        mallProductCodeDO.setMall(rs.getString("f_mall"));
        mallProductCodeDO.setSku(rs.getString("f_sku"));
        mallProductCodeDO.setCode(rs.getString("f_code"));
        mallProductCodeDO.setMallId(rs.getString("f_mall_id"));
        mallProductCodeDO.setCreateTime(rs.getDate("f_createtime"));
        return mallProductCodeDO;
    }

    public boolean batchSave(List<MallProductCodeDO> mallProductCodeDOList) {
        String sql = "insert into t_mall_product_code(f_hqid, f_branchid, f_mall, f_sku, f_code, f_mall_id, f_createtime)" +
                " values(?, ?, ?, ?, ?, ?, now())";
        List<Object[]> paramsArr = new ArrayList<>(mallProductCodeDOList.size());
        MallProductCodeDO entity;
        Object[] params;
        for (int i = 0; i < mallProductCodeDOList.size(); i++) {
            entity = mallProductCodeDOList.get(i);
            params = new Object[]{
                    entity.getHqId(), entity.getBranchId(), entity.getMall(), entity.getSku(), entity.getCode(), entity.getMallId()};
            paramsArr.add(params);
        }
        hJdbcTemplate.batchUpdate(sql, paramsArr);
        return true;
    }

    public void deleteRecord(int hqId, int branchId, Date createTime) {
        String sql = "delete from t_mall_product_code where f_hqid=? and f_branchid=? and f_createtime>?";
        hJdbcTemplate.update(sql, hqId, branchId, createTime);
    }

    public List<MallProductCodeDO> queryExist(MallProductCodeDO mallProductCodeDO) {
        String sql = "select * from t_mall_product_code where f_hqid=? and f_branchid=? ";
        return hJdbcTemplate.query(sql, this::toMapping, mallProductCodeDO.getHqId(), mallProductCodeDO.getBranchId());
    }

    public int updateSku(int branchId, String code, String sku) {
        String sql = "update t_mall_product_code set f_sku=?, f_mall_id=? where f_branchid=? and f_code=?";
        return hJdbcTemplate.update(sql, sku, sku, branchId, code);
    }
}
