package com.hchc.alarm.dao.hchc;

import com.hchc.alarm.dao.HcHcBaseDao;
import com.hchc.alarm.pack.SWResponse;
import com.hchc.alarm.task.TestTask;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
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

    public List<Object[]> queryVipOrderData() {
        String sql = "SELECT f_cardno, SUM(f_balance_after-f_balance_before) as balance  " +
                "from t_shangwei_prepaid_order where f_createtime > '2020-11-09 12:12:12' GROUP BY f_cardno";
        return hJdbcTemplate.query(sql, (r, i) -> {
            Object[] obj = new Object[2];
            obj[0] = r.getString("f_cardno");
            obj[1] = r.getBigDecimal("balance");
            return obj;
        });
    }

    public List<Object[]> queryGiftOrderData() {
        String sql = "SELECT f_cardno, SUM(f_balance_after-f_balance_before) as balance  " +
                "from t_shangwei_prepaid_gc_order where f_createtime > '2020-11-09 12:12:12' GROUP BY f_cardno";
        return hJdbcTemplate.query(sql, (r, i) -> {
            Object[] obj = new Object[2];
            obj[0] = r.getString("f_cardno");
            obj[1] = r.getBigDecimal("balance");
            return obj;
        });
    }

    public BigDecimal queryVipBalance(String abbDate, String cardNo) {
        String sql = "SELECT f_balance from t_vip_card_balance_status " +
                "where f_hqid = 3880 and f_abbdate = ?";
        if (cardNo.length() == 14) {
            sql += " and f_mapping_no = ? ";
        } else {
            sql += " and f_vip_card_no = ?";
        }
        List<BigDecimal> list = hJdbcTemplate.query(sql, (r, i) -> r.getBigDecimal(1), abbDate, cardNo);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    public BigDecimal queryGiftBalance(String abbDate, String cardNo) {
        String sql = "SELECT f_balance from t_gift_card_balance_status " +
                "where f_hqid = 3880 and f_abbdate = ? and  f_gift_card_no = ?";
        List<BigDecimal> list = hJdbcTemplate.query(sql, (r, i) -> r.getBigDecimal(1), abbDate, cardNo);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    public List<TestTask.Card> queryGiftCardBalance() {
        String sql = "SELECT\n" +
                "\tf_card_no,\n" +
                "\tSUM(\n" +
                "\t\tCASE\n" +
                "\t\tWHEN t.f_type = 'CREATE' THEN\n" +
                "\t\t\tf_before_balance - f_after_balance\n" +
                "\t\tWHEN t.f_type = 'CONSUME' THEN\n" +
                "\t\t\tf_after_balance - f_before_balance\n" +
                "\t\tWHEN t.f_type = 'CANCEL' THEN\n" +
                "\t\t\tt.f_amount\n" +
                "\t\tWHEN t.f_type = 'refund' THEN\n" +
                "\t\t\tf_after_balance - f_before_balance\n" +
                "\t\tEND\n" +
                "\t) AS amount\n" +
                "FROM\n" +
                "\tt_shangwei_prepaid_giftcard c\n" +
                "INNER JOIN t_gift_card_transaction t ON c.f_cardno = t.f_card_no\n" +
                "WHERE\n" +
                "\tc.f_hqid = 3880\n" +
                "AND t.f_createtime <= '2020-11-14 00:00:01'\n" +
                "GROUP BY\n" +
                "\tt.f_card_no;";
        List<TestTask.Card> cardBalanceRecords = hJdbcTemplate.query(sql, (rs, i) -> {
            TestTask.Card card = new TestTask.Card();
            card.setNo(rs.getString(1));
            card.setFlipBalance(rs.getBigDecimal(2));
            return card;
        });
        if (CollectionUtils.isEmpty(cardBalanceRecords)) {
            return Collections.emptyList();
        }
        return cardBalanceRecords;
    }

    public List<TestTask.Card> queryVipCardBalance1() {
        String sql = "SELECT f_vip_card_no,f_balance from t_vip_card_balance_status " +
                "where f_hqid = 3880 and f_abbdate = '20201113' and LENGTH(f_vip_card_no) < 12";
        return hJdbcTemplate.query(sql, (rs, i) -> {
            TestTask.Card card = new TestTask.Card();
            card.setNo(rs.getString(1));
            card.setFlipBalance(rs.getBigDecimal(2));
            return card;
        });
    }

    public List<TestTask.Card> queryVipCardBalance2() {
        String sql = "SELECT f_mapping_no,f_balance from t_vip_card_balance_status " +
                "where f_hqid = 3880 and f_abbdate = '20201113' and LENGTH(f_vip_card_no) > 12";
        return hJdbcTemplate.query(sql, (rs, i) -> {
            TestTask.Card card = new TestTask.Card();
            card.setNo(rs.getString(1));
            card.setFlipBalance(rs.getBigDecimal(2));
            return card;
        });
    }

    public void updateProblemCard(List<TestTask.Card> card) {
        List<Object[]> param = new ArrayList<>();
        String sql = "update t_shangwei_problem_card set f_reason = ? where f_card_no = ?";
        for (TestTask.Card c : card) {
            param.add(new Object[]{c.getReason(), c.getNo()});
        }
        hJdbcTemplate.batchUpdate(sql, param);
    }

    public List<TestTask.Card> queryAllSWCard() {
        String sql = "SELECT f_card_no,f_sw_balance from t_shangwei_problem_card where f_has_problem = 1 and length(f_card_no) = 14 and f_flip_balance is not null ";
        return hJdbcTemplate.query(sql, (rs, i) -> {
            TestTask.Card card = new TestTask.Card();
            card.setNo(rs.getString(1));
            card.setSwBalance(rs.getBigDecimal(2));
            return card;
        });
    }

    public void markUnKnowCard(List<String> noList) {
        String sql = "update t_shangwei_problem_card set f_unknow_card = 1 where f_card_no = ?";
        List<Object[]> params = new ArrayList<>();
        for (String s : noList) {
            params.add(new Object[]{s});
        }
        hJdbcTemplate.batchUpdate(sql, params);
    }

    public boolean isFlipCard(String no) {
        List<Long> records;
        String sql;
        if (no.length() == 14) {
            sql = "select f_id from t_shangwei_prepaid_card_mapping where f_card_id = ? and f_need_push = 1";
            records = hJdbcTemplate.query(sql, (r, i) -> r.getLong(1), no);
            return CollectionUtils.isEmpty(records) ? false : true;
        } else if (no.length() == 13) {
            sql = "select f_id from t_shangwei_prepaid_giftcard where f_cardno = ?";
            records = hJdbcTemplate.query(sql, (r, i) -> r.getLong(1), no);
            return CollectionUtils.isEmpty(records) ? false : true;
        } else if (no.length() < 13) {
            sql = "select f_id from t_shangwei_prepaid_vipcard where f_cardno = ?";
            records = hJdbcTemplate.query(sql, (r, i) -> r.getLong(1), no);
            return CollectionUtils.isEmpty(records) ? false : true;
        }
        return false;
    }

    public void saveShangWeiCard(List<SWResponse.CardList> cards) {
        String sql = "insert into t_shangwei_problem_card(f_card_no,f_sw_balance,f_createtime,f_isregister) " +
                "values(?,?,now(),?)";
        List<Object[]> params = new ArrayList<>();
        Object[] param;
        for (SWResponse.CardList c : cards) {
            param = new Object[]{c.getCardNo(), c.getCardMon(), c.getIsregister()};
            params.add(param);
        }
        hJdbcTemplate.batchUpdate(sql, params);
    }

    public void fillData(TestTask.Card c) {
        List<BigDecimal> records;
        BigDecimal flipBalance;
        String sql;
        if (c.getNo().length() == 13) {
            sql = "SELECT f_balance from t_gift_card_balance_status " +
                    "where f_hqid = 3880 and f_abbdate = '20201111' and f_gift_card_no = ?";
        } else if (c.getNo().length() == 14) {
            sql = "SELECT f_balance from t_vip_card_balance_status " +
                    "where f_hqid = 3880 and f_abbdate = '20201111' and f_mapping_no = ?";
        } else {
            sql = "SELECT f_balance from t_vip_card_balance_status " +
                    "where f_hqid = 3880 and f_abbdate = '20201111' and f_vip_card_no = ?";
        }
        records = hJdbcTemplate.query(sql, (r, i) -> r.getBigDecimal(1), c.getNo());
        flipBalance = CollectionUtils.isEmpty(records) ? BigDecimal.ZERO : records.get(0);
        c.setFlipBalance(flipBalance);
        if (c.getSwBalance().compareTo(c.getFlipBalance()) != 0) {
            c.setHasProblem(true);
        }
    }

    public boolean hasRecord(String no) {
        String sql = "SELECT t.id from t_vip_card_transaction t INNER JOIN t_vip_card c on t.vip_card_id = c.id " +
                "INNER JOIN t_shangwei_prepaid_card_mapping m on c.number = m.f_number " +
                "where c.hq_id = 3880 and m.f_card_id = ?";
        List<Long> records = hJdbcTemplate.query(sql, (r, i) -> r.getLong(1), no);
        return CollectionUtils.isEmpty(records) ? false : true;
    }
}
