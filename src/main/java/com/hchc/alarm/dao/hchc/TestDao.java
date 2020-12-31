package com.hchc.alarm.dao.hchc;

import com.fasterxml.jackson.databind.JsonNode;
import com.hchc.alarm.dao.HcHcBaseDao;
import com.hchc.alarm.pack.SWResponse;
import com.hchc.alarm.task.TestTask;
import com.hchc.alarm.util.JsonUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
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

    public List<TestTask.Card> queryGiftCardBalance(String abbDate) {
        String sql = "select f_gift_card_no, f_balance from t_gift_card_balance_status " +
                "where f_hqid = 3880 and f_abbdate = ? ";
        List<TestTask.Card> cardBalanceRecords = hJdbcTemplate.query(sql, (rs, i) -> {
            TestTask.Card card = new TestTask.Card();
            card.setNo(rs.getString(1));
            card.setFlipBalance(rs.getBigDecimal(2));
            return card;
        }, abbDate);
        if (CollectionUtils.isEmpty(cardBalanceRecords)) {
            return Collections.emptyList();
        }
        return cardBalanceRecords;
    }

    public List<TestTask.Card> queryVipCardBalance1(String abbDate) {
        String sql = "SELECT f_vip_card_no,f_balance from t_vip_card_balance_status " +
                "where f_hqid = 3880 and f_abbdate = ? and LENGTH(f_vip_card_no) < 12";
        return hJdbcTemplate.query(sql, (rs, i) -> {
            TestTask.Card card = new TestTask.Card();
            card.setNo(rs.getString(1));
            card.setFlipBalance(rs.getBigDecimal(2));
            return card;
        }, abbDate);
    }

    public List<TestTask.Card> queryVipCardBalance2(String abbDate) {
        String sql = "SELECT f_mapping_no,f_balance from t_vip_card_balance_status " +
                "where f_hqid = 3880 and f_abbdate = ? and LENGTH(f_vip_card_no) > 12";
        return hJdbcTemplate.query(sql, (rs, i) -> {
            TestTask.Card card = new TestTask.Card();
            card.setNo(rs.getString(1));
            card.setFlipBalance(rs.getBigDecimal(2));
            return card;
        }, abbDate);
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

    public List<TestTask.Branch> queryDayBranchInfo(String dayText) {
        String sql = "SELECT h.code, b.`name`, o.branch_id, DATE(o.created_at), SUM(o.price),SUM(o.commission) from t_order o INNER JOIN t_order_payment_record p on o.id = p.order_id \n" +
                "INNER JOIN t_branch b on o.branch_id = b.id \n" +
                "INNER JOIN t_headquarter h on o.hq_id = h.id\n" +
                "where DATE(o.created_at)= ? and o.`status` = 'complete' GROUP BY o.branch_id";
        return hJdbcTemplate.query(sql, (r, i) -> {
            TestTask.Branch branch = new TestTask.Branch();
            branch.setCode(r.getString(1));
            branch.setName(r.getString(2));
            branch.setBranchId(r.getInt(3));
            branch.setDate(r.getString(4));
            branch.setPrice(r.getBigDecimal(5));
            branch.setCommission(r.getBigDecimal(6));
            return branch;
        }, dayText);
    }

    public void saveBranchInfo(List<TestTask.Branch> branches) {
        String sql = "insert into f_waimai_troble_data(f_code,f_name,f_branchid,f_date,f_now_price,f_now_commission) values(?,?,?,?,?,?)";
        List<Object[]> params = new ArrayList<>();
        for (TestTask.Branch b : branches) {
            params.add(new Object[]{b.getCode(), b.getName(), b.getBranchId(), b.getDate(), b.getPrice(), b.getCommission()});
        }
        hJdbcTemplate.batchUpdate(sql, params);
    }

    public List<TestTask.Branch> queryTrobleInfo() {
        String sql = "SELECT h.`code`, b.`name`, o.branch_id,DATE(o.created_at),SUM(price),SUM(commission) from t_order o \n" +
                "INNER JOIN waimai_order wo on o.bill = wo.sys_order_no \n" +
                "INNER JOIN t_branch b on o.branch_id = b.id \n" +
                "INNER JOIN t_headquarter h on o.hq_id = h.id\n" +
                "where o.created_at > '2020-11-01 00:00:00' and o.`status`='complete' and wo.state = 13 \n" +
                "GROUP BY o.branch_id, DATE(o.created_at) ORDER BY o.hq_id,o.branch_id;";
        return hJdbcTemplate.query(sql, (r, i) -> {
            TestTask.Branch branch = new TestTask.Branch();
            branch.setCode(r.getString(1));
            branch.setName(r.getString(2));
            branch.setBranchId(r.getInt(3));
            branch.setDate(r.getString(4));
            branch.setPrice(r.getBigDecimal(5));
            branch.setCommission(r.getBigDecimal(6));
            return branch;
        });

    }

    public void saveTrobleInfo(List<TestTask.Branch> branches) {
        String sql = "update f_waimai_troble_data set f_troble_price = ? , f_troble_commission = ? , f_has_troble = 1 " +
                "where f_branchid = ? and f_date = ? ";
        List<Object[]> params = new ArrayList<>();
        for (TestTask.Branch b : branches) {
            params.add(new Object[]{b.getPrice(), b.getCommission(), b.getBranchId(), b.getDate()});
        }
        hJdbcTemplate.batchUpdate(sql, params);
    }

    public List<String> queryAllExistCardIds() {
        String sql = "SELECT f_card_id from t_shangwei_prepaid_new_card ";
        return hJdbcTemplate.queryForList(sql, String.class);
    }

    public List<TestTask.ComplexCard> queryComplexCards() {
        String sql = "SELECT f_number,f_card_id from t_shangwei_prepaid_card_mapping";
        return hJdbcTemplate.query(sql, (r, i) -> {
            TestTask.ComplexCard card = new TestTask.ComplexCard();
            card.setNumber(r.getString("f_number"));
            card.setCardId(r.getString("f_card_id"));
            return card;
        });
    }

    public void batchSaveTrimCards(List<TestTask.ComplexCard> trimCards) {
        String sql = "insert into t_shangwei_prepaid_new_card(f_number,f_card_id,f_createtime,f_open_status) " +
                "values(?,?,?,1)";
        List<Object[]> params = new ArrayList<>();
        for (TestTask.ComplexCard card : trimCards) {
            params.add(new Object[]{card.getNumber(), card.getCardId(), "2020-9-15 00:00:00"});
        }
        hJdbcTemplate.batchUpdate(sql, params);
    }

    public List<TestTask.DeliveryBranch> queryDeliveryBranches(long hqId) {
        String sql = "SELECT b.`code`,b.`name`,f.`data`, b.phone,b.address,\n" +
                "case when sf.f_id is null then '否' else '是' end as shunfeng,\n" +
                "case when fn.f_id is null then '否' else '是' end as fengniao \n " +
                "from t_branch_feature f INNER JOIN t_branch b on f.branch_id = b.id\n" +
                "LEFT JOIN t_delivery_sf_shop sf on sf.f_branch_id = f.branch_id\n" +
                "LEFT JOIN t_delivery_fn_shop fn on fn.f_branch_id = f.branch_id\n" +
                "where f.hq_id = ? and feature = 'delivery' and f.enabled = 1 ";
        return hJdbcTemplate.query(sql, (r, i) -> {
            TestTask.DeliveryBranch branch = new TestTask.DeliveryBranch();
            branch.setCode(r.getString("code"));
            branch.setName(r.getString("name"));
            branch.setPhone(r.getString("phone"));
            branch.setAddress(r.getString("address"));
            branch.setHasSF(r.getString("shunfeng"));
            branch.setHasFN(r.getString("fengniao"));
            try {
                String data = r.getString("data");
                JsonNode jsonNode = JsonUtils.read(data);
                long start = jsonNode.get("service_start_time").longValue();
                String startTime = buildTime(start);
                long end = jsonNode.get("service_end_time").longValue();
                String endTime = buildTime(end);
                branch.setBusinessHours(startTime + "-" + endTime);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return branch;
        }, hqId);
    }

    private String buildTime(long time) {
        String businessHours = "";
        long hour = time / (60 * 60);
        if (hour == 0) {
            businessHours += "00";
        } else if (hour < 10) {
            businessHours += "0" + hour;
        } else {
            businessHours += hour;
        }
        businessHours += ":";
        long minute = time % (60 * 60) / 60;
        if (minute == 0) {
            businessHours += "00";
        } else if (minute < 10) {
            businessHours += "0" + minute;
        } else {
            businessHours += minute;
        }
        businessHours += ":";
        long second = time % (60 * 60) % 60;
        if (second == 0) {
            businessHours += "00";
        } else if (second < 10) {
            businessHours += "0" + second;
        } else {
            businessHours += second;
        }
        return businessHours;
    }
}
