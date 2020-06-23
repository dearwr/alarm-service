//package com.hchc.alarm.task;
//
//import com.hchc.alarm.dao.hchc.VipPointDao;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.io.FileUtils;
//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//import org.springframework.util.CollectionUtils;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.util.List;
//
///**
// * @author wangrong
// * @date 2020-06-23
// */
//@Service
//@Slf4j
//public class CreatePointCardFileTask {
//
//    @Autowired
//    private VipPointDao vipPointDao;
//
//    @Scheduled(cron = " 0 03 18 * * ? ")
//    public void sumHqSize(){
//        log.info("sumHqSize");
//        List<Long> hqList = vipPointDao.queryHqList();
//        List<String[]> records;
//        File recordFile = createFile("集点卡处理结果-品牌汇总表");
//        if (recordFile != null) {
//            // 写并保存记录文件
//            try (FileOutputStream fos = new FileOutputStream(recordFile);
//                 Workbook workbook = new XSSFWorkbook()
//            ) {
//                //判断是否为新文件
//                if (recordFile.length() == 0) {
//                    Sheet sheet = workbook.createSheet();
//                    CellStyle cellStyle = workbook.createCellStyle();
//                    cellStyle.setWrapText(true);
//                    cellStyle.setAlignment(HorizontalAlignment.LEFT);
//                    Row row = sheet.createRow(0);
//                    row.createCell(0).setCellValue("品牌名");
//                    row.createCell(1).setCellValue("处理集点卡数量");
//                    for (int i = 0; i < hqList.size(); i++) {
//                        records = vipPointDao.queryInvalidPointCardsByHqId(hqList.get(i));
//                        if (CollectionUtils.isEmpty(records)) {
//                            log.info("hqId:{} 没有记录", hqList.get(i));
//                        }
//                        row = sheet.createRow(i + 1);
//                        row.createCell(0).setCellValue(records.get(0)[0]);
//                        row.createCell(1).setCellValue(records.size());
//                    }
//                }
//                workbook.write(fos);
//                log.info("[createFile] success file:{}", recordFile.getName());
//            } catch (Exception e) {
//                e.printStackTrace();
//                log.info("[createFile] 保存数据失败 file:{}, reason:{}", recordFile.getName(), e.getMessage());
//            }
//        }
//    }
//
//    @Scheduled(cron = " 0 30 19 * * ? ")
//    public void createFile() throws InterruptedException {
//        List<Long> hqList = vipPointDao.queryHqList();
//        List<String[]> records;
//        File recordFile;
//        Sheet sheet;
//        for (Long hqId : hqList) {
//            records = vipPointDao.queryInvalidPointCardsByHqId(hqId);
//            if (CollectionUtils.isEmpty(records)) {
//                log.info("hqId:{} 没有记录");
//            }
//            recordFile = createFile(records.get(0)[0]);
//            if (recordFile != null) {
//                // 写并保存记录文件
//                try (FileOutputStream fos = new FileOutputStream(recordFile);
//                     Workbook workbook = new XSSFWorkbook()
//                ) {
//                    //判断是否为新文件
//                    if (recordFile.length() == 0) {
//                        sheet = workbook.createSheet();
//                        CellStyle cellStyle = workbook.createCellStyle();
//                        cellStyle.setWrapText(true);
//                        cellStyle.setAlignment(HorizontalAlignment.LEFT);
//                        writeData(sheet, records);
//                    }
//                    workbook.write(fos);
//                    log.info("[createFile] success file:{}", recordFile.getName());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    log.info("[createFile] 保存数据失败 file:{}, reason:{}", recordFile.getName(), e.getMessage());
//                }
//            }
//            Thread.sleep(1000);
//        }
//    }
//
//    private void writeData(Sheet sheet, List<String[]> records) {
//        Row row = sheet.createRow(0);
//        row.createCell(0).setCellValue("品牌名");
//        row.createCell(1).setCellValue("会员号");
//        row.createCell(2).setCellValue("集点卡名");
//        for (int i = 0; i < records.size(); i++) {
//            row = sheet.createRow(i + 1);
//            row.createCell(0).setCellValue(records.get(i)[0]);
//            row.createCell(1).setCellValue(records.get(i)[1]);
//            row.createCell(2).setCellValue(records.get(i)[2]);
//
//        }
//    }
//
//    private File createFile(String hqName) {
//        String fileName = hqName + ".xls";
//        File file;
//        try {
//            // 创建目录
//            file = FileUtils.getFile(FileUtils.getUserDirectory(), "data", "pointCard");
//            if (!file.exists()) {
//                file.mkdirs();
//            }
//            // 创建文件
//            file = FileUtils.getFile(file, fileName);
//            if (!file.exists()) {
//                file.createNewFile();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.info("[createFile] 创建文件{}失败", fileName);
//            return null;
//        }
//        return file;
//    }
//}
