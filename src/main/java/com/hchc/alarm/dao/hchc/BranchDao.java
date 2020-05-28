package com.hchc.alarm.dao.hchc;

import com.hchc.alarm.dao.HcHcBaseDao;
import com.hchc.alarm.model.BranchBO;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by wangrong 2020/5/13
 * @author wangrong
 */
@Repository
public class BranchDao extends HcHcBaseDao {

    public BranchBO query(int hqId, int branchId) {
        String sql = "select h.name as brandName, b.name as branchName from t_headquarter h " +
                "left join t_branch b on h.id = b.hq_id " +
                " where b.hq_id = ? and b.id = ?";
        return hJdbcTemplate.query(sql, this::mapping, hqId, branchId).get(0);
    }

    private BranchBO mapping(ResultSet rs, int i) throws SQLException {
        BranchBO branchBO = new BranchBO();
        branchBO.setBrandName(rs.getString("brandName"));
        branchBO.setBranchName(rs.getString("branchName"));
        return branchBO;
    }
}
