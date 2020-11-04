package com.hchc.alarm.dao.hchc;

import com.hchc.alarm.dao.HcHcBaseDao;
import com.hchc.alarm.model.PricePolicy;
import com.hchc.alarm.model.PricePolicyProduct;
import com.hchc.alarm.util.DatetimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @author wangrong
 * @date 2020-09-16
 */
@Repository
@Slf4j
public class PricePolicyDao extends HcHcBaseDao {

    public List<Long> queryBranchIds(long policyId) {
        String sql = "SELECT branch_id FROM t_price_policy_branch WHERE policy_id = ?";
        List<Long> branchIds = hJdbcTemplate.query(sql, (rs, i) -> rs.getLong("branch_id"), policyId);
        if (CollectionUtils.isEmpty(branchIds)) {
            return Collections.emptyList();
        }
        return branchIds;
    }

    public List<Long> queryLevels(long policyId) {
        String sql = "SELECT level_id FROM t_price_policy_level WHERE policy_id = ? ";
        List<Long> levels = hJdbcTemplate.query(sql, (rs, i) -> rs.getLong("level_id"), policyId);
        if (CollectionUtils.isEmpty(levels)) {
            return Collections.emptyList();
        }
        return levels;
    }

    private PricePolicyProduct mapping(ResultSet set, int i) throws SQLException {
        PricePolicyProduct product = new PricePolicyProduct();
        product.setId(set.getInt("id"));
        product.setPolicyId(set.getInt("policy_id"));
        product.setProductId(set.getInt("product_id"));
        product.setPrice(set.getDouble("price"));
        return product;
    }

    public List<PricePolicyProduct> queryProducts(long policyId) {
        String sql = "SELECT * FROM t_price_policy_product WHERE policy_id = ? ";
        return hJdbcTemplate.query(sql, this::mapping, policyId);
    }

    private PricePolicy mappingAll(ResultSet set, int i) throws SQLException {
        PricePolicy policy = new PricePolicy();
        policy.setId(set.getInt("id"));
        policy.setHqId(set.getInt("hq_id"));
        policy.setBranchId(set.getInt("branch_id"));
        policy.setName(set.getString("name"));
        policy.setDesc(set.getString("desc"));
        policy.setBeginDate(set.getDate("begin_date"));
        policy.setEndDate(set.getDate("end_date"));
        policy.setPlatform(set.getInt("platform"));
        policy.setWeek(set.getInt("week"));
        policy.setProductCount(set.getInt("product_count"));
        policy.setUseRuleData(set.getString("use_rule_data"));
        policy.setCondition(set.getString("condition"));
        policy.setStatus(set.getString("status"));
        policy.setCreateTime(set.getString("create_time"));
        policy.setUpdateTime(set.getString("update_time"));
        policy.setTitle(set.getString("title"));
        return policy;
    }

    public List<PricePolicy> queryAll(long hqId, long branchId) {
        String sql = "SELECT * FROM t_price_policy WHERE hq_id = ? AND status = 'ENABLE'";
        List<PricePolicy> policies = hJdbcTemplate.query(sql, this::mappingAll, hqId);
        List<PricePolicy> validPolicies = new ArrayList<>();
        List<Character> dayLimit;
        for (PricePolicy p : policies) {
            // 判断适用日期
            Date now = new Date();
            Date dayBegin = DatetimeUtil.getDayStart(p.getBeginDate());
            Date dayEnd = DatetimeUtil.dayEnd(p.getEndDate());
            if (now.getTime() <= dayBegin.getTime() || now.getTime() >= dayEnd.getTime()) {
                continue;
            }
            // 判断适用星期
            dayLimit = toBinaryList(p.getWeek(), 7);
            if (!fitDayOfWeek(dayLimit, now)) {
                continue;
            }
            // 判断适用门店
            if (p.getBranchId() == -1) {
                List<Long> branches = queryBranchIds(p.getId());
                if (!branches.contains(branchId)) {
                    continue;
                }
            } else if (p.getBranchId() > 0) {
                if (p.getBranchId() != branchId) {
                    continue;
                }
            }
            p.setLevels(queryLevels(p.getId()));
            p.setProducts(queryProducts(p.getId()));
            p.setPlatformLimit(toBinaryList(p.getPlatform(), 4));
            validPolicies.add(p);
        }
        return validPolicies;
    }

    private static List<Character> toBinaryList(int bInt, int length) {
        List<Character> list = new ArrayList<>();
        char[] chars = Integer.toBinaryString(bInt).toCharArray();
        int len = chars.length;
        for (int i = 0; i < length; i++) {
            if (i < len) {
                list.add(i, chars[len - 1 - i]);
            } else {
                list.add(i, '0');
            }
        }
        return list;
    }

    private boolean fitDayOfWeek(List<Character> dayLimit, Date date) {
        int dayOfWeek = DatetimeUtil.getField(date, Calendar.DAY_OF_WEEK);
        if (dayOfWeek == 1 && dayLimit.get(6) == '0') {
            return false;
        }
        if (dayOfWeek != 1 && dayLimit.get(dayOfWeek - 2) == '0') {
            return false;
        }
        return true;
    }

}
