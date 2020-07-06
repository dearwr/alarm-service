package com.hchc.alarm.controller;

import com.hchc.alarm.dao.hchc.MallProductCodeDao;
import com.hchc.alarm.pack.MallResponse;
import com.hchc.alarm.service.AirportFileService;
import com.hchc.alarm.util.DatetimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.util.Date;

/**
 * @author wangrong
 */
@RestController
@RequestMapping("/file")
@Slf4j
public class AirportFileController {

    @Autowired
    private AirportFileService airportFileService;
    @Autowired
    private MallProductCodeDao mallProductCodeDao;

    @PostMapping("/upload")
    public MallResponse fileUpload(MultipartFile sourceFile, int hqId, int branchId) {
        if (hqId < 0 || branchId < 0) {
            log.info("hqId or branchId 小于0");
            return MallResponse.fail("hqId or branchId 不能小于0");
        }
        if (sourceFile == null || sourceFile.isEmpty()) {
            log.info("上传文件为空");
            return MallResponse.fail("你没有选择要上传的文件");
        }
        String fileName = sourceFile.getOriginalFilename();
        log.info("[fileUpload] fileName{}, hqId:{}, branchId:{}", fileName, hqId, branchId);
        if ("airport.xlsx".equals(fileName) || "airport.xls".equals(fileName)) {
            return airportFileService.parseFile(sourceFile, hqId, branchId);
        } else {
            return MallResponse.fail("不支持解析该文件，请检查文件名");
        }
    }

    @GetMapping("/deleteRecord")
    public MallResponse deleteRecord(int hqId, int branchId, String time) {
        log.info("[deleteRecord] hqId:{}, branchId:{}, time:{}", hqId, branchId, time);
        Date createTime;
        try {
            createTime = DatetimeUtil.parse(time);
        } catch (ParseException e) {
            log.info("[deleteRecord] parse timeString error: {}", e.getMessage());
            return MallResponse.fail(e.getMessage());
        }
        mallProductCodeDao.deleteRecord(hqId, branchId, createTime);
        return MallResponse.ok();
    }

    @GetMapping("/changeSku")
    public MallResponse changeSku(int branchId, String code, String sku) {
        int result = mallProductCodeDao.updateSku(branchId, code, sku);
        if (result > 0) {
            return MallResponse.ok();
        }
        return MallResponse.fail();
    }

}
