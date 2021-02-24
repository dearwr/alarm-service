package com.hchc.alarm.service;

import com.hchc.alarm.constant.DeliveryCompany;
import com.hchc.alarm.dao.hchc.FileDao;
import com.hchc.alarm.dao.hchc.MergeDao;
import com.hchc.alarm.dao.hchc.ShangWeiDao;
import com.hchc.alarm.entity.Product;
import com.hchc.alarm.task.TestTask;
import com.hchc.alarm.util.DatetimeUtil;
import com.hchc.alarm.util.FileUtil;
import com.hchc.alarm.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wangrong
 * @date 2020-07-09
 */
@Service
@Slf4j
public class FileService {

    @Autowired
    private MergeDao mergeDao;
    @Autowired
    private ShangWeiDao shangWeiDao;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private FileDao fileDao;

    public List<String> parse(MultipartFile file, int hqId) throws IOException {
        Workbook workbook = null;
        FileInputStream fis = null;
        List<String> result = new ArrayList<>();
        try {
            fis = (FileInputStream) file.getInputStream();
            if (file.getOriginalFilename().contains(".xlsx")) {
                workbook = new XSSFWorkbook(fis);
            } else {
                workbook = new HSSFWorkbook(fis);
            }
            Sheet sheet = workbook.getSheetAt(0);
            result.addAll(parseAirport(sheet, hqId));
        } catch (Exception e) {
            e.printStackTrace();
            log.info("解析文件报错：" + e.getMessage());
        } finally {
            if (fis != null) {
                fis.close();
            }
            if (workbook != null) {
                workbook.close();
            }
        }
        return result;
    }

    public List<String> parseNewCard(MultipartFile file, int hqId) throws IOException {
        Workbook workbook = null;
        FileInputStream fis = null;
        List<String> result = new ArrayList<>();
        try {
            fis = (FileInputStream) file.getInputStream();
            if (file.getOriginalFilename().contains(".xlsx")) {
                workbook = new XSSFWorkbook(fis);
            } else {
                workbook = new HSSFWorkbook(fis);
            }
            Sheet sheet = workbook.getSheetAt(0);
            result.addAll(parseNewCard(sheet, hqId));
        } catch (Exception e) {
            e.printStackTrace();
            log.info("解析文件报错：" + e.getMessage());
        } finally {
            if (fis != null) {
                fis.close();
            }
            if (workbook != null) {
                workbook.close();
            }
        }
        return result;
    }

    public List<String> parseWaiMaiCode(MultipartFile file, int hqId) throws IOException {
        Workbook workbook = null;
        FileInputStream fis = null;
        List<String> result = new ArrayList<>();
        try {
            fis = (FileInputStream) file.getInputStream();
            if (file.getOriginalFilename().contains(".xlsx")) {
                workbook = new XSSFWorkbook(fis);
            } else {
                workbook = new HSSFWorkbook(fis);
            }
            Sheet sheet = workbook.getSheetAt(0);
            result.addAll(parseWaiMaiCode(sheet, hqId));
        } catch (Exception e) {
            e.printStackTrace();
            log.info("解析文件报错：" + e.getMessage());
        } finally {
            if (fis != null) {
                fis.close();
            }
            if (workbook != null) {
                workbook.close();
            }
        }
        return result;
    }

    private List<String> parseWaiMaiCode(Sheet sheet, int hqId) {
        Map<String, Integer> placeMap = new HashMap<>();
        String cValue;
        // 解析第一行表头
        String product = "商品";
        String code = "SKU";
        Row row = sheet.getRow(0);
        for (int i = 0; i < row.getLastCellNum(); i++) {
            if (row.getCell(i) == null) {
                log.info("cell is empty, sheet:{}, row:{}, col:{}", sheet.getSheetName(), row.getRowNum(), i);
                continue;
            }
            cValue = parseCellValue(row.getCell(i), false);
            if (cValue == null) {
                continue;
            }
            if (product.equals(cValue) || code.equals(cValue)) {
                placeMap.put(cValue, i);
            }
        }
        // 解析第二行开始的数据行
        int productIndex = placeMap.get(product);
        int codeIndex = placeMap.get(code);
        String productStr;
        String codeStr;
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            try {
                row = sheet.getRow(i);
                log.info("sheet:{}, row:{}", sheet.getSheetName(), row.getRowNum());
                productStr = parseCellValue(row.getCell(productIndex), false);
                codeStr = parseCellValue(row.getCell(codeIndex), false);
//                mergeDao.updateWaiMaiCode(productStr, codeStr);
                if (!mergeDao.queryExistName(productStr)) {
                    mergeDao.saveWaimaiNameCode(productStr, codeStr);
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.info("error sheet:{}, row:{}", sheet.getSheetName(), row.getRowNum());
            }
        }
        List<Product> nameList = mergeDao.queryNoCodeProducts();
        if (!CollectionUtils.isEmpty(nameList)) {
            return nameList.stream().map(p -> p.getId() + "  " + p.getName()).collect(Collectors.toList());
        }
        return null;
    }

