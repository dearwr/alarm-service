package com.hchc.alarm.task;

import com.hchc.alarm.dao.hchc.BranchMallDao;
import com.hchc.alarm.dao.hchc.TestDao;
import com.hchc.alarm.model.TBranchMall;
import com.hchc.alarm.pack.Output;
import com.hchc.alarm.pack.SWResponse;
import com.hchc.alarm.service.FileService;
import com.hchc.alarm.util.DatetimeUtil;
import com.hchc.alarm.util.FileUtil;
import com.hchc.alarm.util.JsonUtils;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wangrong
 * @date 2020-10-13
 */
@Slf4j
@Service
public class TestTask {

    private static final String URL = "http://yfk.sww.sh.gov.cn/organizationfk_proxy/orSelectSendCardInfoAction.do" +
            "?pageNo=1&pageSize=10&isRegister=&usciNo=91310000753817795P&uniqueNo=310106H6213146100149&industrycode=H62&OSessionId=AYFJBSHDCJEBDRHLGHCFCXFZEYACIFCC";

    @Autowired
    private FileService fileService;
    @Autowired
    private TestDao testDao;
    @Autowired
    private BranchMallDao branchMallDao;
    @Autowired
    private RestTemplate restTemplate;

    /**
     * 之前传wdt，改传 HARMAY ERP的地址
     */
    private final String HARMAY_ORDER_PUSH_URL = "http://120.78.232.8:9897/harmay/sync/orderPush/";
    private static final List<Long> TO_HARMAY_BRANCHES = new ArrayList<>();

    static {
        /*introlemons*/
        TO_HARMAY_BRANCHES.add(6509L);
        /*马里昂巴*/
        TO_HARMAY_BRANCHES.add(6708L);
    }


    /**
     * 更新wagas蜂鸟门店名
     */
//    @Scheduled(cron = "0 24 17 * * ?")
//    public void updateWagasFengNiaoShopName() {
//        execUpdateWagasFengNiaoShopName(3880);
//    }


    /**
     * 校验商委卡余额
     */
    @Scheduled(cron = "0 22 10 * * ?")
    public void checkShangWeiBalance() {
        execCheckShangWeiBalance();
    }


    /**
     * 查询wagasERP未维护SKU清单
     */
//    @Scheduled(cron = "0 53 15 * * ?")
//    public void queryWagasERPNoSkuItems() {
//        execQueryWagasERPNoSkuItems(3880,"20210125");
//    }


    /**
     * 查询配送平台骑手接单时间
     */
    @Scheduled(cron = "0 13 16 * * ?")
    public void queryDeliveryTime() {
        execQueryDeliveryTime();
    }


//    @Scheduled(cron = "0 */5 * * * ?")
//    public void syncOrdersToHarMay() {
//        log.info("[syncOrdersToHarMay] start");
//        execSyncOrdersToHarMay();
//        log.info("[syncOrdersToHarMay] end");
//    }

    private void execSyncOrdersToHarMay() {
        List<TBranchMall> branchMalls = branchMallDao.query("wdt");
        if (CollectionUtils.isEmpty(branchMalls)) {
            log.info("No merchant was found that required order push");
            return;
        }
        branchMalls = branchMalls.stream().filter(m -> TO_HARMAY_BRANCHES.contains(m.getBranchId())).collect(Collectors.toList());
        String pushDay = DatetimeUtil.format(new Date());
        branchMalls.forEach(tBranchMall -> {
            String orderPushUrl = HARMAY_ORDER_PUSH_URL + tBranchMall.getHqId() + "/" + tBranchMall.getBranchId() +
                    "?start=" + pushDay + "&end=" + pushDay;
            Output resPack = restTemplate.getForObject(orderPushUrl, Output.class);
            log.info("url: {}, 订单推送返回结果：{}", orderPushUrl, JsonUtils.toJson(resPack));
        });
    }


    private void execUpdateWagasFengNiaoShopName(long hqId) {
        String baseUrl = "http://47.112.150.247:8080/fengniao/updateStore";
        log.info("updateWagasFengNiaoShopName start");
        List<Object[]> shopInfo = testDao.queryFengNiaoShopInfo(hqId);
        UpdateStoreInfo info;
        String name;
        String code;
        for (Object[] obj : shopInfo) {
            info = new UpdateStoreInfo();
            name = (String) obj[2];
            code = (String) obj[3];
            switch (code) {
                case "BAKER":
                    info.setChain_store_name("Bs[" + name + "]");
                    break;
                case "FUNKKALE":
                    info.setChain_store_name("Funk[" + name + "]");
                    break;
                case "LOKAL":
                    info.setChain_store_name("Lokal[" + name + "]");
                    break;
                case "UNO":
                    info.setChain_store_name("Uno[" + name + "]");
                    break;
                case "Wagas":
                    info.setChain_store_name("Wagas[" + name + "]");
                    break;
                default:
                    log.info("unexpect code :{}", code);
                    break;
            }
            String url = baseUrl + "?hqId=" + obj[0] + "&branchId=" + obj[1];
            log.info("url = {}", url);
            Output output = restTemplate.postForEntity(url, info, Output.class).getBody();
            log.info("updateWagasFengNiaoShopName 返回结果：{}", JsonUtils.toJson(output));
        }
        log.info("updateWagasFengNiaoShopName end");
    }

