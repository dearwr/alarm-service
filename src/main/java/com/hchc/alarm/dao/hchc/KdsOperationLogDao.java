package com.hchc.alarm.dao.hchc;

import com.hchc.alarm.dao.HcHcBaseDao;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Repository
public class KdsOperationLogDao extends HcHcBaseDao {

    public String queryVersionCode(int hqId, int branchId, String startTime, String endTime) {
        String sql = "SELECT f_version_code from t_kds_operation_log where f_hqid = ? and f_branchid = ? " +
                "and f_option_time between ? and ? order by f_option_time desc limit 1";
        List<String> versionList = hJdbcTemplate.query(sql, (rs, i) -> String.valueOf(rs.getString("f_version_code")), hqId, branchId, startTime, endTime);
        if (CollectionUtils.isEmpty(versionList)) {
            return "";
        }
        return versionList.get(0);
    }
}
