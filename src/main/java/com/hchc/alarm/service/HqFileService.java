package com.hchc.alarm.service;

import com.hchc.alarm.model.niceconsole.*;
import com.hchc.alarm.pack.MallResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wangrong
 * @date 2020-07-09
 */
@Service
@Slf4j
public class HqFileService {

    public MallResponse parseFile(MultipartFile hqFile) throws IOException {
        ModelBO model;
        Workbook workbook = null;
        FileInputStream fis = null;
        try {
            fis = (FileInputStream) hqFile.getInputStream();
            workbook = new HSSFWorkbook(fis);
            // 打开Excel中的第一个Sheet
            Sheet firstSheet = workbook.getSheetAt(0);
            if (!checkFirstRow(firstSheet)) {
                log.info("第一行数据不合法");
                return MallResponse.fail("第一行数据不合法");
            }
            model = parseSecondRowData(firstSheet);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("解析文件报错：" + e.getMessage());
            return MallResponse.fail("解析文件报错：" + e.getMessage());
        }finally {
            if (fis != null) {
                fis.close();
            }
            if (workbook != null) {
                workbook.close();
            }
        }
        return MallResponse.ok(model);
    }

    /**
     * 校验第一行的数据是否合法
     * @param firstSheet
     * @return
     */
    private boolean checkFirstRow(Sheet firstSheet) {
        Row row = firstSheet.getRow(0);
        String cValue;
        for (int i = 1; i <= 10; i++) {
            cValue = row.getCell(i).getStringCellValue();
            log.info("第一行，第{}列内容{}", i, cValue);
            if (!cValue.contains(HqFileConstant.FIRST_ROW_MAP.get(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 解析第二行的数据
     * @param firstSheet
     * @return
     */
    private ModelBO parseSecondRowData(Sheet firstSheet) {
        ModelBO modelBO = new ModelBO();
        Row row = firstSheet.getRow(1);
        String cValue = null;
        Cell cell;
        Map<Integer, String> dataMap = new HashMap<>();
        for (int i = 1; i <= 10; i++) {
            if (row.getCell(i) == null) {
                continue;
            }
            cell = row.getCell(i);
            switch (cell.getCellType()){
                case NUMERIC:
                    cValue = String.valueOf(cell.getNumericCellValue());
                    cValue = cValue.substring(0, cValue.indexOf("."));
                    break;
                case STRING:
                    cValue = cell.getStringCellValue();
                    break;
                default:
                    log.info("解析出错，类型为：{}", cell.getCellType());
            }
            log.info("第二行，第{}列内容{}", i, cValue);
            if (StringUtils.isEmpty(cValue)) {
                continue;
            }
            dataMap.put(i, cValue);
        }

        HqBO hqBO = new HqBO();
        String categoryIndex = dataMap.get(1);
        if (categoryIndex != null) {
            hqBO.setCategory(HqFileConstant.CATEGORY_MAP.get(categoryIndex));
        }
        hqBO.setHqDay(dataMap.get(6));

        BranchBO branchBO = new BranchBO();
        branchBO.setName(dataMap.get(2));
        branchBO.setAddress(dataMap.get(7));
        branchBO.setBusinessHours(dataMap.get(8));
        branchBO.setProvince(dataMap.get(9));
        branchBO.setCity(dataMap.get(10));

        SuperAdminBO superAdminBO = new SuperAdminBO();
        superAdminBO.setFullname(dataMap.get(3));
        superAdminBO.setMobile(dataMap.get(4));

        modelBO.setAdminPassword(dataMap.get(5));
        modelBO.setHq(hqBO);
        modelBO.setBranch(branchBO);
        modelBO.setSuperAdmin(superAdminBO);
        return modelBO;
    }
}
