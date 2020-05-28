package com.hchc.alarm.service;

import com.hchc.alarm.dao.hchc.MallProductCodeDao;
import com.hchc.alarm.entity.MallProductCodeDO;
import com.hchc.alarm.model.AirportFileBO;
import com.hchc.alarm.pack.MallResponse;
import com.hchc.alarm.util.DatetimeUtil;
import com.hchc.alarm.util.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created 2019/12/30
 *
 * @author wangrong
 */
@Service
@Slf4j
public class AirportFileService {

    @Autowired
    private MallProductCodeDao mallProductCodeDao;
    private final String TIMESTAMP = "yyyyMMddHHmmss";

    public MallResponse parseFile(MultipartFile sourceFile, int hqId, int branchId) {
        Workbook workbook;
        try {
            FileInputStream file = (FileInputStream) sourceFile.getInputStream();
            workbook = new XSSFWorkbook(file);
        } catch (IOException e) {
            log.info("读取文件失败：" + e.getMessage());
            return MallResponse.fail("读取文件失败：" + e.getMessage());
        }
        try {
            // 打开Excel中的第一个Sheet
            Sheet firstSheet = workbook.getSheetAt(0);
            // 读取第一行，记录要读取哪些列
            Map<Integer, String> indexToNameMap = parseFirstRow(firstSheet);
            // 检查文件中未找到对应名称的数据列
            List<String> noColumnNameList = AirportFileBO.nameToParamMap.keySet()
                    .stream()
                    .filter(columnName -> !indexToNameMap.containsValue(columnName))
                    .collect(Collectors.toList());
            if (!noColumnNameList.isEmpty()) {
                return MallResponse.fail("文件中未找到对应名称的数据列，请检查文件格式", noColumnNameList);
            }
            // 从第二行开始读取数据
            List<Map<String, String>> dataList = parseDataRows(firstSheet, indexToNameMap);
            // 将解析的数据转化为数据对象
            List<MallProductCodeDO> mallProductCodeDOList = buildData(dataList, hqId, branchId);
            // 保存到数据库
            boolean isSaved = persistData(mallProductCodeDOList);
            if (isSaved) {
                // 保存上传的文件
                if (saveSourceFile(sourceFile, hqId, branchId)) {
                    return MallResponse.ok();
                }
                return MallResponse.fail("数据库保存数据成功，服务器保存文件失败!");
            } else {
                return MallResponse.fail("数据库保存数据失败!");
            }
        } catch (Exception e) {
            log.info("解析文件报错：" + e.getMessage());
            return MallResponse.fail("解析文件报错：" + e.getMessage());
        }
    }

    /**
     * 解析第一行数据
     *
     * @param sheet
     * @return
     */
    private Map<Integer, String> parseFirstRow(Sheet sheet) {
        Map<Integer, String> indexToNameMap = new HashMap<>();
        Row firstRow = sheet.getRow(0);
        Cell cell;
        String cellVal;
        for (int index = 0; index < firstRow.getLastCellNum(); index++) {
            cell = firstRow.getCell(index);
            cellVal = cell.getRichStringCellValue().getString();
            for (String name : AirportFileBO.nameToParamMap.keySet()) {
                if (name.equals(cellVal)) {
                    indexToNameMap.put(index, cellVal);
                    break;
                }
            }
        }
        return indexToNameMap;
    }

    /**
     * 解析第二行开始的数据行
     *
     * @param sheet
     * @param indexToNameMap
     * @return
     */
    private List<Map<String, String>> parseDataRows(Sheet sheet, Map<Integer, String> indexToNameMap) {
        List<Map<String, String>> dataList = new ArrayList<>();
        Map<String, String> rowDataMap;
        Row dataRow;
        Cell cell;
        String cellVal;
        String columnName;
        String classParam;
        int lastDataRowIndex = sheet.getLastRowNum();
        for (int rowIndex = 1; rowIndex <= lastDataRowIndex; rowIndex++) {
            dataRow = sheet.getRow(rowIndex);
            rowDataMap = new HashMap<>();
            for (Integer index : indexToNameMap.keySet()) {
                cell = dataRow.getCell(index);
                cellVal = convertCellVal(cell);
                columnName = indexToNameMap.get(index);
                classParam = AirportFileBO.nameToParamMap.get(columnName);
                rowDataMap.put(classParam, cellVal);
            }
            dataList.add(rowDataMap);
        }
        return dataList;
    }

