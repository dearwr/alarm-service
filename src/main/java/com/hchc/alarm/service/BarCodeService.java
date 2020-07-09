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
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
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

    public void parseFile(MultipartFile file) throws IOException {
        Workbook workbook = null;
        FileInputStream fis = null;
        try {
            fis = (FileInputStream) file.getInputStream();
            if (file.getOriginalFilename().contains(".xlsx")) {
                workbook = new XSSFWorkbook(fis);
            }else {
                workbook = new HSSFWorkbook(fis);
            }
            Sheet sheet;
            while (workbook.sheetIterator().hasNext()) {
                sheet = workbook.sheetIterator().next();
                parseSheet(sheet);
            }
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
    }

    private void parseSheet(Sheet sheet) {
        Map<String, Integer> placeMap = new HashMap<>();
        String cValue;
        // 解析第一行表头
        String BAR_CODE = "条码";
        String SKU = "商家编码";
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
            if (SKU.equals(cValue) || BAR_CODE.equals(cValue)) {
                placeMap.put(cValue, i);
            }
        }
        // 解析第二行开始的数据行
        int skuIndex = placeMap.get(SKU);
        int barIndex = placeMap.get(BAR_CODE);
        List<Long> idList;
        for (int i = 1; i < sheet.getLastRowNum(); i++) {
            try {
                row = sheet.getRow(i);
                // 解析sku
                log.info("sheet:{}, row:{}, col:{}", sheet.getSheetName(), row.getRowNum(), skuIndex);
                cValue = parseCellValue(row.getCell(skuIndex));
                if (cValue == null) {
                    log.info("parse sku cell is null");
                    continue;
                }
                // 查询sku的id
                idList = materialGroupDao.queryIdByCode(cValue);
                if (CollectionUtils.isEmpty(idList)) {
                    log.info("find sku is empty from db, sku:{}", cValue);
                    continue;
                }
                if (idList.size() > 1) {
                    log.info("find sku size more then 1 from db , sku:{}", cValue);
                    continue;
                }
                // 解析barcode
                cValue = parseCellValue(row.getCell(barIndex));
                if (cValue == null) {
                    log.info("parse barCode cell is null");
                    continue;
                }
                // 保存记录
                if (materialBarCodeDao.save(idList.get(0), cValue) != 1) {
                    log.info("save record fail, groupId:{}, barCode:{}", idList.get(0), cValue);
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.info("error sheet:{}, row:{}, col:{}", sheet.getSheetName(), row.getRowNum(), skuIndex);
            }

        }
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
