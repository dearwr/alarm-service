package com.hchc.alarm.controller;

import com.alibaba.fastjson.JSON;
import com.hchc.alarm.dao.hchc.ShangWeiDao;
import com.hchc.alarm.entity.ShangWeiMch;
import com.hchc.alarm.pack.ActiveCardInfo;
import com.hchc.alarm.pack.MigrateCardInfo;
import com.hchc.alarm.pack.Output;
import com.hchc.alarm.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author wangrong
 * @date 2020-08-22
 */
@RestController
@RequestMapping("shangwei")
@Slf4j
public class ShangWeiController {

    @Autowired
    private ShangWeiDao shangWeiDao;

    @PostMapping("update")
    public Output updateConfig(@RequestBody ShangWeiMch mch) {
        try {
            String data = JSON.toJSONString(mch);
            log.info("[updateConfig] recv mch:{}", data);
            if (shangWeiDao.updateMchData(mch.getHqId(), data)) {
                return Output.ok();
            } else {
                return Output.fail("update fail");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("updateConfig happen error:{}", e.getMessage());
            return Output.fail(e.getMessage());
        }
    }

    @PostMapping("activeCard")
    public Output activeCard(@RequestBody ActiveCardInfo cardInfo) {
        log.info("[activeCard] recv :{}", JSON.toJSONString(cardInfo));
        String cardId = cardInfo.getCardId();
        try {
            if (StringUtil.isBlank(cardInfo.getCardId()) || StringUtil.isBlank(cardInfo.getKid())) {
                return Output.fail(cardId + " 卡号和映射的卡号不能为空");
            }
            if (cardInfo.getBalance() == null) {
                return Output.fail(cardId + " 金额不能为空");
            }
            if (cardInfo.getBalance().compareTo(BigDecimal.ZERO) < 0) {
                return Output.fail(cardId + " 余额不能小于0");
            }
            if (shangWeiDao.alreadyActivated(cardInfo)) {
                return Output.fail(cardId + " 已经激活过了");
            }
            shangWeiDao.activeCard(cardInfo);
            return Output.ok();
        } catch (Exception e) {
            e.printStackTrace();
            log.info("[activeCard] {} happen error:{}", cardId, e.getMessage());
            return Output.fail(e.getMessage());
        }
    }


    @PostMapping("activeCard/batch")
    public Output batchActiveCard(@RequestBody List<ActiveCardInfo> cardInfos) {
        log.info("[batchActiveCard] recv :{}", JSON.toJSONString(cardInfos));
        String cardId = null;
        try {
            for (ActiveCardInfo cardInfo : cardInfos) {
                cardId = cardInfo.getCardId();
                if (StringUtil.isBlank(cardInfo.getCardId()) || StringUtil.isBlank(cardInfo.getKid())) {
                    return Output.fail(cardId + " 卡号和映射的卡号不能为空");
                }
                if (cardInfo.getBalance() == null) {
                    return Output.fail(cardId + " 金额不能为空");
                }
                if (cardInfo.getBalance().compareTo(BigDecimal.ZERO) < 0) {
                    return Output.fail(cardId + " 余额不能小于0");
                }
                if (shangWeiDao.alreadyActivated(cardInfo)) {
                    log.info(cardId + "已经激活过了");
                    continue;
                }
                shangWeiDao.activeCard(cardInfo);
            }
            return Output.ok();
        } catch (Exception e) {
            e.printStackTrace();
            log.info("[batchActiveCard] {} happen error:{}", cardId, e.getMessage());
            return Output.fail(e.getMessage());
        }
    }

    @PostMapping("importCards")
    public Output importCards(@RequestBody List<MigrateCardInfo> cardInfos) {
        try {
            log.info("[importCards] recv :{}", JSON.toJSONString(cardInfos));
            shangWeiDao.importCards(cardInfos);
            return Output.ok();
        } catch (Exception e) {
            e.printStackTrace();
            log.info("[importCards] happen error:{}", e.getMessage());
            return Output.fail(e.getMessage());
        }
    }


}
