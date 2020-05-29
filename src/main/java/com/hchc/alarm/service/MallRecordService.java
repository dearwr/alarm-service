package com.hchc.alarm.service;

import com.alibaba.fastjson.JSON;
import com.hchc.alarm.dao.hchc.MallRecordDao;
import com.hchc.alarm.model.BranchCheckBO;
import com.hchc.alarm.model.CheckOrderBO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * @author wangrong
 * @date 2020-05-27
 */
@Service
@Slf4j
public class MallRecordService {

    @Autowired
    private MallRecordDao mallRecordDao;

    /**
     * 检查商场数据并保存到文件
     *
     * @param branchCheckBO
     * @return
     */
    public String checkDataAndSaveToFile(BranchCheckBO branchCheckBO) {
        log.info("[checkDataAndSaveToFile] param:{}", JSON.toJSONString(branchCheckBO));
        List<CheckOrderBO> failOrders = mallRecordDao.queryPushFailOrders(branchCheckBO);
        File recordFile = getRecordFile(branchCheckBO);
        if (recordFile == null) {
            log.info("[checkDataAndSaveToFile] 获取记录文件失败");
            return "getRecordFile is null";
        }
        // 写并保存记录文件
        try (FileInputStream fi = new FileInputStream(recordFile);
             FileOutputStream fos = new FileOutputStream(recordFile)) {
            Workbook workbook;
            Sheet firstSheet;
            //判断是否为新文件
            if (recordFile.length() == 0) {
                workbook = new XSSFWorkbook();
                firstSheet = workbook.createSheet("checkSheet");
                firstSheet.setDefaultRowHeight((short) (14 * 20));
                CellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setWrapText(true);
                cellStyle.setAlignment(HorizontalAlignment.LEFT);
                writeFirstRow(firstSheet);
            } else {
                workbook = new XSSFWorkbook(fi);
                firstSheet = workbook.getSheet("checkSheet");
            }
            writeDataRow(firstSheet, failOrders, branchCheckBO);
            // 行宽自适应
            for (int i = 0; i <= 9; i++) {
                if (3 == i || 2 == i) {
                    firstSheet.setColumnWidth(i, 22 * 256);
                } else if (i >= 5) {
                    firstSheet.autoSizeColumn(i);
                } else {
                    firstSheet.setColumnWidth(i, 11 * 256);
                }
            }
            workbook.write(fos);
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
            log.info("[checkDataAndSaveToFile]保存数据失败 file:{}, reason:{}", recordFile.getAbsolutePath(), e.getMessage());
            return e.getMessage();
        }
        return "suc";
    }

    /**
     * 获取记录文件
     *
     * @param b
     * @return
     */
    private File getRecordFile(BranchCheckBO b) {
        String fileName = b.getMall() + "-" + b.getBranchId() + ".xls";
        File file;
        try {
            // 创建目录
            file = FileUtils.getFile(FileUtils.getUserDirectory(), "data", "mall", "check", b.getStartText().substring(0, 6));
            if (!file.exists()) {
                file.mkdirs();
            }
            // 创建文件
            file = FileUtils.getFile(file, fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("[getRecordFile] 创建目录失败");
            return null;
        }
        return file;
    }

    /**
     * 写第一行
     *
     * @param sheet
     */
    private void writeFirstRow(Sheet sheet) {
        Row firstRow = sheet.createRow(0);
        firstRow.createCell(0).setCellValue("日期");
        firstRow.createCell(1).setCellValue("品牌");
        firstRow.createCell(2).setCellValue("门店");
        firstRow.createCell(3).setCellValue("商场");
        firstRow.createCell(4).setCellValue("失败数");
        firstRow.createCell(5).setCellValue("订单号");
        firstRow.createCell(6).setCellValue("订单状态");
        firstRow.createCell(7).setCellValue("订单类型");
        firstRow.createCell(8).setCellValue("推送状态");
        firstRow.createCell(9).setCellValue("失败原因");
    }

    /**
     * 写数据行
     *
     * @param sheet
     * @param failOrders
     * @param branchCheckBO
     */
    private void writeDataRow(Sheet sheet, List<CheckOrderBO> failOrders, BranchCheckBO branchCheckBO) {
        int startRow = sheet.getLastRowNum() + 1;
        int endRow = startRow;
        Row writeRow = sheet.createRow(startRow);
        writeRow.createCell(0).setCellValue(branchCheckBO.getStartText());
        writeRow.createCell(1).setCellValue(branchCheckBO.getBrandName());
        writeRow.createCell(2).setCellValue(branchCheckBO.getBranchName());
        writeRow.createCell(3).setCellValue(branchCheckBO.getMall());
        if (CollectionUtils.isEmpty(failOrders)) {
            writeRow.createCell(4).setCellValue("无");
            writeRow.createCell(5).setCellValue("无");
            writeRow.createCell(6).setCellValue("无");
            writeRow.createCell(7).setCellValue("无");
            writeRow.createCell(8).setCellValue("无");
            writeRow.createCell(9).setCellValue("无");
        } else {
            writeRow.createCell(4).setCellValue(failOrders.size());
            for (CheckOrderBO order : failOrders) {
                writeRow.createCell(5).setCellValue(order.getOrderNo());
                writeRow.createCell(6).setCellValue(order.getOrderStatus());
                writeRow.createCell(7).setCellValue(order.getPlatform());
                writeRow.createCell(8).setCellValue(order.getPushStatus());
                writeRow.createCell(9).setCellValue(order.getPushRemark());
                writeRow = sheet.createRow(++endRow);
            }
            // 合并单元格
            CellRangeAddress region;
            for (int i = 0; i <= 4; i++) {
                region = new CellRangeAddress(startRow, endRow - 1, i, i);
                sheet.addMergedRegion(region);
            }
        }
    }

}
