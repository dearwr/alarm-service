package com.hchc.alarm.dao.hchc;

import com.hchc.alarm.dao.HcHcBaseDao;
import com.hchc.alarm.entity.BranchKdsDO;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by wangrong 2020/5/12
 *
 * @author wangrong
 */
@Repository
public class BranchKdsBaseDao extends HcHcBaseDao {

    public List<Integer[]> queryCheckInfos() {
        String sql = "select f_id, f_hqid, f_branchid, f_open from t_branch_kds ";
        return hJdbcTemplate.query(sql,(rs,num)-> {
            Integer[] arr = new Integer[4];
            arr[0] = rs.getInt("f_id");
            arr[1] = rs.getInt("f_hqid");
            arr[2] = rs.getInt("f_branchid");
            arr[3] = rs.getInt("f_open");
            return arr;
        });
    }

    public List<BranchKdsDO> query(int hqId, int branchId) {
        List<Object> params = new ArrayList<>();
        StringBuilder sb = new StringBuilder("select * from t_branch_kds where f_open=1 and f_version is not null");
        if (hqId != 0) {
            sb.append(" and f_hqid = ? ");
            params.add(hqId);
        }
        if (branchId != 0) {
            sb.append(" and f_branchid = ? ");
            params.add(branchId);
        }
        List<BranchKdsDO> kdsList = hJdbcTemplate.query(sb.toString(), this::mapping, params.toArray());
        if (CollectionUtils.isEmpty(kdsList)) {
            return Collections.emptyList();
        }
        return kdsList;
    }

    private BranchKdsDO mapping(ResultSet rs, int i) throws SQLException {
        BranchKdsDO branchKdsTb = new BranchKdsDO();
        branchKdsTb.setHqId(rs.getInt("f_hqid"));
        branchKdsTb.setBranchId(rs.getInt("f_branchid"));
        branchKdsTb.setName(rs.getString("f_name"));
        branchKdsTb.setUuid(rs.getString("f_uuid"));
        branchKdsTb.setHeartTime(rs.getString("f_heart_time"));
        branchKdsTb.setVersion(rs.getString("f_version"));
        branchKdsTb.setOpen(rs.getBoolean("f_open"));
        return branchKdsTb;
    }

    public boolean queryExist(Integer branchId) {
        String sql = "select f_id from t_branch_kds where f_branchid = ?";
        List<Long> idList = hJdbcTemplate.query(sql, (rs, i) -> rs.getLong("f_id"), branchId);
        if (CollectionUtils.isEmpty(idList)) {
            return false;
        }
        return true;
    }

    public boolean queryUUidExit(String uuid) {
        String sql = "select f_uuid from t_branch_kds where f_uuid = ?";
        List<Long> idList = hJdbcTemplate.query(sql, (rs, i) -> rs.getLong("f_uuid"), uuid);
        if (CollectionUtils.isEmpty(idList)) {
            return false;
        }
        return true;
    }

    public int saveOne(BranchKdsDO kdsDO) {
        String sql = "insert into t_branch_kds (f_hqid, f_branchid, f_uuid, f_create_time, f_name) values (?, ?, ?, now(), ?)";
        return hJdbcTemplate.update(sql, kdsDO.getHqId(), kdsDO.getBranchId(), kdsDO.getUuid(), "自动添加");

    }

    public int delete(int hqId, int branchId, String uuid) {
        String sql = "delete from t_branch_kds where 1=1 ";
        List<Object> params = new ArrayList<>();
        if (hqId > 0) {
            sql += "and f_hqid = ?";
            params.add(hqId);
        }
        if (branchId > 0) {
            sql += "and f_branchid = ?";
            params.add(branchId);
        }
        if (uuid != null) {
            sql += "and f_uuid = ?";
            params.add(uuid);
        }
        return hJdbcTemplate.update(sql, params.toArray());
    }

    public void updateOpenState(Integer id, int state) {
        String sql = "update t_branch_kds set f_open = ? where f_id = ?";
        hJdbcTemplate.update(sql, state, id);
    }
}
