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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            result.addAll(parseSheet(sheet, hqId));
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

    private List<String> parseSheet(Sheet sheet, int hqId) {
        List<String> result = new ArrayList<>();
        Map<String, Integer> placeMap = new HashMap<>();
        String cValue;
        // 解析第一行表头
        String f_number = "f_number";
        String f_card_id = "f_card_id";
        String f_need_push = "f_need_push";
        Row row = sheet.getRow(0);
        for (int i = 0; i < row.getLastCellNum(); i++) {
            if (row.getCell(i) == null) {
                log.info("cell is empty, sheet:{}, row:{}, col:{}", sheet.getSheetName(), row.getRowNum(), i);
                continue;
            }
            cValue = parseCellValue(row.getCell(i));
            if (cValue == null) {
                continue;
            }
            if (f_card_id.equals(cValue) || f_number.equals(cValue) || f_need_push.equals(cValue)) {
                placeMap.put(cValue, i);
            }
        }
        // 解析第二行开始的数据行
        int cardIdIndex = placeMap.get(f_card_id);
        int numberIndex = placeMap.get(f_number);
        int needPushIndex = placeMap.get(f_need_push);
        List<Long> idList;
        String number;
        String cardId;
        int needPush = 0;
        for (int i = 1; i < sheet.getLastRowNum(); i++) {
            try {
                row = sheet.getRow(i);
                log.info("sheet:{}, row:{}", sheet.getSheetName(), row.getRowNum());
                // 解析sku
                cValue = parseCellValue(row.getCell(cardIdIndex));
                if (cValue == null) {
                    log.info("parse sku cell is null");
                    result.add("parse sku cell is null, row:" + row.getRowNum());
                    continue;
                }
                cardId = cValue;
                // 解析suite
                cValue = parseCellValue(row.getCell(needPushIndex));
                if ("1".equals(cValue)) {
                    needPush = 1;
                }
                // 解析barcode
                cValue = parseCellValue(row.getCell(numberIndex));
                if (cValue == null) {
                    log.info("parse barCode cell is null");
                    result.add("parse barCode cell is null, row:" + row.getRowNum());
                    continue;
                }
                number = cValue;
                // 保存记录
                materialBarCodeDao.save(number, cardId, needPush);
            } catch (Exception e) {
                e.printStackTrace();
                log.info("error sheet:{}, row:{}, col:{}", sheet.getSheetName(), row.getRowNum(), cardIdIndex);
            }
        }
        return result;
    }

    private String parseCellValue(Cell cell) {
        String cValue;
        switch (cell.getCellType()) {
            case NUMERIC:
                cValue = String.valueOf(cell.getNumericCellValue());
                cValue = cValue.substring(0, cValue.indexOf("."));
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
