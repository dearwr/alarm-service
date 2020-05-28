package com.hchc.alarm.dao.hchc;

import com.alibaba.fastjson.JSON;
import com.hchc.alarm.dao.HcHcBaseDao;
import com.hchc.alarm.model.MallBranch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangrong 2020/5/18
 * @author wangrong
 */
@Repository
@Slf4j
public class BranchMallDao extends HcHcBaseDao {

    public List<MallBranch> queryBranchInfos(String pushType) {
        String sql = "SELECT b.id, IFNULL(h.`name`,h.`code`) hName, b.`name` as bName, b.address, m.f_mall, m.f_type, m.f_config from t_branch_mall m" +
                " LEFT JOIN t_headquarter h on m.f_hqid = h.id" +
                " LEFT JOIN t_branch b on m.f_branchid = b.id" +
                " WHERE m.f_enable = 1 and f_config <> '' and f_hqid not in (199,4)";
        List<Object> paramList = new ArrayList<>();
        if (pushType != null) {
            sql += " and f_type=? ";
            paramList.add(pushType);
        }
        return hJdbcTemplate.query(sql, this::queryMapping, paramList.toArray());
    }

    private MallBranch queryMapping(ResultSet set, int i) throws SQLException {
        MallBranch bInfo = JSON.parseObject(set.getString("f_config"), MallBranch.class);
        bInfo.setBranchId(set.getLong("id"));
        bInfo.setBrandName(set.getString("hName"));
        bInfo.setBranchName(set.getString("bName"));
        bInfo.setAddress(set.getString("address"));
        bInfo.setMark(set.getString("f_mall"));
        bInfo.setPushMethod(set.getString("f_type"));
        return bInfo;
    }
}
