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
        String BAR_CODE = "条码";
        String SKU = "商家编码";
        String SUITE = "是否组合装";
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
            if (SKU.equals(cValue) || BAR_CODE.equals(cValue) || SUITE.equals(cValue)) {
                placeMap.put(cValue, i);
            }
        }
        // 解析第二行开始的数据行
        int skuIndex = placeMap.get(SKU);
        int barIndex = placeMap.get(BAR_CODE);
        int suiteIndex = placeMap.get(SUITE);
        List<Long> idList;
        String sku;
        boolean isSuit;
        for (int i = 1; i < sheet.getLastRowNum(); i++) {
            isSuit = false;
            try {
                row = sheet.getRow(i);
                log.info("sheet:{}, row:{}", sheet.getSheetName(), row.getRowNum());
                // 解析sku
                cValue = parseCellValue(row.getCell(skuIndex));
                if (cValue == null) {
                    log.info("parse sku cell is null");
                    result.add("parse sku cell is null, row:" + row.getRowNum());
                    continue;
                }
                sku = cValue;
                // 解析suite
                cValue = parseCellValue(row.getCell(suiteIndex));
                if ("是".equals(cValue)) {
                    isSuit = true;
                    idList = materialGroupDao.querySuitProductIdBySku(sku, hqId);
                    if (CollectionUtils.isEmpty(idList)) {
                        log.info("find productId is empty for spu:{}", sku);
                        result.add("find productId is empty for spu:" + sku);
                        continue;
                    }
                    if (idList.size() > 1) {
                        log.info("find productId size more then 1 from db , sku:{}", sku);
                        result.add("find productId size more then 1 from db , sku:" + sku);
                        continue;
                    }
                } else {
                    // 查询sku的id
                    idList = materialGroupDao.queryIdByCode(sku, hqId);
                    if (CollectionUtils.isEmpty(idList)) {
                        log.info("find skuId is empty from db, sku:{}", sku);
                        result.add("find skuId is empty for sku:" + sku);
                        continue;
                    }
                    if (idList.size() > 1) {
                        log.info("find skuId size more then 1 from db , sku:{}", sku);
                        result.add("find skuId size more then 1 from db , sku:" + sku);
                        continue;
                    }
                }
                // 解析barcode
                cValue = parseCellValue(row.getCell(barIndex));
                if (cValue == null) {
                    log.info("parse barCode cell is null");
                    result.add("parse barCode cell is null, row:" + row.getRowNum());
                    continue;
                }
                if (materialBarCodeDao.queryExist(idList.get(0), cValue)) {
                    log.info("groupId:{}, barCode:{} is exist ", idList.get(0), cValue);
                    continue;
                }
                // 保存记录
                if (isSuit) {
                    materialBarCodeDao.save(idList.get(0), cValue, "SUITE");
                }else {
                    materialBarCodeDao.save(idList.get(0), cValue);
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.info("error sheet:{}, row:{}, col:{}", sheet.getSheetName(), row.getRowNum(), skuIndex);
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
