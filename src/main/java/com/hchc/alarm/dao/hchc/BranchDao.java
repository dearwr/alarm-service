package com.hchc.alarm.dao.hchc;

import com.hchc.alarm.dao.HcHcBaseDao;
import com.hchc.alarm.model.Branch;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by wangrong 2020/5/13
 */
@Repository
public class BranchDao extends HcHcBaseDao {

    public Branch query(int hqId, int branchId) {
        String sql = "select h.name as brandName, b.name as branchName from t_headquarter h " +
                "left join t_branch b on h.id = b.hq_id " +
                " where b.hq_id = ? and b.id = ?";
        return hJdbcTemplate.query(sql, this::Mapping, hqId, branchId).get(0);
    }

    private Branch Mapping(ResultSet rs, int i) throws SQLException {
        Branch branch = new Branch();
        branch.setBrandName(rs.getString("brandName"));
        branch.setBranchName(rs.getString("branchName"));
        return branch;
    }
}