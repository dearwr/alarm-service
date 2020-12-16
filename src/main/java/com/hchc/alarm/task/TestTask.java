package com.hchc.alarm.task;

import com.hchc.alarm.dao.hchc.TestDao;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author wangrong
 * @date 2020-10-13
 */
@Slf4j
@Service
public class TestTask {

    public static final String URL = "http://yfk.sww.sh.gov.cn/organizationfk_proxy/orSelectSendCardInfoAction.do" +
            "?pageNo=1&pageSize=10&isRegister=&usciNo=91310000753817795P&uniqueNo=310106H6213146100149&industrycode=H62&OSessionId=AYFJBSHDCJEBDRHLGHCFCXFZEYACIFCC";

    @Autowired
    private TestDao testDao;
    @Autowired
    private RestTemplate restTemplate;

//    @Scheduled(cron = "0 24 17 * * ?")
//    public void scientist() {
//        String baseUrl = "http://47.112.150.247:8080/fengniao/updateStore";
//        log.info("schedule start");
//        List<Object[]> shopInfo = testDao.queryFengNiaoShopInfo(3880L);
//        UpdateStoreInfo info;
//        String name;
//        String code;
//        for (Object[] obj : shopInfo) {
//            info = new UpdateStoreInfo();
//            name = (String) obj[2];
//            code = (String) obj[3];
//            switch (code) {
//                case "BAKER":
//                    info.setChain_store_name("Bs[" + name + "]");
//                    break;
//                case "FUNKKALE":
//                    info.setChain_store_name("Funk[" + name + "]");
//                    break;
//                case "LOKAL":
//                    info.setChain_store_name("Lokal[" + name + "]");
//                    break;
//                case "UNO":
//                    info.setChain_store_name("Uno[" + name + "]");
//                    break;
//                case "Wagas":
//                    info.setChain_store_name("Wagas[" + name + "]");
//                    break;
//                default:
//                    log.info("unexpect code :{}", code);
//                    break;
//            }
//            String url = baseUrl + "?hqId=" + obj[0] + "&branchId=" + obj[1];
//            log.info("url = {}", url);
//            Output output = restTemplate.postForEntity(url, info, Output.class).getBody();
//            log.info("更新门店返回结果：{}", JsonUtils.toJson(output));
//
//        }
//        log.info("schedule end");
//    }


//    @Scheduled(cron = "0 22 15 * * ?")
//    public void scientist() {
//        List<POrder> allOrders;
//        List<COrder> receivedOrders;
//        String abbDate;
//        for (int startAbbDate = 20201001; startAbbDate <= 20201022; startAbbDate++) {
//            abbDate = String.valueOf(startAbbDate);
//            abbDate = abbDate.substring(0, 4) + "-" + abbDate.substring(4, 6) + "-" + abbDate.substring(6);
//            allOrders = testDao.queryAllOrders(4885, abbDate);
//            abbDate = "2020-10-" + ('0' == abbDate.charAt(8) ? abbDate.charAt(9) : abbDate.substring(8));
//            receivedOrders = testDao.queryReceived(abbDate);
//            boolean exist;
//            List<POrder> unPushOrders = new ArrayList<>();
//            List<COrder> removeOrders;
//
//            for (POrder p : allOrders) {
//
//                removeOrders = new ArrayList<>();
//                exist = false;
//                for (COrder c : receivedOrders) {
//                    if (c.getDealTime().getTime() > p.getCreated().getTime()) {
//                        break;
//                    }
//                    if (p.getCreated().getTime() == c.getDealTime().getTime() && p.getPrice().compareTo(c.getMoney()) == 0) {
//                        exist = true;
//                        removeOrders.add(c);
//                        testDao.update(c.getId(), p);
//                    }
//                }
//                if (exist) {
//                    receivedOrders.removeAll(removeOrders);
//                }else {
//                    unPushOrders.add(p);
//                    testDao.add(p, abbDate);
//                }
//            }
//            log.info("未上传订单：" + JsonUtils.toJson(unPushOrders));
//        }
//    }

//    @Scheduled(cron = "0 15 15 * * ?")
//    public void scientist() {
//        log.info("schedule start");
//        List<COrder> receivedOrders;
//        String abbDate;
//        for (int startAbbDate = 20201001; startAbbDate <= 20201022; startAbbDate++) {
//            abbDate = String.valueOf(startAbbDate);
//            abbDate = abbDate.substring(0, 4) + "-" + abbDate.substring(4, 6) + "-" + abbDate.substring(6);
//            abbDate = "2020-10-" + ('0' == abbDate.charAt(8) ? abbDate.charAt(9) : abbDate.substring(8));
//            receivedOrders = testDao.queryReceived(abbDate);
//            Timestamp beforeTime = null;
//            BigDecimal beforeMoney = null;
//            for (COrder o : receivedOrders) {
//                if (beforeTime != null && beforeTime.getTime() == o.getDealTime().getTime() && o.getMoney().compareTo(beforeMoney) == 0) {
//                    testDao.markRepeat(o.getId(), o.getMoney());
//                }
//                beforeMoney = o.getMoney();
//                beforeTime = o.getDealTime();
//            }
//
//        }
//        log.info("schedule end");
//    }


//    @Scheduled(cron = "0 43 17 * * ?")
//    public void scientist() {
//        log.info("schedule start");
//        List<COrder> receivedOrders = testDao.queryShangWeiOrders();
//        String abbDate;
//        for (COrder o : receivedOrders) {
//            abbDate = DatetimeUtil.dayText(DatetimeUtil.addDay(o.getDealTime(), -1));
//            testDao.updateDate(o.getId(), abbDate);
//        }
//        log.info("schedule end");
//    }

//    @Scheduled(cron = "0 12 14 * * ?")
//    public void queryAllCardBalance() {
//        log.info("schedule start");
//        String URL = "http://yfk.sww.sh.gov.cn/organizationfk_proxy/orSelectSendCardInfoAction.do" +
//                "?isRegister=&usciNo=91310000753817795P&uniqueNo=310106H6213146100149&industrycode=H62&OSessionId=AYFJBSHDCJEBDRHLGHCFCXFZEYACIFCC";
//
//        List<Card> cards = testDao.queryGiftCardBalance();
//        check(cards);
//        cards = testDao.queryVipCardBalance1();
//        check(cards);
//        cards = testDao.queryVipCardBalance2();
//        check(cards);
//
//        log.info("schedule end");
//    }
//
//    private void check(List<Card> cards) {
//        SWResponse response;
//        String url;
//        List<Card> problemCards = new ArrayList<>();
//        BigDecimal totalAmt = BigDecimal.ZERO;
//        for (Card c : cards) {
//            totalAmt = totalAmt.add(c.getFlipBalance());
//            url = URL + "&cardNo=" + c.getNo();
//            response = restTemplate.postForObject(url, null, SWResponse.class);
//            if (CollectionUtils.isEmpty(response.getBody().getOrCardList())) {
//                log.info("not find cardNo={} in shangwei system", c.getNo());
//                continue;
//            }
//            c.setSwBalance(new BigDecimal(response.getBody().getOrCardList().get(0).getCardMon()));
//            if (c.getFlipBalance().compareTo(c.getSwBalance()) != 0) {
//                log.info(" {} flip:{}, sw:{}", c.getNo(), c.getFlipBalance(), c.getSwBalance());
//                problemCards.add(c);
//            }
//        }
//        log.info("checked cards size:{}, totalAmt:{}", cards.size(), totalAmt);
//        log.info("problemsCards:{}", JsonUtils.toJson(problemCards));
//    }

//    @Scheduled(cron = "0 59 20 * * ?")
//    public void scientist() throws ParseException {
//        Date start = DatetimeUtil.parse("2020-11-01 00:00:00");
//        Date end = DatetimeUtil.parse("2020-11-16 00:00:00");
//        String dayText;
//        while (start.getTime() <= end.getTime()) {
//            dayText = DatetimeUtil.format(start, "yyyy-MM-dd");
//            log.info("daytext:{} start", dayText);
//            List<Branch> branches = testDao.queryDayBranchInfo(dayText);
//            testDao.saveBranchInfo(branches);
//            start = DatetimeUtil.addDay(start, 1);
//        }
//    }
//
//    @Scheduled(cron = "0 12 21 * * ?")
//    public void find() throws ParseException {
//        log.info(" start");
//        List<Branch> branches = testDao.queryTrobleInfo();
//        testDao.saveTrobleInfo(branches);
//        log.info(" end");
//    }

//    @Scheduled(cron = "0 34 15 * * ?")
//    public void fillNewCardTask() {
//        log.info("fillNewCardTask start");
//        List<String> existCardIds = testDao.queryAllExistCardIds();
//        log.info("existCardIds size : " + existCardIds.size());
//        List<ComplexCard> cards = testDao.queryComplexCards();
//        log.info("cards size : " + cards.size());
//        List<ComplexCard> trimCards = new ArrayList<>();
//        for (ComplexCard card : cards) {
//            if (existCardIds.contains(card.getCardId())) {
//                continue;
//            }
//            trimCards.add(card);
//        }
//        log.info("trimCards size : " + trimCards.size());
//        testDao.batchSaveTrimCards(trimCards);
//
//        log.info("fillNewCardTask end");
//    }

    @Data
    public static class ComplexCard {
        private String number;
        private String cardId;
    }


    @Data
    public static class POrder {
        private String bill;
        private Timestamp created;
        private BigDecimal price;
    }

    @Data
    public static class COrder {
        private int id;
        private Timestamp dealTime;
        private BigDecimal money;
    }

    @Data
    public static class UpdateStoreInfo {
        private String service_code = "HHSJC0001";
        private String chain_store_name;
    }

    @Getter
    @Setter
    public static class Card {
        private String no;
        private BigDecimal flipBalance;
        private BigDecimal swBalance;
        private boolean hasProblem;
        private String reason;
    }

    @Getter
    @Setter
    public static class Branch {
        private String code;
        private String name;
        private int branchId;
        private String date;
        private BigDecimal price;
        private BigDecimal commission;
        private boolean hasProblem;
    }

}
