package com.hchc.alarm.service;

import com.hchc.alarm.dao.hchc.MergeDao;
import com.hchc.alarm.dao.hchc.ShangWeiDao;
import com.hchc.alarm.entity.Product;
import com.hchc.alarm.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
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

import java.io.FileInputStream;
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
}
