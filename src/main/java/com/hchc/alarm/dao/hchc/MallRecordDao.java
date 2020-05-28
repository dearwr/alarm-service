package com.hchc.alarm.dao.hchc;

import com.hchc.alarm.dao.HcHcBaseDao;
import com.hchc.alarm.model.CheckOrder;
import com.hchc.alarm.model.BranchCheck;
import org.springframework.util.CollectionUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wangrong
 * @date 2020-05-28
 */
public class MallRecordDao extends HcHcBaseDao {

    private CheckOrder orderMapping(ResultSet set, int rowNum) throws SQLException {
        CheckOrder checkOrder = new CheckOrder();
        checkOrder.setHqId(set.getLong("hq_id"));
        checkOrder.setBranchId(set.getLong("branch_id"));
        checkOrder.setCreateTime(set.getDate("created_at"));
        checkOrder.setOrderNo(set.getString("bill"));
        checkOrder.setPlatform(set.getString("platform"));
        checkOrder.setStatus(set.getString("status"));
        return checkOrder;
    }

    private CheckOrder recordMapping(ResultSet set, int rowNum) throws SQLException {
        CheckOrder checkOrder = new CheckOrder();
        checkOrder.setOrderNo("f_orderno");
        checkOrder.setCreateTime(set.getDate("f_createtime"));
        checkOrder.setRemark(set.getString("f_remark"));
        return checkOrder;
    }

    public List<CheckOrder> queryUnPushOrders(BranchCheck branchCheck) {
        String sql = "SELECT hq_id, branch_id, created_at, bill, platform, `status` FROM t_order " +
                "WHERE branch_id = ? AND created_at BETWEEN ? AND ? AND `status`='COMPLETE' " +
                "AND bill NOT IN (" +
                "SELECT f_orderno FROM t_mall_record WHERE f_branchid = ? AND f_mall = ? " +
                "AND f_abbdate BETWEEN ? AND ? AND f_status IN ('suc' , 'skip' , 'exist') )";
        Object[] args = new Object[]{
                branchCheck.getBranchId(), branchCheck.getStartTime(), branchCheck.getEndTime(),
                branchCheck.getBranchId(), branchCheck.getMall(), branchCheck.getStartText(), branchCheck.getEndText()
        };
        List<CheckOrder> unPushOrders = hJdbcTemplate.query(sql, this::orderMapping, args);
        if (CollectionUtils.isEmpty(unPushOrders)) {
            return null;
        }
        return queryUnPushReason(branchCheck, unPushOrders);
    }

    private List<CheckOrder> queryUnPushReason(BranchCheck branchCheck, List<CheckOrder> checkOrders) {
        String orderStr = checkOrders.stream().map(CheckOrder::getOrderNo).collect(Collectors.joining("','", "'", "'"));
        String sql = "SELECT f_orderno, f_createtime, f_remark FROM t_mall_record WHERE f_branchid = ? AND f_abbdate BETWEEN ? AND ? AND f_orderno IN (" + orderStr + ")";
        Object[] args = new Object[]{
                branchCheck.getBranchId(), branchCheck.getStartText(), branchCheck.getEndText()
        };
        List<CheckOrder> checkOrderList = hJdbcTemplate.query(sql, this::recordMapping, args);
        if (CollectionUtils.isEmpty(checkOrderList)) {
            return checkOrders;
        }
        for (CheckOrder order : checkOrders) {
            for (CheckOrder record : checkOrderList) {
                if (order.getOrderNo().equals(record.getOrderNo())) {
                    order.setCreateTime(record.getCreateTime());
                    order.setRemark(record.getRemark());
                }
            }
        }
        return checkOrders;
    }
}
