package com.hchc.alarm.dao.hchc;

import com.fasterxml.jackson.databind.JsonNode;
import com.hchc.alarm.dao.HcHcBaseDao;
import com.hchc.alarm.task.TestTask;
import com.hchc.alarm.util.DatetimeUtil;
import com.hchc.alarm.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;

/**
 * @author wangrong
 * @date 2020-10-13
 */
@Slf4j
@Repository
public class TestDao extends HcHcBaseDao {

    public void add(TestTask.POrder p, String abbDate) {
        String sql = "insert into t_chanyan_airport(f_bill, f_created_at, f_price, f_lose_money, f_date) " +
                "values(?,?,?,?,?) ";
        hJdbcTemplate.update(sql, p.getBill(), p.getCreated(), p.getPrice(), p.getPrice(), abbDate);
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

    public List<TestTask.ErrOrder> queryWagasErrOrders(int code, String abbDate) {
        String sql = "SELECT f_branchid,f_remark from t_erp_sync_record where f_hqid = ? and f_abbdate = ? and f_status = 'fail'";
        List<Object[]> errBranches = hJdbcTemplate.query(sql, (r, i) -> {
            Object[] obj = new Object[2];
            obj[0] = r.getLong(1);
            obj[1] = r.getString(2);
            return obj;
        }, code, abbDate);

        List<TestTask.ErrOrder> errOrders = new ArrayList<>();
        List<TestTask.ErrItem> errItems;
        TestTask.ErrOrder errOrder;
        TestTask.Branch branch;

        Set<String> totalErrStr = new HashSet<>();
        Set<String> errStr;
        StringBuilder sb;
        for (Object[] obj : errBranches) {
            long branchId = (long) obj[0];
            sql = "select name,code from t_branch where id = ? ";
            branch = hJdbcTemplate.query(sql, (r, i) -> {
                TestTask.Branch b = new TestTask.Branch();
                b.setName(r.getString(1));
                b.setCode(r.getString(2));
                return b;
            }, branchId).get(0);

            errOrder = new TestTask.ErrOrder();
            errOrder.setName(branch.getName());
            errOrder.setErpCode(branch.getCode());
            errItems = JsonUtils.parseList((String) obj[1], TestTask.ErrItem.class);

            errStr = new HashSet<>();
            for (TestTask.ErrItem item : errItems) {
                sb = new StringBuilder();
                String type = fetchType(item.getServiceType());
                sb.append("(").append(type).append(")").append(item.getName());
                for (TestTask.ErrOption op : item.getOptions()) {
                    sb.append("[").append(op.getName()).append("]");
                }
                errStr.add(sb.toString());
                totalErrStr.add(sb.toString());
            }
            sb = new StringBuilder();
            for (String str : errStr) {
                sb.append(str).append("、 ");
            }
            errOrder.setErrItem(sb.toString());
            errOrders.add(errOrder);
        }
        sb = new StringBuilder();
        for (String str : totalErrStr) {
            sb.append(str).append("\n");
        }
        log.info("[total errStr] \n {}", sb.toString());
        return errOrders;
    }

    private String fetchType(String serviceType) {
        if ("ELEME".equals(serviceType)) {
            return "饿了么";
        } else if ("MEITUAN".equals(serviceType)) {
            return "美团";
        } else {
            return "FLIPOS";
        }
    }

    public List<TestTask.DeliveryOrder> queryDeliveryTime(int start, int end, int state) {
        String sql = "SELECT b.city,fo.f_create_time,fo.f_order_no,fo.f_history from t_delivery_sf_order fo " +
                "JOIN t_branch b on fo.f_branchid = b.id where f_hqid = 3880 and  f_create_time > '2021-01-01 00:00:00' and  f_state = ? LIMIT ?,?";
        return hJdbcTemplate.query(sql, (r, i) -> {
            TestTask.DeliveryOrder order = new TestTask.DeliveryOrder();
            order.setCity(r.getString("city"));
            order.setNo(r.getString("f_order_no"));
            Date creatTime = r.getTimestamp("f_create_time");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(creatTime);
            order.setYeah(calendar.get(Calendar.YEAR));
            order.setMonth(calendar.get(Calendar.MONTH) + 1);
            int week = calendar.get(Calendar.DAY_OF_WEEK) -1;
            if (week == 0) {
                week = 7;
            }
            order.setWeek(week);
            order.setDay(DatetimeUtil.format(creatTime));
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            order.setInterval(hour + "-" + (++hour));
            List<TestTask.Status> statusList = JsonUtils.parseList(r.getString("f_history"), TestTask.Status.class);
            long pushTime = 0;
            long receiveTime = 0;
            int rIndex = 0;
            TestTask.Status status;
            for (int ix = 0; ix < statusList.size(); ix++) {
                status = statusList.get(ix);
                if (status.getCode() == 10) {
                    rIndex = ix;
                    try {
                        receiveTime = DatetimeUtil.parse(status.getDatetime()).getTime();
                        break;
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
            for (int s = rIndex - 1; s >= 0; s--) {
                status = statusList.get(s);
                if (status.getCode() != 1) {
                    try {
                        status = statusList.get(s + 1);
                        if (status.getCode() != 10) {
                            pushTime = DatetimeUtil.parse(status.getDatetime()).getTime();
                            break;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if (s == 0) {
                    try {
                        status = statusList.get(s);
                        pushTime = DatetimeUtil.parse(status.getDatetime()).getTime();
                        break;
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
            order.setPickTime(DatetimeUtil.format(new Timestamp(receiveTime)));
            order.setWaitTime(roundHalfUpToBigDouble((receiveTime - pushTime) / 1000 / 60.0));
            if (order.getWaitTime() == 0) {
                order.setWaitTime(1.00);
            }
            return order;
        }, state, start, end);
    }

    public static double roundHalfUpToBigDouble(double val){
        BigDecimal b = new BigDecimal(val);
        return b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

}
