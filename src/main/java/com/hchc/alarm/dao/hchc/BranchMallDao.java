package com.hchc.alarm.dao.hchc;

import com.alibaba.fastjson.JSON;
import com.hchc.alarm.dao.HcHcBaseDao;
import com.hchc.alarm.model.BranchInfo;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by wangrong 2020/5/18
 */
@Repository
public class BranchMallDao extends HcHcBaseDao {

    public List<BranchInfo> queryMallConsoleInfos() {
        String sql = "SELECT h.id, IFNULL(h.`name`,h.`code`) hName, b.`name` as bName, b.address, m.f_mall, m.f_type, m.f_config from t_branch_mall m" +
                " LEFT JOIN t_headquarter h on m.f_hqid = h.id" +
                " LEFT JOIN t_branch b on m.f_branchid = b.id" +
                " WHERE m.f_enable = 1 and f_config <> '' and f_hqid <> 199";
        return hJdbcTemplate.query(sql, this::queryMapping);
    }

    private BranchInfo queryMapping(ResultSet set, int i) throws SQLException {
        BranchInfo bConfig = JSON.parseObject(set.getString("f_config"), BranchInfo.class);
        bConfig.setBrandName(set.getString("hName"));
        bConfig.setBranchName(set.getString("bName"));
        bConfig.setAddress(set.getString("address"));
        bConfig.setMark(set.getString("f_mall"));
        bConfig.setPushMethod(set.getString("f_type"));
        return bConfig;
    }
}
