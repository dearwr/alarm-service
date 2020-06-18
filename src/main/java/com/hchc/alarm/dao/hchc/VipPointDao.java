package com.hchc.alarm.dao.hchc;

import com.hchc.alarm.dao.HcHcBaseDao;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author wangrong
 * @date 2020-06-17
 */
@Repository
public class VipPointDao extends HcHcBaseDao {

    public List<String> queryNumbers(int start, int end) {
        String sql = "SELECT DISTINCT f_mrvipnumber from t_mr_consume_point LIMIT ?,?";
        return hJdbcTemplate.query(sql, (rs, i) -> rs.getString("f_mrvipnumber"), start, end);
    }

    public List<String[]> queryPointCardsByNumber(String vipNumber) {
        String sql = "SELECT f_id, f_templatenumber from t_mr_consume_point where f_mrvipnumber = ? and f_status = 'VALID'";
        return hJdbcTemplate.query(sql, this::mapping, vipNumber);
    }

    public int updatePointCardInvalid(List<String> idList) {
        String idListStr = String.join("','", idList);
        String sql = "update t_mr_consume_point set f_status = 'INVALID' where f_id in (' " + idListStr + " ')";
        return hJdbcTemplate.update(sql);

    }

    public String[] mapping(ResultSet rs, int num) throws SQLException {
        String[] obj = new String[2];
        obj[0] = rs.getString("f_id");
        obj[1] = rs.getString("f_templatenumber");
        return obj;
    }
}