    private String convertCellVal(Cell cell) {
        int typeCode = cell.getCellType().getCode();
        String value;
        switch (typeCode) {
            case 1:
                value = cell.getStringCellValue();
                break;
            case 0:
                value = String.valueOf(cell.getNumericCellValue());
                break;
            default:
                value = "";
        }
        return value;
    }

    /**
     * 构建要保存的数据对象
     *
     * @param dataList
     * @return
     */
    private List<MallProductCodeDO> buildData(List<Map<String, String>> dataList, int hqId, int branchId) {
        List<MallProductCodeDO> mallProductCodeDOList = new ArrayList<>();
        MallProductCodeDO mallProductCodeDO;
        for (Map<String, String> rowDataMap : dataList) {
            AirportFileBO airportFileBO = ObjectUtils.getInstanceFromMap(rowDataMap, AirportFileBO.class);
            mallProductCodeDO = new MallProductCodeDO();
            mallProductCodeDO.setHqId(hqId);
            mallProductCodeDO.setBranchId(branchId);
            mallProductCodeDO.setMall(airportFileBO.getShopName());
            String code = airportFileBO.getCode();
            mallProductCodeDO.setCode(code.contains(".") ? code.substring(0, code.indexOf('.')) : code);
            mallProductCodeDO.setMallId(airportFileBO.getSku());
            mallProductCodeDO.setSku(airportFileBO.getSku());
            mallProductCodeDOList.add(mallProductCodeDO);
        }
        return mallProductCodeDOList;
    }

    /**
     * 保存数据到数据库
     *
     * @param mallProductCodeDOList
     * @return
     */
    private boolean persistData(List<MallProductCodeDO> mallProductCodeDOList) {
        MallProductCodeDO mallProductCodeDO = mallProductCodeDOList.get(0);
        List<MallProductCodeDO> existList = mallProductCodeDao.queryExist(mallProductCodeDO);
        List<MallProductCodeDO> needSaveList = new ArrayList<>();
        if (!existList.isEmpty()) {
            String exitSku;
            String existCode;
            boolean isExist;
            for (MallProductCodeDO productCode : mallProductCodeDOList) {
                isExist = false;
                for (MallProductCodeDO existProductCode : existList) {
                    exitSku = existProductCode.getSku();
                    existCode = existProductCode.getCode().startsWith("0") ? existProductCode.getCode().substring(1) : existProductCode.getCode();
                    if (existCode.equals(productCode.getCode()) && exitSku.equals(productCode.getSku())) {
                        isExist = true;
                        break;
                    }
                }
                if (!isExist) {
                    needSaveList.add(productCode);
                }
            }
        } else {
            needSaveList = mallProductCodeDOList;
        }
        try {
            List<String> codeList = needSaveList.stream().map(MallProductCodeDO::getCode).collect(Collectors.toList());
            log.info("保存 count:{}, codeList：{}", codeList.size(), codeList.toArray());
            return mallProductCodeDao.batchSave(needSaveList);
        } catch (Exception e) {
            log.info("数据库保存数据发生异常：" + e.getMessage());
            return false;
        }
    }

    /**
     * 保存上传的文件
     *
     * @param sourceFile
     * @param hqId
     * @param branchId
     * @return
     */
    private boolean saveSourceFile(MultipartFile sourceFile, int hqId, int branchId) {
        String sourceFileName = sourceFile.getOriginalFilename();
        String saveFileName = sourceFileName.replace("airport", "airport-" + DatetimeUtil.format(new Date(), TIMESTAMP));
        try {
            File file = FileUtils.getFile(FileUtils.getUserDirectory(), "data", "mall", "airport", String.valueOf(hqId), String.valueOf(branchId));
            if (!file.exists()) {
                file.mkdirs();
            }
            if (file.isDirectory()) {
                file = FileUtils.getFile(file, saveFileName);
                if (!file.exists()) {
                    file.createNewFile();
                }
            }
            sourceFile.transferTo(file);
        } catch (IOException e) {
            log.info(saveFileName + "文件保存失败：" + e.getMessage());
            return false;
        }
        return true;
    }

}
