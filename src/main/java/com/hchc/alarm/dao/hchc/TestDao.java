package com.hchc.alarm.dao.hchc;

import com.hchc.alarm.dao.HcHcBaseDao;
import com.hchc.alarm.task.TestTask;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author wangrong
 * @date 2020-10-13
 */
@Repository
public class TestDao extends HcHcBaseDao {


    public List<TestTask.POrder> queryAllOrders(Integer branchId, String abbDate) {
        String sql = "SELECT bill ,created_at, SUM(IFNULL(p.settle_amount,p.amount)) as money from t_order o " +
                "LEFT JOIN t_order_payment_record p on o.id = p.order_id " +
                "where branch_id = ? and  DATE(created_at) = ? " +
                "GROUP BY bill HAVING SUM(IFNULL(p.settle_amount,p.amount)) != 0 ORDER BY created_at";
        return hJdbcTemplate.query(sql, (rs, i) -> {
            TestTask.POrder obj = new TestTask.POrder();
            obj.setBill(rs.getString("bill"));
            obj.setCreated(rs.getTimestamp("created_at"));
            obj.setPrice(rs.getBigDecimal("money"));
            return obj;
        }, branchId, abbDate);
    }

    public List<TestTask.COrder> queryReceived(String startAbbDate) {
        String sql = " SELECT f_id, f_time, f_money from t_chanyan_airport where date(f_time) = ? order by f_time";
        return hJdbcTemplate.query(sql, (rs, i) -> {
            TestTask.COrder obj = new TestTask.COrder();
            obj.setId(rs.getInt("f_id"));
            obj.setDealTime(rs.getTimestamp("f_time"));
            obj.setMoney(rs.getBigDecimal("f_money"));
            return obj;
        }, startAbbDate);
    }

    public void markRepeat(int id, BigDecimal moreMoney) {
        String sql = "update t_chanyan_airport set f_more_money = ? where f_id  = ?";
        hJdbcTemplate.update(sql, moreMoney, id);
    }

    public void updateAbbDate(int id, TestTask.POrder p) {
        String sql = "update t_chanyan_airport set f_bill = ?, f_created_at = ?, f_price = ? where f_id = ?";
        hJdbcTemplate.update(sql, p.getBill(), p.getCreated(), p.getPrice(), id);
    }

    public void add(TestTask.POrder p, String abbDate) {
        String sql = "insert into t_chanyan_airport(f_bill, f_created_at, f_price, f_lose_money, f_date) " +
                "values(?,?,?,?,?) ";
        hJdbcTemplate.update(sql, p.getBill(), p.getCreated(), p.getPrice(), p.getPrice(), abbDate);
    }

    public List<TestTask.COrder> queryShangWeiOrders() {
        String sql = "select f_id , f_createtime from t_shangwei_prepaid_order";
        return hJdbcTemplate.query(sql, (rs, i) -> {
            TestTask.COrder obj = new TestTask.COrder();
            obj.setId(rs.getInt("f_id"));
            obj.setDealTime(rs.getTimestamp("f_createtime"));
            return obj;
        });
    }

    public void updateDate(int id, String abbDate) {
        String sql = "update t_shangwei_prepaid_order set f_abbdate = ? where f_id = ?";
        hJdbcTemplate.update(sql, abbDate, id);
    }

    public List<Object[]> queryFengNiaoShopInfo(long hqId) {
        String sql = "SELECT s.f_hq_id, s.f_branch_id, b.`name`, h.`code` from t_delivery_fn_shop s " +
                "LEFT JOIN t_branch b on s.f_branch_id = b.id " +
                "LEFT JOIN t_headquarter h on b.sub_hq_id = h.id where s.f_hq_id = ? ORDER BY h.`name`";
        return hJdbcTemplate.query(sql, (r, i) -> {
            Object[] obj = new Object[4];
            obj[0] = r.getLong("f_hq_id");
            obj[1] = r.getLong("f_branch_id");
            obj[2] = r.getString("name");
            obj[3] = r.getString("code");
            return obj;
        }, hqId);
    }
}
