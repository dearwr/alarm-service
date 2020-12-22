package com.hchc.alarm.controller;

import com.hchc.alarm.pack.Output;
import com.hchc.alarm.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author wangrong
 * @date 2020-06-17
 */
@CrossOrigin
@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/parseAirFile")
    public Output parseAirFile(MultipartFile file, int hqId) {
        log.info("[parseAirFile] recv fileName:{}, hqId:{}", file.getOriginalFilename(), hqId);
        if (file.isEmpty()) {
            log.info("上传文件为空");
            return Output.fail("上传文件为空");
        }
        try {
            return Output.ok(fileService.parse(file, hqId));
        } catch (Exception e) {
            e.printStackTrace();
            log.info("[parseAirFile] happen err:{}", e.getMessage());
            return Output.fail(e.getMessage());
        }
    }

    @PostMapping("/parseNewCard")
    public Output parseNewCard(MultipartFile file, int hqId) {
        log.info("[parseNewCard] recv fileName:{}, hqId:{}", file.getOriginalFilename(), hqId);
        if (file.isEmpty()) {
            log.info("上传文件为空");
            return Output.fail("上传文件为空");
        }
        try {
            return Output.ok(fileService.parseNewCard(file, hqId));
        } catch (Exception e) {
            e.printStackTrace();
            log.info("[parseNewCard] happen err:{}", e.getMessage());
            return Output.fail(e.getMessage());
        }
    }

    @PostMapping("/parseWaiMaiCode")
    public Output parseWaiMaiCode(MultipartFile file, int hqId) {
        log.info("[parseWaiMaiCode] recv fileName:{}, hqId:{}", file.getOriginalFilename(), hqId);
        if (file.isEmpty()) {
            log.info("上传文件为空");
            return Output.fail("上传文件为空");
        }
        try {
            return Output.ok(fileService.parseWaiMaiCode(file, hqId));
        } catch (Exception e) {
            e.printStackTrace();
            log.info("[parseWaiMaiCode] happen err:{}", e.getMessage());
            return Output.fail(e.getMessage());
        }
    }

    @GetMapping("/fillTogoSku")
    public Output fillTogoSku() {
        log.info("[fillTogoSku] recv");
        try {
            return Output.ok(fileService.fillTogoSku());
        } catch (Exception e) {
            e.printStackTrace();
            log.info("[fillTogoSku] happen err:{}", e.getMessage());
            return Output.fail(e.getMessage());
        }
    }

}
