package com.hchc.alarm.task;

import com.hchc.alarm.dao.hchc.TestDao;
import lombok.Data;
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

}
