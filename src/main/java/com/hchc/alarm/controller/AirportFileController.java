package com.hchc.alarm.controller;

import com.hchc.alarm.pack.MallResponse;
import com.hchc.alarm.service.AirportFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/** 茶颜机场商品映射文件导入
 * @author wangrong
 */
@RestController
@RequestMapping("/file/airport")
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
            return MallResponse.fail("上传文件为空");
        }
        String fileName = sourceFile.getOriginalFilename();
        log.info("[fileUpload] fileName{}, hqId:{}, branchId:{}", fileName, hqId, branchId);
        return airportFileService.parseFile(sourceFile, hqId, branchId);
    }

}
