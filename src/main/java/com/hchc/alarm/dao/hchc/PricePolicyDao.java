package com.hchc.alarm.dao.hchc;

import com.hchc.alarm.dao.HcHcBaseDao;
import com.hchc.alarm.model.PricePolicy;
import com.hchc.alarm.model.PricePolicyProduct;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author wangrong
 * @date 2020-09-16
 */
@Repository
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
        return policy;
    }

    public List<PricePolicy> queryAll(long hqId, long branchId) {
        String sql = "SELECT * FROM t_price_policy WHERE hq_id = ? AND status = 'ENABLE'";
        List<PricePolicy> policies = hJdbcTemplate.query(sql, this::mappingAll, hqId);
        List<PricePolicy> validPolicies = new ArrayList<>();
        for (PricePolicy p : policies) {
            // 过滤适用的门店
            if (p.getBranchId() == 0) {
                p.setBranches(new ArrayList<>());
            } else if (p.getBranchId() == -1) {
                p.setBranches(queryBranchIds(p.getId()));
                if (!p.getBranches().contains(branchId)) {
                    continue;
                }
            } else {
                if (p.getBranchId() != branchId) {
                    continue;
                }
                p.setBranches(new ArrayList<>());
            }
            p.setLevels(queryLevels(p.getId()));
            p.setProducts(queryProducts(p.getId()));

            p.setPlatformLimit(new ArrayList<>());
            fillCharacter(p.getPlatform(), 4, p.getPlatformLimit());
            p.setDayLimit(new ArrayList<>());
            fillCharacter(p.getWeek(), 7, p.getDayLimit());
            validPolicies.add(p);
        }
        return validPolicies;
    }


    private static void fillCharacter(int bInt, int length, List<Character> list) {
        char[] chars = Integer.toBinaryString(bInt).toCharArray();
        for (int i = 0; i < length; i++) {
            if (i < chars.length) {
                list.add(i, chars[i]);
            } else {
                list.add(i, '0');
            }
        }
    }
}
