package com.hchc.alarm.controller;

import com.hchc.alarm.pack.MallResponse;
import com.hchc.alarm.service.AirportFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author wangrong
 */
@RestController
@RequestMapping("/file")
@Slf4j
public class AirportFileController {

    @Autowired
    private AirportFileService airportFileService;

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

}