    private void execCheckShangWeiBalance() {
        log.info(" checkShangWeiBalance start");
        String abbDate = DatetimeUtil.dayText(DatetimeUtil.addDay(new Date(), -1));
        List<Card> cards = testDao.queryGiftCardBalance(abbDate);
        checkShangWeiCard(cards);
        cards = testDao.queryVipCardBalance1(abbDate);
        checkShangWeiCard(cards);
        cards = testDao.queryVipCardBalance2(abbDate);
        checkShangWeiCard(cards);
        log.info("checkShangWeiBalance end");
    }

    private void checkShangWeiCard(List<Card> cards) {
        SWResponse response;
        String url;
        List<Card> problemCards = new ArrayList<>();
        BigDecimal totalAmt = BigDecimal.ZERO;
        try {
            for (Card c : cards) {
                totalAmt = totalAmt.add(c.getFlipBalance());
                url = URL + "&cardNo=" + c.getNo();
                response = restTemplate.postForObject(url, null, SWResponse.class);
                if (CollectionUtils.isEmpty(response.getBody().getOrCardList())) {
                    log.info("[checkShangWeiCard] not find cardNo={} in shangwei system", c.getNo());
                    continue;
                }
                c.setSwBalance(new BigDecimal(response.getBody().getOrCardList().get(0).getCardMon()));
                if (c.getFlipBalance().compareTo(c.getSwBalance()) != 0) {
                    problemCards.add(c);
                }
            }
            log.info("[checkShangWeiCard] find problemsCards:{}", JsonUtils.toJson(problemCards));
        } catch (Exception e) {
            e.printStackTrace();
            log.info("[checkShangWeiCard] happen error:{}", e.getMessage());
        }
    }

    private void execQueryDeliveryBranches(long hqId) {
        log.info("queryDeliveryBranches start");
        String fileName = "手机外送门店清单.xlsx";
        File file = FileUtils.getFile("/data", "share", "prepaid", "shangwei", "3880", "report", fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("sheet1");
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(1, true);
            Row row = sheet.createRow(0);
            row.createCell(0).setCellValue("Flipos Erp代码（开通手机外卖门店）");
            row.createCell(1).setCellValue("Flipos门店名称（开通手机外卖门店）");
            row.createCell(2).setCellValue("外送门店营业时间");
            row.createCell(3).setCellValue("门店电话");
            row.createCell(4).setCellValue("门店地址");
            row.createCell(5).setCellValue("是否有顺丰");
            row.createCell(6).setCellValue("是否有蜂鸟");
            List<DeliveryBranch> branches = testDao.queryDeliveryBranches(hqId);
            DeliveryBranch branch;
            for (int i = 0; i < branches.size(); i++) {
                row = sheet.createRow(i + 1);
                branch = branches.get(i);
                row.createCell(0).setCellValue(branch.getCode());
                row.createCell(1).setCellValue(branch.getName());
                row.createCell(2).setCellValue(branch.getBusinessHours());
                row.createCell(3).setCellValue(branch.getPhone());
                row.createCell(4).setCellValue(branch.getAddress());
                row.createCell(5).setCellValue(branch.hasSF);
                row.createCell(6).setCellValue(branch.hasFN);
            }
            workbook.write(fos);
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.deleteFile(file);
        }
        log.info("queryDeliveryBranches end");
    }

    public void execQueryWagasERPNoSkuItems(int hqId, String abbDate) {
        log.info("execQueryWagasERPNoSkuItems start");
        String fileName = "未维护SKU清单.xlsx";
        File file = FileUtils.getFile("/data", "share", "prepaid", "shangwei", "3880", "report", fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            Workbook workbook;
            workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("sheet1");
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(1, true);
            Row row = sheet.createRow(0);
            row.createCell(0).setCellValue("门店名");
            row.createCell(1).setCellValue("ERP CODE");
            row.createCell(3).setCellValue("未维护SKU商品");
            List<ErrOrder> orders = testDao.queryWagasErrOrders(hqId, abbDate);
            ErrOrder errOrder;
            for (int i = 0; i < orders.size(); i++) {
                row = sheet.createRow(i + 1);
                errOrder = orders.get(i);
                row.createCell(0).setCellValue(errOrder.getName());
                row.createCell(1).setCellValue(errOrder.getErpCode());
                row.createCell(3).setCellValue(errOrder.getErrItem());
            }
            workbook.write(fos);
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.deleteFile(file);
        }
        log.info("execQueryWagasERPNoSkuItems end");
    }

    public File execQueryDeliveryTime() {
        log.info("execQueryDeliveryTime start");
        File file = fileService.createPickTimeFile(3880, "2020-01-01 00:00:00",
                "2021-01-01 00:00:00", new String[]{"shunfeng"});
        log.info("execQueryDeliveryTime end");
        return file;
    }

    @Data
    public static class DeliveryBranch {
        private String code;
        private String name;
        private String businessHours;
        private String phone;
        private String address;
        private String hasSF;
        private String hasFN;
    }


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

    @Getter
    @Setter
    public static class ErrOrder {
        private String name;
        private String erpCode;
        private String errItem;
    }

    @Getter
    @Setter
    public static class ErrItem {
        private String serviceType;
        private String name;
        List<ErrOption> options;
    }

    @Getter
    @Setter
    public static class ErrOption {
        private String name;
    }

    @Getter
    @Setter
    public static class DeliveryOrder {
        private String city;
        private int yeah;
        private int month;
        private int week;
        private String day;
        private String pickTime;
        private String interval;
        private String no;
        private double waitTime;
    }

    @Getter
    @Setter
    public static class Status {
        private Integer code;
        private String name;
        private String datetime;
    }
}
