package com.hchc.alarm.service;

import com.hchc.alarm.dao.hchc.MaterialBarCodeDao;
import com.hchc.alarm.dao.hchc.MaterialGroupDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author wangrong
 * @date 2020-07-09
 */
@Service
@Slf4j
public class BarCodeService {

    @Autowired
    private MaterialGroupDao materialGroupDao;
    @Autowired
    private MaterialBarCodeDao materialBarCodeDao;

    public List<String> parseFile(MultipartFile file, int hqId) throws IOException {
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
//            result.addAll(parseCardMapping(sheet, hqId));
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

    private List<String> parseNewCard(Sheet sheet, int hqId) {
        List<String> result = new ArrayList<>();
        Map<String, Integer> placeMap = new HashMap<>();
        String cValue;
        // 解析第一行表头
        String kId = "卡号";
        String cId = "券码";
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
            if (kId.equals(cValue) || cId.equals(cValue)) {
                placeMap.put(cValue, i);
            }
        }
        // 解析第二行开始的数据行
        int kidIndex = placeMap.get(kId);
        int cIdIndex = placeMap.get(cId);
        String kidStr;
        String cidStr;
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            try {
                row = sheet.getRow(i);
                log.info("sheet:{}, row:{}", sheet.getSheetName(), row.getRowNum());
                // 解析kid
                cValue = parseCellValue(row.getCell(kidIndex), false);
                if (cValue == null) {
                    log.info("parse sku cell is null");
                    result.add("parse sku cell is null, row:" + row.getRowNum());
                    continue;
                }
                kidStr = cValue;
                cValue = parseCellValue(row.getCell(cIdIndex), false);
                if (cValue == null) {
                    log.info("parse balance cell is null");
                    result.add("parse balance cell is null, row:" + row.getRowNum());
                    continue;
                }
                cidStr = cValue;
                // 保存记录
                materialBarCodeDao.save(kidStr, cidStr);
            } catch (Exception e) {
                e.printStackTrace();
                log.info("error sheet:{}, row:{}", sheet.getSheetName(), row.getRowNum());
            }
        }
        return result;
    }

    private List<String> parseCardMapping(Sheet sheet, int hqId) {
        List<String> result = new ArrayList<>();
        Map<String, Integer> placeMap = new HashMap<>();
        String cValue;
        // 解析第一行表头
        String kId = "卡号";
        String cId = "券码";
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
            if (kId.equals(cValue) || cId.equals(cValue)) {
                placeMap.put(cValue, i);
            }
        }
        // 解析第二行开始的数据行
        int kidIndex = placeMap.get(kId);
        int cIdIndex = placeMap.get(cId);
        String kidStr;
        String cidStr;
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            try {
                row = sheet.getRow(i);
                log.info("sheet:{}, row:{}", sheet.getSheetName(), row.getRowNum());
                // 解析kid
                cValue = parseCellValue(row.getCell(kidIndex), false);
                if (cValue == null) {
                    log.info("parse sku cell is null");
                    result.add("parse sku cell is null, row:" + row.getRowNum());
                    continue;
                }
                kidStr = cValue;
                cValue = parseCellValue(row.getCell(cIdIndex), false);
                if (cValue == null) {
                    log.info("parse balance cell is null");
                    result.add("parse balance cell is null, row:" + row.getRowNum());
                    continue;
                }
                cidStr = cValue;
                // 保存记录
                materialBarCodeDao.save(kidStr, cidStr, 0);
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

}
