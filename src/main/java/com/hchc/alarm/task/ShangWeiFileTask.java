package com.hchc.alarm.task;

import com.hchc.alarm.dao.hchc.ShangWeiDao;
import com.hchc.alarm.entity.ShangWeiCard;
import com.hchc.alarm.entity.ShangWeiFileRecord;
import com.hchc.alarm.util.DatetimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * @author wangrong
 * @date 2020-11-23
 */
@Service
@Slf4j
public class ShangWeiFileTask {

    @Autowired
    private ShangWeiDao shangWeiDao;

    @Scheduled(cron = "0 30 09 * * ?")
    public void createFile() {
        log.info("********* start create shangwei file *************");
        long hqId = 3880;
        try {
            createReportFile(hqId, new Date());
            createBalanceDetailFile(hqId, new Date());
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("********* end create shangwei file *************");
    }

    private File createReportFile(long hqId, Date date) {
        String fileName = "截止" + DatetimeUtil.dayText(date) + "零点预付卡数据上报.xlsx";
        File file = FileUtils.getFile("/data", "share", "prepaid", "shangwei", String.valueOf(hqId), "report", fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            Workbook workbook;
            workbook = new XSSFWorkbook();

            String dayText = DatetimeUtil.dayText(date);
            createReportSheet(workbook, "上报", hqId, dayText);
            createNewCardSheet(workbook, "新增开卡记录", hqId, dayText);
            createTransactionSheet(workbook, "新增交易记录", hqId, dayText);

            workbook.write(fos);
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
            deleteFile(file);
        }
        return file;
    }

    private void createReportSheet(Workbook workbook, String sheetName, long hqId, String date) {
        Sheet sheet = workbook.createSheet(sheetName);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(1, true);
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("报送日期");
        row.createCell(1).setCellValue("文件流水号");
        row.createCell(2).setCellValue("文件大小");
        row.createCell(3).setCellValue("文件MD5");
        row.createCell(4).setCellValue("文件上传状态");
        row.createCell(5).setCellValue("错误信息描述");
        row.createCell(6).setCellValue("当前预收资金总面额");
        row.createCell(7).setCellValue("当前预收资金总本金");
        row.createCell(8).setCellValue("新增发卡总数量");

        ShangWeiFileRecord fileRecord = shangWeiDao.queryFileRecord(hqId, date);
        row = sheet.createRow(1);
        row.createCell(0).setCellValue(date);
        row.createCell(1).setCellValue(fileRecord.getUploadFlowNo());
        row.createCell(2).setCellValue(fileRecord.getLength());
        row.createCell(3).setCellValue(fileRecord.getMd5());
        row.createCell(4).setCellValue("上传成功");
        row.createCell(5).setCellValue("上报成功");
        row.createCell(6).setCellValue(fileRecord.getTotalBalance());
        row.createCell(7).setCellValue(fileRecord.getTotalBalance());
        row.createCell(8).setCellValue(fileRecord.getCardSize());

        setCenterStyle(workbook, sheet);
    }

    private void createNewCardSheet(Workbook workbook, String sheetName, long hqId, String date) {
        Sheet sheet = workbook.createSheet(sheetName);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(1, true);
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("卡号");
        row.createCell(1).setCellValue("类型");
        row.createCell(2).setCellValue("卡本金");
        row.createCell(3).setCellValue("卡面额");

        List<ShangWeiCard> cards = shangWeiDao.queryNewGiftCards(hqId, date);
        int rowIndex = fillNewCardSheet(cards, sheet, 1);
        cards = shangWeiDao.queryNewVipCards(hqId, date);
        fillNewCardSheet(cards, sheet, rowIndex);
    }

    private void createTransactionSheet(Workbook workbook, String sheetName, long hqId, String date) {
        Sheet sheet = workbook.createSheet(sheetName);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(1, true);
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("卡号");
        row.createCell(1).setCellValue("卡类型");
        row.createCell(2).setCellValue("交易前卡本金");
        row.createCell(3).setCellValue("交易前卡面额");
        row.createCell(4).setCellValue("交易本金");
        row.createCell(5).setCellValue("交易面额");
        row.createCell(6).setCellValue("交易后本金");
        row.createCell(7).setCellValue("交易后卡面额");

        List<ShangWeiCard> cards = shangWeiDao.queryGiftCardsTrs(hqId, date);
        int rowIndex = fillTransactionSheet(cards, sheet, 1);
        cards = shangWeiDao.queryVipCardTrs(hqId, date);
        fillTransactionSheet(cards, sheet, rowIndex);
    }

    private int fillNewCardSheet(List<ShangWeiCard> cards, Sheet sheet, int startIndex) {
        ShangWeiCard card;
        Row row;
        for (int i = startIndex; i < cards.size() + startIndex; i++) {
            card = cards.get(i - startIndex);
            row = sheet.createRow(i);
            row.createCell(0).setCellValue(card.getNo());
            row.createCell(1).setCellValue(card.getType());
            row.createCell(2).setCellValue("0");
            row.createCell(3).setCellValue("0");
        }
        return startIndex + cards.size() - 1;
    }

    private int fillTransactionSheet(List<ShangWeiCard> cards, Sheet sheet, int startIndex) {
        Row row;
        ShangWeiCard card;
        for (int i = startIndex; i < cards.size() + startIndex; i++) {
            card = cards.get(i - startIndex);
            row = sheet.createRow(i);
            row.createCell(0).setCellValue(card.getNo());
            row.createCell(1).setCellValue(card.getType());
            row.createCell(2).setCellValue(card.getBeforeBalance());
            row.createCell(3).setCellValue(card.getBeforeBalance());
            row.createCell(4).setCellValue(card.getBalance());
            row.createCell(5).setCellValue(card.getBalance());
            row.createCell(6).setCellValue(card.getAfterBalance());
            row.createCell(7).setCellValue(card.getAfterBalance());
        }
        return startIndex + cards.size() - 1;
    }

    private File createBalanceDetailFile(long hqId, Date date) {
        String fileName = "截止" + DatetimeUtil.dayText(date) + "零点预付卡余额明细表.xlsx";
        File file = FileUtils.getFile("/data", "share", "prepaid", "shangwei", String.valueOf(hqId), "report", fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            Workbook workbook;
            workbook = new XSSFWorkbook();

            String dayText = DatetimeUtil.dayText(date);
            createBalanceDetailSheet(workbook, "预付卡余额明细", hqId, dayText);
            workbook.write(fos);
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
            deleteFile(file);
        }
        return file;
    }

    private void createBalanceDetailSheet(Workbook workbook, String sheetName, long hqId, String dayText) throws ParseException {
        ShangWeiFileRecord record = shangWeiDao.queryFileRecord(hqId, dayText);
        if (record == null) {
            return;
        }
        java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
        nf.setGroupingUsed(false);
        Sheet sheet = workbook.createSheet(sheetName);
        for (int i = 0; i <= 3; i++) {
            sheet.setColumnWidth(i, 18 * 256);
        }
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(1, true);
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("#预付卡余额统计表");
        row = sheet.createRow(1);
        row.createCell(0).setCellValue("#商户名称：Wagas Group");
        row = sheet.createRow(2);
        String dayStr = DatetimeUtil.format(DatetimeUtil.parseDayText(dayText), "yyyy/MM/dd");
        row.createCell(0).setCellValue("#数据周期：[截至" + dayStr + " 00:00:00]");
        row = sheet.createRow(3);
        dayStr = DatetimeUtil.format(new Date(), "yyyy/MM/dd HH:mm:ss");
        row.createCell(0).setCellValue("#导出日期：[" + dayStr + "]");
        row = sheet.createRow(5);
        row.createCell(0).setCellValue("#--------------------数据汇总--------------------");
        row = sheet.createRow(6);
        row.createCell(0).setCellValue("预收资金总本金：：" + nf.format(record.getTotalBalance()));
        row = sheet.createRow(7);
        row.createCell(0).setCellValue("预收资金总面额：" + nf.format(record.getTotalBalance()));
        row = sheet.createRow(9);
        row.createCell(0).setCellValue("#--------------------数据说明--------------------");
        row = sheet.createRow(10);
        row.createCell(0).setCellValue("迁移实体卡：前系统发放的芯片卡及二维码卡；电子会员卡：新系统中电子会员卡，" +
                "包含迁移过来的余额及新系统中充值；礼品卡：新系统的礼品卡");
        row = sheet.createRow(12);
        row.createCell(0).setCellValue("#--------------------明细列表--------------------");
        row = sheet.createRow(13);
        row.createCell(0).setCellValue("卡号");
        row.createCell(1).setCellValue("卡类型");
        row.createCell(2).setCellValue("本金余额");
        row.createCell(3).setCellValue("面额余额");

        String abbDate = DatetimeUtil.dayText(DatetimeUtil.addDay(new Date(), -1));
        List<ShangWeiCard> cards = shangWeiDao.queryGiftCardBalance(hqId, abbDate);
        int startWriteRow = writeBalanceDetailData(sheet, 14, cards);
        cards = shangWeiDao.querySystemCardBalance(hqId, abbDate);
        startWriteRow = writeBalanceDetailData(sheet, startWriteRow, cards);
        cards = shangWeiDao.queryMigrateCardBalance(hqId, abbDate);
        startWriteRow = writeBalanceDetailData(sheet, startWriteRow, cards);
        row = sheet.createRow(startWriteRow);
        row.createCell(2).setCellValue(nf.format(record.getTotalBalance()));
        row.createCell(3).setCellValue(nf.format(record.getTotalBalance()));

    }

    private int writeBalanceDetailData(Sheet sheet, int startWriteRow, List<ShangWeiCard> cards) {
        Row row;
        ShangWeiCard card;
        for (int i = 0; i < cards.size(); i++) {
            card = cards.get(i);
            row = sheet.createRow(i + startWriteRow);
            row.createCell(0).setCellValue(card.getNo());
            row.createCell(1).setCellValue(card.getType());
            row.createCell(2).setCellValue(card.getBalance());
            row.createCell(3).setCellValue(card.getBalance());
        }
        return startWriteRow + cards.size();
    }

    /**
     * 删除文件
     *
     * @param file
     */
    private void deleteFile(File file) {
        try {
            FileUtils.forceDelete(file);
            log.info("[deleteFile] 删除文件成功 filePath:{}", file.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setCenterStyle(Workbook workbook, Sheet sheet) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setWrapText(true);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        for (int i = 0; i < sheet.getLastRowNum(); i++) {
            for (int j = 0; j < sheet.getRow(i).getLastCellNum(); j++) {
                sheet.getRow(i).getCell(j).setCellStyle(cellStyle);
            }
        }
    }

}
