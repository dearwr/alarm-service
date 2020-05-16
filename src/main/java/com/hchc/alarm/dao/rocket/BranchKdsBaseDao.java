package com.hchc.alarm.dao.rocket;

import com.hchc.alarm.dao.RocketBaseDao;
import com.hchc.alarm.entity.kds.TBranchKds;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by wangrong 2020/5/12
 */
@Repository
public class BranchKdsBaseDao extends RocketBaseDao {

    public List<TBranchKds> query(int hqId, int branchId) {
        List<Object> params = new ArrayList<>();
        StringBuilder sb = new StringBuilder("select * from t_branch_kds where f_open=1");
        if (hqId != 0) {
            sb.append(" and f_hqid = ? ");
            params.add(hqId);
        }
        if (branchId != 0) {
            sb.append(" and f_branchid = ? ");
            params.add(branchId);
        }
        List<TBranchKds> kdsList = rJdbcTemplate.query(sb.toString(), this::Mapping, params.toArray());
        if (CollectionUtils.isEmpty(kdsList)) {
            return Collections.emptyList();
        }
        return kdsList;
    }

    private TBranchKds Mapping(ResultSet rs, int i) throws SQLException {
        TBranchKds tBranchKds = new TBranchKds();
        tBranchKds.setHqId(rs.getInt("f_hqid"));
        tBranchKds.setBranchId(rs.getInt("f_branchid"));
        tBranchKds.setName(rs.getString("f_name"));
        tBranchKds.setUuid(rs.getString("f_uuid"));
        tBranchKds.setHeartTime(rs.getString("f_heart_time"));
        return tBranchKds;
    }
}
