package com.hchc.alarm.controller;

import com.hchc.alarm.model.niceconsole.*;
import com.hchc.alarm.pack.MallResponse;
import com.hchc.alarm.pack.Output;
import com.hchc.alarm.service.BarCodeService;
import com.hchc.alarm.service.HqFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
    private HqFileService hqFileService;
    @Autowired
    private BarCodeService barCodeService;

    @PostMapping("/parseHqFile")
    public MallResponse parseHqFile(MultipartFile hqFile) throws IOException {
        log.info("[parseHqFile] recv fileName:{}", hqFile.getOriginalFilename());
        if (hqFile.isEmpty()) {
            log.info("上传文件为空");
            return MallResponse.ok(new ModelBO());
        }
        MallResponse response;
        try {
            response = hqFileService.parseFile(hqFile);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("[parseHqFile] happen err :{}", e.getMessage());
            response = MallResponse.ok(new ModelBO());
        }
        return response;
    }

    @PostMapping("/parseBarCodeFile")
    public Output parseBarCodeFile(MultipartFile file){
        log.info("[parseBarCodeFile] recv fileName:{}", file.getOriginalFilename());
        if (file.isEmpty()) {
            log.info("上传文件为空");
            return Output.fail("上传文件为空");
        }
        try {
            barCodeService.parseFile(file);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("[parseBarCodeFile] happen err:{}", e.getMessage());
            return Output.fail(e.getMessage());
        }
        return Output.ok();
    }
}
