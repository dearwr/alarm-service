package com.hchc.alarm.dao.hchc;

import com.hchc.alarm.dao.HcHcBaseDao;
import com.hchc.alarm.entity.ShangWeiCard;
import com.hchc.alarm.entity.ShangWeiFileRecord;
import com.hchc.alarm.pack.ActiveCardInfo;
import com.hchc.alarm.pack.MigrateCardInfo;
import com.hchc.alarm.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author wangrong
 * @date 2020-08-22
 */
@Repository
@Slf4j
@EnableTransactionManagement
public class ShangWeiDao extends HcHcBaseDao {

    public void saveNewCard(String kidStr, String cidStr, BigDecimal balance) {
        String sql = "insert into t_shangwei_prepaid_new_card(f_number, f_card_id, f_balance, f_createtime) " +
                "values(?,?,?,now())";
        hJdbcTemplate.update(sql, kidStr, cidStr, balance);
    }

    public void saveCardMapping(String kidStr, String cidStr, int needPush) {
        String sql = "insert into t_shangwei_prepaid_card_mapping(f_number, f_card_id, f_need_push) " +
                "values(?,?,?)";
        hJdbcTemplate.update(sql, kidStr, cidStr, needPush);
    }

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
        return isEmpty(fileRecords) ? null : fileRecords.get(0);
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
        return isEmpty(cards) ? Collections.emptyList() : cards;
    }

    public List<ShangWeiCard> queryNewGiftCards(long hqId, String date) {
        String sql = "select f_cardno from t_shangwei_prepaid_giftcard where f_hqid = ? and date(f_createtime) = ?";
        List<ShangWeiCard> cards = hJdbcTemplate.query(sql, (r, i) -> {
            ShangWeiCard card = new ShangWeiCard();
            card.setNo(r.getString(1));
            card.setType("礼品卡");
            return card;
        }, hqId, date);
        return isEmpty(cards) ? Collections.emptyList() : cards;
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
        return isEmpty(cards) ? Collections.emptyList() : cards;
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
        return isEmpty(cards) ? Collections.emptyList() : cards;
    }

    @Transactional(rollbackFor = Exception.class, transactionManager = "hTransactionManager")
    public void importCards(List<MigrateCardInfo> cardInfos) {
        List<Object[]> cardParams = new ArrayList<>();
        List<Object[]> mappingParams = new ArrayList<>();
        List<Object[]> giveCardParams = new ArrayList<>();
        long hqId = 199;
        for (MigrateCardInfo card : cardInfos) {
            if (alreadyExist(hqId, card.getKid())) {
                continue;
            }
            cardParams.add(new Object[]{hqId, card.getKid(), StringUtil.isBlank(card.getPassword()) ? "1111" : card.getPassword(),
                    card.getBalance(), card.isGiveCard() ? card.getBalance() : 0});
            mappingParams.add(new Object[]{card.getKid(), card.getCardId()});
            if (card.isGiveCard()) {
                giveCardParams.add(new Object[]{card.getKid(), card.getCardId()});
            }
        }

        String sql = "insert into t_vip_card(hq_id, `number`, `name`, gender, `password`, balance, `status`, " +
                "created_at, f_membernumber, f_mrvipnumber, grant_balance) values(?,?,'','',?,?,'FROZEN',NOW(),'','',?)";
        hJdbcTemplate.batchUpdate(sql, cardParams);

        sql = "insert into t_shangwei_prepaid_card_mapping(f_number, f_card_id, f_need_push) " +
                "values(?,?,0)";
        hJdbcTemplate.batchUpdate(sql, mappingParams);

        if (!isEmpty(giveCardParams)) {
            sql = "insert into t_shangwei_prepaid_new_card(f_number, f_card_id, f_createtime, f_open_status) " +
                    "values(?,?,'2020-9-15 00:00:00', 1)";
            hJdbcTemplate.batchUpdate(sql, giveCardParams);
        }
    }

    private boolean alreadyExist(long hqId, String kid) {
        String sql = "select id from t_vip_card where hq_id = ? and `number` = ? ";
        List<Long> idList = hJdbcTemplate.query(sql, (r, i) -> r.getLong(1), hqId, kid);
        return !CollectionUtils.isEmpty(idList);
    }

    public boolean alreadyActivated(String number) {
        String sql = "select id from t_vip_card where hq_id = ? and `number` = ? and `status` = 'ACTIVE' ";
        List<Long> idList = hJdbcTemplate.query(sql, (r, i) -> r.getLong(1), 199, number);
        return !CollectionUtils.isEmpty(idList);
    }

    public boolean queryIsGiveCard(String number) {
        String sql = "select id from t_vip_card where hq_id = ? and `number` = ? and balance = grant_balance ";
        List<Long> idList = hJdbcTemplate.query(sql, (r, i) -> r.getLong(1), 199, number);
        return !CollectionUtils.isEmpty(idList);
    }

    @Transactional(rollbackFor = Exception.class,transactionManager = "hTransactionManager")
    public void activeCard(ActiveCardInfo cardInfo) {
        String sql = "update t_vip_card set `status` = 'ACTIVE' where hq_id = ? and `number` = ?";
        hJdbcTemplate.update(sql, 199, cardInfo.getKid());

        if (queryIsGiveCard(cardInfo.getKid())) {
            return;
        }
        sql = "insert into t_shangwei_prepaid_new_card(f_number, f_card_id, f_createtime) " +
                "values (?,?,now()) ";
        hJdbcTemplate.update(sql, cardInfo.getKid(), cardInfo.getCardId());
    }
}
