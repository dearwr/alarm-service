package com.hchc.alarm.dao.hchc;

import com.hchc.alarm.dao.HcHcBaseDao;
import com.hchc.alarm.model.CheckOrderBO;
import com.hchc.alarm.model.BranchCheckBO;
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

    private CheckOrderBO orderMapping(ResultSet set, int rowNum) throws SQLException {
        CheckOrderBO checkOrderBO = new CheckOrderBO();
        checkOrderBO.setHqId(set.getLong("hq_id"));
        checkOrderBO.setBranchId(set.getLong("branch_id"));
        checkOrderBO.setCreateTime(set.getDate("created_at"));
        checkOrderBO.setOrderNo(set.getString("bill"));
        checkOrderBO.setPlatform(set.getString("platform"));
        checkOrderBO.setStatus(set.getString("status"));
        return checkOrderBO;
    }

    private CheckOrderBO recordMapping(ResultSet set, int rowNum) throws SQLException {
        CheckOrderBO checkOrderBO = new CheckOrderBO();
        checkOrderBO.setOrderNo("f_orderno");
        checkOrderBO.setCreateTime(set.getDate("f_createtime"));
        checkOrderBO.setRemark(set.getString("f_remark"));
        return checkOrderBO;
    }

    public List<CheckOrderBO> queryUnPushOrders(BranchCheckBO branchCheckBO) {
        String sql = "SELECT hq_id, branch_id, created_at, bill, platform, `status` FROM t_order " +
                "WHERE branch_id = ? AND created_at BETWEEN ? AND ? AND `status`='COMPLETE' " +
                "AND bill NOT IN (" +
                "SELECT f_orderno FROM t_mall_record WHERE f_branchid = ? AND f_mall = ? " +
                "AND f_abbdate BETWEEN ? AND ? AND f_status IN ('suc' , 'skip' , 'exist') )";
        Object[] args = new Object[]{
                branchCheckBO.getBranchId(), branchCheckBO.getStartTime(), branchCheckBO.getEndTime(),
                branchCheckBO.getBranchId(), branchCheckBO.getMall(), branchCheckBO.getStartText(), branchCheckBO.getEndText()
        };
        List<CheckOrderBO> unPushOrders = hJdbcTemplate.query(sql, this::orderMapping, args);
        if (CollectionUtils.isEmpty(unPushOrders)) {
            return null;
        }
        return queryUnPushReason(branchCheckBO, unPushOrders);
    }

    private List<CheckOrderBO> queryUnPushReason(BranchCheckBO branchCheckBO, List<CheckOrderBO> checkOrderBOS) {
        String orderStr = checkOrderBOS.stream().map(CheckOrderBO::getOrderNo).collect(Collectors.joining("','", "'", "'"));
        String sql = "SELECT f_orderno, f_createtime, f_remark FROM t_mall_record WHERE f_branchid = ? AND f_abbdate BETWEEN ? AND ? AND f_orderno IN (" + orderStr + ")";
        Object[] args = new Object[]{
                branchCheckBO.getBranchId(), branchCheckBO.getStartText(), branchCheckBO.getEndText()
        };
        List<CheckOrderBO> checkOrderBOList = hJdbcTemplate.query(sql, this::recordMapping, args);
        if (CollectionUtils.isEmpty(checkOrderBOList)) {
            return checkOrderBOS;
        }
        for (CheckOrderBO order : checkOrderBOS) {
            for (CheckOrderBO record : checkOrderBOList) {
                if (order.getOrderNo().equals(record.getOrderNo())) {
                    order.setCreateTime(record.getCreateTime());
                    order.setRemark(record.getRemark());
                }
            }
        }
        return checkOrderBOS;
    }
}