    private List<String> parseAirport(Sheet sheet, int hqId) {
        List<String> result = new ArrayList<>();
        Map<String, Integer> placeMap = new HashMap<>();
        String cValue;
        // 解析第一行表头
        String flow = "交易号";
        String date = "记账日期";
        String time = "交易时间";
        String pushAmount = "交易金额";
        Row row = sheet.getRow(0);
        for (int i = 0; i < row.getLastCellNum(); i++) {
            if (row.getCell(i) == null) {
                log.info("cell is empty, sheet:{}, row:{}, col:{}", sheet.getSheetName(), row.getRowNum(), i);
                continue;
            }
            cValue = parseCellValue(row.getCell(i), false);
            if (cValue == null) {
                continue;
            }
            if (date.equals(cValue) || time.equals(cValue) || pushAmount.equals(cValue) || flow.equals(cValue)) {
                placeMap.put(cValue, i);
            }
        }
        // 解析第二行开始的数据行
        int flowIndex = placeMap.get(flow);
        int dateIndex = placeMap.get(date);
        int timeIndex = placeMap.get(time);
        int pushAmountIndex = placeMap.get(pushAmount);
        String flowStr;
        String dateStr;
        String timeStr;
        BigDecimal pushAmountStr;
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            try {
                row = sheet.getRow(i);
                log.info("sheet:{}, row:{}", sheet.getSheetName(), row.getRowNum());
                flowStr = parseCellValue(row.getCell(flowIndex), false);
                dateStr = parseCellValue(row.getCell(dateIndex), false);
                timeStr = parseCellValue(row.getCell(timeIndex), true);
                pushAmountStr = BigDecimal.valueOf(Double.parseDouble(parseCellValue(row.getCell(pushAmountIndex), false)));
                mergeDao.saveAirport(dateStr, timeStr, pushAmountStr, flowStr);
            } catch (Exception e) {
                e.printStackTrace();
                log.info("error sheet:{}, row:{}", sheet.getSheetName(), row.getRowNum());
            }
        }
        return result;
    }

    private List<String> parseNewCard(Sheet sheet, int hqId) {
        List<String> result = new ArrayList<>();
        Map<String, Integer> placeMap = new HashMap<>();
        String cValue;
        // 解析第一行表头
        String kId = "KID";
        String cId = "券码";
        String newCard = "新卡";
        String needPush = "上传";
        String balance = "金额";
        Row row = sheet.getRow(0);
        for (int i = 0; i < row.getLastCellNum(); i++) {
            if (row.getCell(i) == null) {
                log.info("cell is empty, sheet:{}, row:{}, col:{}", sheet.getSheetName(), row.getRowNum(), i);
                continue;
            }
            cValue = parseCellValue(row.getCell(i), false);
            if (cValue == null) {
                continue;
            }
            if (kId.equals(cValue) || cId.equals(cValue) || newCard.equals(cValue) || needPush.equals(cValue) || balance.equals(cValue)) {
                placeMap.put(cValue, i);
            }
        }
        // 解析第二行开始的数据行
        int kidIndex = placeMap.get(kId);
        int cIdIndex = placeMap.get(cId);
        int newCardIndex = placeMap.get(newCard);
        int needPushIndex = placeMap.get(needPush);
        int balanceIndex = placeMap.get(balance);
        String kidStr;
        String cidStr;
        String newCardStr;
        String needPushStr;
        BigDecimal balanceStr;
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            try {
                row = sheet.getRow(i);
                log.info("sheet:{}, row:{}", sheet.getSheetName(), row.getRowNum());
                kidStr = parseCellValue(row.getCell(kidIndex), false);
                cidStr = parseCellValue(row.getCell(cIdIndex), false);
                newCardStr = parseCellValue(row.getCell(newCardIndex), false);
                needPushStr = parseCellValue(row.getCell(needPushIndex), false);
                balanceStr = BigDecimal.valueOf(Double.parseDouble(parseCellValue(row.getCell(balanceIndex), false)));

                if ("是".equals(newCardStr)) {
                    shangWeiDao.saveNewCard(kidStr, cidStr, balanceStr);
                    shangWeiDao.saveCardMapping(kidStr, cidStr, 0);
                } else {
                    shangWeiDao.saveCardMapping(kidStr, cidStr, "是".equals(needPushStr) ? 1 : 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.info("error sheet:{}, row:{}", sheet.getSheetName(), row.getRowNum());
            }
        }
        return result;
    }

    private String parseCellValue(Cell cell, boolean isTime) {
        String cValue;
        if (isTime) {
            //用于转化为日期格式
            Date d = cell.getDateCellValue();
            DateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return formater.format(d);

        }
        switch (cell.getCellType()) {
            case NUMERIC:
                cValue = String.valueOf(cell.getNumericCellValue());
//                cValue = cValue.substring(0, cValue.indexOf("."));
                break;
            case STRING:
                cValue = cell.getStringCellValue();
                break;
            default:
                log.info("不支持解析类型为：{}", cell.getCellType());
                cValue = null;
        }
        return cValue;
    }

    public List<String> fillTogoSku() {
        Set<Product> productMapping = new HashSet<>();
        List<Product> codeMappings = mergeDao.queryProductMapping();
        if (!StringUtils.isEmpty(codeMappings)) {
            productMapping.addAll(codeMappings);
        }
        String fetchSkuUrl = "http://120.78.232.8:9500/sync/sku?hqId=3880";
        String result = restTemplate.getForObject(fetchSkuUrl, String.class);
        String[] products = result.split("\n");
        String[] unions;
        String name;
        for (String p : products) {
            log.info(p);
            unions = p.split("\t");
            for (int i = 0; i < unions.length; i++) {
                if (StringUtil.isNotBlank(unions[0])) {
                    name = unions[1];
                    if (unions[1].contains("[")) {
                        name = unions[1].substring(0, unions[1].indexOf("["));
                    }
                    productMapping.add(new Product(name, unions[0]));
                }
            }
        }
        List<Product> noCodeProducts = mergeDao.queryNoCodeProducts();
        if (CollectionUtils.isEmpty(noCodeProducts)) {
            return null;
        }
        for (Product np : noCodeProducts) {
            for (Product mp : productMapping) {
                if (np.getName().contains(mp.getName()) || mp.getName().contains(np.getName())) {
                    mergeDao.updateWaiMaiCode(np.getName(), mp.getCode());
                    break;
                }
            }
        }
        noCodeProducts = mergeDao.queryNoCodeProducts();
        if (!CollectionUtils.isEmpty(noCodeProducts)) {
            return noCodeProducts.stream().map(p -> p.getId() + "  " + p.getName()).collect(Collectors.toList());
        }
        return null;
    }

    public File createPickTimeFile(long hqId, String start, String end, String[] companies) {
        log.info("createPickTimeFile start");
        String fileName = "骑手接单用时情况.xlsx";
        File file;
        try {
            // 创建目录
            file = FileUtils.getFile("/data", "share", "delivery", String.valueOf(hqId), DatetimeUtil.dayText(new Date()));
            if (!file.exists()) {
                file.mkdirs();
            }
            // 创建文件
            file = FileUtils.getFile(file, fileName);
            if (file.exists()) {
                FileUtil.deleteFile(file);
            }
            file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
            log.info("[createPickTimeFile] 创建文件失败");
            return null;
        }
        List<String> platforms = new ArrayList<>();
        try (FileOutputStream fos = new FileOutputStream(file)) {
            Workbook workbook;
            workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("sheet1");

            Row row = sheet.createRow(1);
            row.createCell(0).setCellValue("筛选时间：");
            row.createCell(1).setCellValue(start + "至" + end);

            row = sheet.createRow(4);
            row.createCell(0).setCellValue("城市");
            row.createCell(1).setCellValue("年份");
            row.createCell(2).setCellValue("月份");
            row.createCell(3).setCellValue("星期");
            row.createCell(4).setCellValue("发起配送时间");
            row.createCell(5).setCellValue("配送员接单时间");
            row.createCell(6).setCellValue("发起配送时间段（小时）");
            row.createCell(7).setCellValue("订单号");
            row.createCell(8).setCellValue("接单用时(分钟)");
            row.createCell(9).setCellValue("配送平台");

            int rowIndex = 0;
            String platform;
            for (String company : companies) {
                platform = DeliveryCompany.fetchPlatform(company);
                platforms.add(platform);
                List<TestTask.DeliveryOrder> orders = fileDao.queryDeliveryTime(start, end, hqId, company);
                if (CollectionUtils.isEmpty(orders)) {
                    continue;
                }
                TestTask.DeliveryOrder order;
                for (int i = 0; i < orders.size(); i++) {
                    order = orders.get(i);
                    row = sheet.createRow((rowIndex++) + 5);
                    row.createCell(0).setCellValue(order.getCity());
                    row.createCell(1).setCellValue(order.getYeah());
                    row.createCell(2).setCellValue(order.getMonth());
                    row.createCell(3).setCellValue(order.getWeek());
                    row.createCell(4).setCellValue(order.getDay());
                    row.createCell(5).setCellValue(order.getPickTime());
                    row.createCell(6).setCellValue(order.getInterval());
                    row.createCell(7).setCellValue(order.getNo());
                    row.createCell(8).setCellValue(order.getWaitTime());
                    row.createCell(9).setCellValue(platform);
                }
            }

            row = sheet.createRow(0);
            row.createCell(0).setCellValue("配送平台：");
            row.createCell(1).setCellValue(String.join("、", platforms));

            workbook.write(fos);
            workbook.close();
        } catch (Exception e) {
            log.info("[createPickTimeFile] 发生异常：" + e.getMessage());
            FileUtil.deleteFile(file);
        }
        log.info("createPickTimeFile end");
        return file;
    }
}
