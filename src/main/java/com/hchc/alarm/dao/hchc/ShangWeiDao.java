package com.hchc.alarm.dao.hchc;

import com.hchc.alarm.dao.HcHcBaseDao;
import com.hchc.alarm.entity.ShangWeiCard;
import com.hchc.alarm.entity.ShangWeiFileRecord;
import com.hchc.alarm.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * @author wangrong
 * @date 2020-08-22
 */
@Repository
@Slf4j
public class ShangWeiDao extends HcHcBaseDao {

    public boolean updateMchData(long hqId, String data) {
        String sql = "update t_shangwei_prepaid_mch set f_data = ? where f_hqid = ?";
        return hJdbcTemplate.update(sql, data, hqId) > 0;
    }

    public ShangWeiFileRecord queryFileRecord(long hqId, String dayText) {
        String sql = "select * from t_shangwei_file_record where f_hqid = ? and date(f_createtime) = ? ";
        List<ShangWeiFileRecord> fileRecords = hJdbcTemplate.query(sql, (r, i) -> {
            ShangWeiFileRecord record = new ShangWeiFileRecord();
            record.setUploadFlowNo(r.getString("f_uploadflowno"));
            record.setMd5(r.getString("f_file_md5"));
            record.setLength(r.getLong("f_file_length"));
            record.setCardSize(r.getInt("f_card_size"));
            record.setTotalBalance(r.getDouble("f_total_balance"));
            return record;
        }, hqId, dayText);
        return CollectionUtils.isEmpty(fileRecords) ? null : fileRecords.get(0);
    }

    public List<ShangWeiCard> queryNewVipCards(long hqId, String date) {
        log.info("queryNewGiftCards hqId:{},date:{}", hqId, date);
        String sql = "select f_cardno from t_shangwei_prepaid_vipcard where f_hqid = ? and date(f_createtime) = ?";
        List<ShangWeiCard> cards = hJdbcTemplate.query(sql, (r, i) -> {
            ShangWeiCard card = new ShangWeiCard();
            card.setNo(r.getString(1));
            card.setType("电子会员卡");
            return card;
        }, hqId, date);
        log.info(" queryNewGiftCards cards:{}", JsonUtils.toJson(cards));
        return CollectionUtils.isEmpty(cards) ? Collections.emptyList() : cards;
    }

    public List<ShangWeiCard> queryNewGiftCards(long hqId, String date) {
        String sql = "select f_cardno from t_shangwei_prepaid_giftcard where f_hqid = ? and date(f_createtime) = ?";
        List<ShangWeiCard> cards = hJdbcTemplate.query(sql, (r, i) -> {
            ShangWeiCard card = new ShangWeiCard();
            card.setNo(r.getString(1));
            card.setType("礼品卡");
            return card;
        }, hqId, date);
        return CollectionUtils.isEmpty(cards) ? Collections.emptyList() : cards;
    }

    public List<ShangWeiCard> queryGiftCardsTrs(long hqId, String date) {
        String sql = "select * from t_shangwei_prepaid_gc_order where f_hqid = ? and date(f_createtime) = ? ORDER BY f_cardno";
        List<ShangWeiCard> cards = hJdbcTemplate.query(sql, (r, i) -> {
            ShangWeiCard card = new ShangWeiCard();
            card.setNo(r.getString("f_cardno"));
            card.setType("礼品卡");
            card.setBeforeBalance(r.getDouble("f_balance_before"));
            card.setBalance(r.getDouble("f_amount"));
            card.setAfterBalance(r.getDouble("f_balance_after"));
            return card;
        }, hqId, date);
        return CollectionUtils.isEmpty(cards) ? Collections.emptyList() : cards;
    }

    public List<ShangWeiCard> queryVipCardTrs(long hqId, String date) {
        String sql = "select * from t_shangwei_prepaid_order where f_hqid = ? and date(f_createtime) = ? ORDER BY f_cardno";
        List<ShangWeiCard> cards = hJdbcTemplate.query(sql, (r, i) -> {
            ShangWeiCard card = new ShangWeiCard();
            card.setNo(r.getString("f_cardno"));
            if (card.getNo().length() >= 14) {
                card.setType("迁移实体卡");
            } else {
                card.setType("电子会员卡");
            }
            card.setBeforeBalance(r.getDouble("f_balance_before"));
            card.setBalance(r.getDouble("f_amount"));
            card.setAfterBalance(r.getDouble("f_balance_after"));
            return card;
        }, hqId, date);
        return CollectionUtils.isEmpty(cards) ? Collections.emptyList() : cards;
    }
}
