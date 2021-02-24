package com.hchc.alarm.dao.hchc;

import com.hchc.alarm.constant.DeliveryCompany;
import com.hchc.alarm.dao.HcHcBaseDao;
import com.hchc.alarm.task.TestTask;
import com.hchc.alarm.util.DatetimeUtil;
import com.hchc.alarm.util.JsonUtils;
import com.hchc.alarm.util.MathUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author wangrong
 * @date 2021-02-23
 */
@Slf4j
@Repository
public class FileDao extends HcHcBaseDao {

    public List<TestTask.DeliveryOrder> queryDeliveryTime(String start, String end, long hqId, String company) throws Exception {
        DeliveryCompany deliveryCompany = DeliveryCompany.fetchCompanyByName(company);
        if (deliveryCompany == null) {
            throw new Exception("物流方参数不合法");
        }
        String preSql = " SELECT b.city, o.f_create_time, o.f_order_no, o.f_history FROM ";
        String midSql = " o JOIN t_branch b ON o.f_branchid = b.id WHERE f_create_time BETWEEN ? AND ? AND f_hqid = ? AND f_state= ";
        String sql = preSql + deliveryCompany.getTable() + midSql + deliveryCompany.getCompleteState();

        return hJdbcTemplate.query(sql, (r, i) -> {
            TestTask.DeliveryOrder order = new TestTask.DeliveryOrder();
            order.setCity(r.getString("city"));
            order.setNo(r.getString("f_order_no"));
            Date creatTime = r.getTimestamp("f_create_time");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(creatTime);
            order.setYeah(calendar.get(Calendar.YEAR));
            order.setMonth(calendar.get(Calendar.MONTH) + 1);
            int week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            if (week == 0) {
                week = 7;
            }
            order.setWeek(week);
            order.setDay(DatetimeUtil.format(creatTime));
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            order.setInterval(hour + "-" + (++hour));
            List<TestTask.Status> statusList = JsonUtils.parseList(r.getString("f_history"), TestTask.Status.class);
            long pushTime = 0;
            long pickTime = 0;
            int rIndex = 0;
            TestTask.Status status;
            for (int ix = 0; ix < statusList.size(); ix++) {
                status = statusList.get(ix);
                if (status.getCode() == deliveryCompany.getPickState()) {
                    rIndex = ix;
                    try {
                        pickTime = DatetimeUtil.parse(status.getDatetime()).getTime();
                        break;
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
            for (int s = rIndex - 1; s >= 0; s--) {
                status = statusList.get(s);
                if (status.getCode() != deliveryCompany.getPushState()) {
                    try {
                        status = statusList.get(s + 1);
                        if (status.getCode() != deliveryCompany.getPickState()) {
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
            order.setPickTime(DatetimeUtil.format(new Timestamp(pickTime)));
            order.setWaitTime(MathUtil.roundHalfUpToBigDouble((pickTime - pushTime) / 1000 / 60.0));
            if (order.getWaitTime() == 0) {
                order.setWaitTime(1.00);
            }
            return order;
        }, start, end, hqId);
    }

}
