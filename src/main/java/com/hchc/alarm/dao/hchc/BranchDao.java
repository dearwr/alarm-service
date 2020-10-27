package com.hchc.alarm.dao.hchc;

import com.hchc.alarm.dao.HcHcBaseDao;
import com.hchc.alarm.model.BranchBO;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * Created by wangrong 2020/5/13
 * @author wangrong
 */
@Repository
public class BranchDao extends HcHcBaseDao {

    public int changeCode(int branchId, String code) {
        String sql = "update t_branch set `code` = ? where id = ?";
        return hJdbcTemplate.update(sql, code, branchId);
    }

    public BranchBO query(int hqId, int branchId) {
        String sql = "select h.name as brandName, b.name as branchName from t_headquarter h " +
                "left join t_branch b on h.id = b.hq_id " +
                " where b.hq_id = ? and b.id = ?";
        return hJdbcTemplate.query(sql, this::mapping, hqId, branchId).get(0);
    }

    public int updateFeatureData(long hqId, long branchId, String feature, String data) {
        String sql = "update t_branch_feature set data = ? " +
                "where hq_id = ? and branch_id =? and feature = ?";
        return hJdbcTemplate.update(sql, data, hqId, branchId, feature);
    }

    private BranchBO mapping(ResultSet rs, int i) throws SQLException {
        BranchBO branchBO = new BranchBO();
        branchBO.setBrandName(rs.getString("brandName"));
        branchBO.setBranchName(rs.getString("branchName"));
        return branchBO;
    }

    public List<Long> queryErpBranchIds(long hqId) {
        String sql = "select id from t_branch where hq_id = ? and code <> ''";
        List<Long> branchIds =  hJdbcTemplate.query(sql, (rs, i) -> rs.getLong("id"), hqId);
        if (CollectionUtils.isEmpty(branchIds)) {
            return Collections.emptyList();
        }
        return branchIds;
    }
}
