package com.hchc.alarm.dao.hchc;

import com.hchc.alarm.dao.HcHcBaseDao;
import com.hchc.alarm.model.BranchCheckBO;
import com.hchc.alarm.model.CheckOrderBO;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wangrong
 * @date 2020-05-28
 */
@Repository
public class MallRecordDao extends HcHcBaseDao {

    public List<CheckOrderBO> queryPushFailOrders(BranchCheckBO b) {
        String sql = "SELECT hq_id, branch_id, created_at, bill, platform, `status` FROM t_order " +
                "WHERE branch_id = ? AND created_at BETWEEN ? AND ? AND `status`='COMPLETE' " +
                "AND bill NOT IN (" +
                "SELECT f_orderno FROM t_mall_record WHERE f_branchid = ? AND f_mall = ? " +
                "AND f_abbdate BETWEEN ? AND ? AND f_status IN ('suc' , 'skip' , 'exist') )";
        Object[] args = new Object[]{
                b.getBranchId(), b.getStartTime(), b.getEndTime(), b.getBranchId(), b.getMall(), b.getStartText(), b.getEndText()
        };
        List<CheckOrderBO> pushFailOrders = hJdbcTemplate.query(sql, this::orderMapping, args);
        if (CollectionUtils.isEmpty(pushFailOrders)) {
            return null;
        }
        return queryPushFailReason(b, pushFailOrders);
    }

    private List<CheckOrderBO> queryPushFailReason(BranchCheckBO b, List<CheckOrderBO> failOrders) {
        String orderStr = failOrders.stream().map(CheckOrderBO::getOrderNo).collect(Collectors.joining("','", "'", "'"));
        String sql = "SELECT f_orderno, f_createtime, f_remark, f_status FROM t_mall_record WHERE f_branchid = ? AND f_abbdate BETWEEN ? AND ? AND f_orderno IN (" + orderStr + ")";
        List<CheckOrderBO> reasonOrderList = hJdbcTemplate.query(sql, this::recordMapping, b.getBranchId(), b.getStartText(), b.getEndText());
        if (CollectionUtils.isEmpty(reasonOrderList)) {
            return failOrders;
        }
        for (CheckOrderBO reasonOrder : reasonOrderList) {
            for (CheckOrderBO failOrder : failOrders) {
                if (failOrder.getOrderNo().equals(reasonOrder.getOrderNo())) {
                    failOrder.setPushTime(reasonOrder.getPushTime());
                    failOrder.setPushRemark(reasonOrder.getPushRemark());
                    failOrder.setPushStatus(reasonOrder.getPushStatus());
                    break;
                }
            }
        }
        return failOrders;
    }

    private CheckOrderBO orderMapping(ResultSet set, int rowNum) throws SQLException {
        CheckOrderBO checkOrderBO = new CheckOrderBO();
        checkOrderBO.setHqId(set.getLong("hq_id"));
        checkOrderBO.setBranchId(set.getLong("branch_id"));
        checkOrderBO.setCreatedAt(set.getDate("created_at"));
        checkOrderBO.setOrderNo(set.getString("bill"));
        checkOrderBO.setPlatform(set.getString("platform"));
        checkOrderBO.setOrderStatus(set.getString("status"));
        return checkOrderBO;
    }

    private CheckOrderBO recordMapping(ResultSet set, int rowNum) throws SQLException {
        CheckOrderBO checkOrderBO = new CheckOrderBO();
        checkOrderBO.setOrderNo(set.getString("f_orderno"));
        checkOrderBO.setPushTime(set.getDate("f_createtime"));
        checkOrderBO.setPushRemark(set.getString("f_remark"));
        checkOrderBO.setPushStatus(set.getString("f_status"));
        return checkOrderBO;
    }

    /**
     * 查询所有未上传的订单（包含外卖订单）
     * @param hqId
     * @param branchId
     * @param start
     * @param end
     * @param abbDate
     * @return
     */
    public List<String> queryAllUnPushOrderNo(long hqId, long branchId, Date start, Date end, String abbDate, String mallName) {
        String sql = "SELECT  DISTINCT o.bill  FROM t_order o " +
                "WHERE o.hq_id = ? AND o.branch_id = ? AND o.`status` = 'COMPLETE' " +
                "AND o.created_at BETWEEN ? AND ? and o.bill NOT IN(\n" +
                "SELECT r.f_orderno FROM t_mall_record r " +
                "WHERE r.f_hqid = ? AND r.f_branchid = ? AND r.f_abbdate = ? " +
                "AND r.f_status IN('suc' , 'skip' , 'exist') AND r.f_mall = ? " +
                ")";
        return hJdbcTemplate.queryForList(sql, String.class, hqId, branchId, start, end, hqId, branchId, abbDate, mallName);
    }

    public List<String> queryAllUnPushTransOrders(long branchId, String abbDate) {
        String sql = "SELECT o.bill FROM t_order o WHERE branch_id = ?  AND DATE(created_at) = ? AND `status` = 'COMPLETE'";
        return hJdbcTemplate.queryForList(sql, String.class, branchId, abbDate);
    }

}
