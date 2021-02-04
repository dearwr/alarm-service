package com.hchc.alarm.dao.hchc;

import com.alibaba.fastjson.JSON;
import com.hchc.alarm.dao.HcHcBaseDao;
import com.hchc.alarm.model.MallBranchBO;
import com.hchc.alarm.model.RePushMallBO;
import com.hchc.alarm.model.TBranchMall;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by wangrong 2020/5/18
 *
 * @author wangrong
 */
@Repository
@Slf4j
public class BranchMallDao extends HcHcBaseDao {

    public List<RePushMallBO> queryValidMalls() {
        String sql = "select m.f_hqid, m.f_branchid, m.f_mall, m.f_transport_enable from t_branch_mall m " +
                "where f_type in ('immediate','daily2') and f_test = 1 and f_config is not null and f_config != '' ";
        return hJdbcTemplate.query(sql, this::mapping);
    }

    public List<MallBranchBO> queryBranchInfos(List<String> types) {
        String sql = "SELECT IFNULL(h.`name`,h.`code`) hName, b.`name` as bName, b.address, m.f_config, m.f_name " +
                "from t_branch_mall m LEFT JOIN t_headquarter h on m.f_hqid = h.id" +
                " LEFT JOIN t_branch b on m.f_branchid = b.id" +
                " WHERE f_config is not null and f_config <> '' and f_hqid not in (199,4)";
        if (types != null) {
            String typeStr = String.join("','", types);
            sql += " and f_type in ('" + typeStr + "')";
        }
        return hJdbcTemplate.query(sql, this::queryMapping);
    }

    public List<TBranchMall> query(String mallName) {
        String sql = "SELECT * FROM t_branch_mall WHERE f_mall = ? AND f_enable = TRUE ";
        List<TBranchMall> tBranchMalls = hJdbcTemplate.query(sql, (set, num) -> map(set), mallName);
        if (CollectionUtils.isEmpty(tBranchMalls)) {
            return null;
        }
        return tBranchMalls;
    }

    public TBranchMall map(ResultSet set) throws SQLException {
        TBranchMall branchMall = new TBranchMall();
        branchMall.setId(set.getLong("f_id"));
        branchMall.setHqId(set.getLong("f_hqid"));
        branchMall.setBranchId(set.getLong("f_branchid"));
        branchMall.setType(set.getString("f_type"));
        branchMall.setMall(set.getString("f_mall"));
        branchMall.setEnable(set.getBoolean("f_enable"));
        branchMall.setConfig(set.getString("f_config"));
        return branchMall;
    }

    private RePushMallBO mapping(ResultSet rs, int num) throws SQLException {
        RePushMallBO mall = new RePushMallBO();
        mall.setHqId(rs.getLong("f_hqid"));
        mall.setBranchId(rs.getLong("f_branchid"));
        mall.setMallName(rs.getString("f_mall"));
        return mall;
    }

    private MallBranchBO queryMapping(ResultSet set, int i) throws SQLException {
        MallBranchBO bInfo = JSON.parseObject(set.getString("f_config"), MallBranchBO.class);
        bInfo.setBrandName(set.getString("hName"));
        bInfo.setBranchName(set.getString("bName"));
        bInfo.setAddress(set.getString("address"));
        bInfo.setDisplayName(set.getString("f_name"));
        return bInfo;
    }
}
