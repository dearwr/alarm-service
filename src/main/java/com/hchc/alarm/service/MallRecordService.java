package com.hchc.alarm.service;

import com.alibaba.fastjson.JSON;
import com.hchc.alarm.dao.hchc.MallRecordDao;
import com.hchc.alarm.model.BranchCheckBO;
import com.hchc.alarm.model.CheckOrderBO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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

    public String checkMallDataAndSave(BranchCheckBO branchCheckBO) {
        log.info("[checkMallDataAndSave] param:{}", JSON.toJSONString(branchCheckBO));
        List<CheckOrderBO> failOrders = mallRecordDao.queryPushFailOrders(branchCheckBO);
        File recordFile = getRecordFile(branchCheckBO);
        if (recordFile == null) {
            log.info("[checkMallDataAndSave] getRecordFile is null");
            return "getRecordFile is null";
        }
        FileInputStream fi = null;
        FileOutputStream fos = null;
        try {
            fi = new FileInputStream(recordFile);
            Workbook workbook = new XSSFWorkbook(fi);
            // 打开第一个Sheet
            Sheet firstSheet = null;
            if (workbook.sheetIterator().hasNext()) {
                firstSheet = workbook.sheetIterator().next();
            }
            if (firstSheet == null) {
                firstSheet = workbook.createSheet();
            }
            firstSheet.setDefaultRowHeight((short) 10);
            // 判断是否是新表
            if (firstSheet.getLastRowNum() > 1) {
                writeDataRow(firstSheet.getLastRowNum() + 1, firstSheet, failOrders, branchCheckBO);
            } else {
                writeInNewSheet(firstSheet, failOrders, branchCheckBO);
            }
            fos = new FileOutputStream(recordFile);
            workbook.write(fos);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("[checkMallDataAndSave]读取文件失败 file:{}, reason:{}", recordFile.getAbsolutePath(), e.getMessage());
            return e.getMessage();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fi != null) {
                try {
                    fi.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "suc";
    }

    private File getRecordFile(BranchCheckBO b) {
        String fileName = b.getMall() + "-" + b.getBranchId() + ".xls";
        File file;
        FileOutputStream fos = null;
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
                fos = new FileOutputStream(file);
                new XSSFWorkbook().write(fos);
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.info("[getRecordFile] error:{}", e.getMessage());
            return null;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }

    private void writeInNewSheet(Sheet sheet, List<CheckOrderBO> failOrders, BranchCheckBO branchCheckBO) {
        writeFirstRow(sheet);
        writeDataRow(1, sheet, failOrders, branchCheckBO);
    }

    private void writeFirstRow(Sheet sheet) {
        Row firstRow = sheet.createRow(0);
        firstRow.createCell(0).setCellValue("日期");
        firstRow.createCell(1).setCellValue("品牌");
        firstRow.createCell(2).setCellValue("门店");
        firstRow.createCell(3).setCellValue("商场");
        firstRow.createCell(4).setCellValue("失败数");
        firstRow.createCell(5).setCellValue("失败原因");
        firstRow.createCell(6).setCellValue("订单号");
    }

    private void writeDataRow(int index, Sheet sheet, List<CheckOrderBO> failOrders, BranchCheckBO branchCheckBO) {
        int size;
        Set<String> reasons = new HashSet<>();
        List<String> orderNos = new ArrayList<>();
        if (CollectionUtils.isEmpty(failOrders)) {
            size = 0;
        } else {
            size = failOrders.size();
            failOrders.stream()
                    .peek(order -> {
                        if (order.getPushRemark() != null) {
                            reasons.add(order.getPushRemark());
                        }
                        orderNos.add(order.getOrderNo());
                    });
        }
        Row writeRow = sheet.createRow(index);
        writeRow.setHeight((short) (10 * size));
        writeRow.createCell(0).setCellValue(branchCheckBO.getStartText());
        writeRow.createCell(1).setCellValue(branchCheckBO.getBrandName());
        writeRow.createCell(2).setCellValue(branchCheckBO.getBranchName());
        writeRow.createCell(3).setCellValue(branchCheckBO.getMall());
        writeRow.createCell(4).setCellValue(size);
        writeRow.createCell(5).setCellValue(String.join("、", reasons) == null ? "无" : String.join("、", reasons));
        writeRow.createCell(6).setCellValue(String.join("、", orderNos) == null ? "无" : String.join("、", orderNos));
    }

}
