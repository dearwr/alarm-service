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

    public String[] mapping(ResultSet rs, int i) throws SQLException {
        String[] obj = new String[2];
        obj[0] = rs.getString("f_id");
        obj[1] = rs.getString("f_templatenumber");
        return obj;
    }

    public List<String[]> queryInvalidPointCardsByHqId(long hqId) {
        String sql = "SELECT h.`name`, p.f_mrvipnumber, tp.f_name FROM t_mr_consume_point p " +
                "LEFT JOIN t_tp_mr_consume_point tp ON p.f_templatenumber = tp.f_number " +
                "LEFT JOIN t_headquarter h on tp.f_hqid = h.id " +
                "WHERE p.f_status = 'invalid' and tp.f_hqid = ? ";
        return hJdbcTemplate.query(sql, this::invalidMapping, hqId);
    }

    private String[] invalidMapping(ResultSet rs, int i) throws SQLException {
        String[] obj = new String[3];
        obj[0] = rs.getString("name");
        obj[1] = rs.getString("f_mrvipnumber");
        obj[2] = rs.getString("f_name");
        return obj;
    }

    public List<Long> queryHqList() {
        String sql = "SELECT DISTINCT h.id " +
                "FROM t_mr_consume_point p " +
                "LEFT JOIN t_tp_mr_consume_point tp ON p.f_templatenumber = tp.f_number " +
                "LEFT JOIN t_headquarter h on tp.f_hqid = h.id WHERE p.f_status = 'invalid' and tp.f_hqid <> 199;";
        return hJdbcTemplate.query(sql, (rs, i) -> rs.getLong("id"));
    }

}
