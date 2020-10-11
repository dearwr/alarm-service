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
        String name = "商品";
        String sku = "SKU";
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
            if (name.equals(cValue)  || sku.equals(cValue)) {
                placeMap.put(cValue, i);
            }
        }
        // 解析第二行开始的数据行
        int nameIndex = placeMap.get(name);
        int skuIndex = placeMap.get(sku);
        String pName;
        String pSku;
        String currName = null;
        for (int i = 1; i < sheet.getLastRowNum(); i++) {
            try {
                row = sheet.getRow(i);
                log.info("sheet:{}, row:{}", sheet.getSheetName(), row.getRowNum());
                // 解析sku
                cValue = parseCellValue(row.getCell(nameIndex));
                if (cValue.equals(currName)) {
                    log.info("重复名称跳过：{}", cValue);
                }
                currName = cValue;
                if (cValue == null) {
                    log.info("parse sku cell is null");
                    result.add("parse sku cell is null, row:" + row.getRowNum());
                    continue;
                }
                pName = cValue;
                cValue = parseCellValue(row.getCell(skuIndex));
                if (cValue == null) {
                    log.info("parse balance cell is null");
                    result.add("parse balance cell is null, row:" + row.getRowNum());
                    continue;
                }
                pSku = cValue;
                // 保存记录
                materialBarCodeDao.save(pName, pSku);
            } catch (Exception e) {
                e.printStackTrace();
                log.info("error sheet:{}, row:{}, col:{}", sheet.getSheetName(), row.getRowNum(), nameIndex);
            }
        }
        return result;
    }

    private String parseCellValue(Cell cell) {
        String cValue;
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
