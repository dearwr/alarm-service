package com.hchc.alarm.dao.hchc;

import com.hchc.alarm.dao.HcHcBaseDao;
import com.hchc.alarm.entity.MallProductCode;
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

    private MallProductCode toMapping(ResultSet rs, int num) throws SQLException {
        MallProductCode mallProductCode = new MallProductCode();
        mallProductCode.setId(rs.getInt("f_id"));
        mallProductCode.setHqId(rs.getInt("f_hqid"));
        mallProductCode.setBranchId(rs.getInt("f_branchid"));
        mallProductCode.setMall(rs.getString("f_mall"));
        mallProductCode.setSku(rs.getString("f_sku"));
        mallProductCode.setCode(rs.getString("f_code"));
        mallProductCode.setMallId(rs.getString("f_mall_id"));
        mallProductCode.setCreateTime(rs.getDate("f_createtime"));
        return mallProductCode;
    }

    public boolean batchSave(List<MallProductCode> mallProductCodeList) {
        String sql = "insert into t_mall_product_code(f_hqid, f_branchid, f_mall, f_sku, f_code, f_mall_id, f_createtime)" +
                " values(?, ?, ?, ?, ?, ?, now())";
        List<Object[]> paramsArr = new ArrayList<>(mallProductCodeList.size());
        MallProductCode entity;
        Object[] params;
        for (int i = 0; i < mallProductCodeList.size(); i++) {
            entity = mallProductCodeList.get(i);
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

    public List<MallProductCode> queryExist(MallProductCode mallProductCode) {
        String sql = "select * from t_mall_product_code where f_hqid=? and f_branchid=? ";
        return hJdbcTemplate.query(sql, this::toMapping, mallProductCode.getHqId(), mallProductCode.getBranchId());
    }

    public int updateSku(int branchId, String code, String sku) {
        String sql = "update t_mall_product_code set f_sku=?, f_mall_id=? where f_branchid=? and f_code=?";
        return hJdbcTemplate.update(sql, sku, sku, branchId, code);
    }
}
