package com.hchc.alarm.controller;

import com.hchc.alarm.dao.hchc.MaterialBarCodeDao;
import com.hchc.alarm.model.niceconsole.ModelBO;
import com.hchc.alarm.pack.MallResponse;
import com.hchc.alarm.pack.Output;
import com.hchc.alarm.service.BarCodeService;
import com.hchc.alarm.service.HqFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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
    @Autowired
    private MaterialBarCodeDao materialBarCodeDao;

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
    public Output parseBarCodeFile(MultipartFile file, int hqId) {
        log.info("[parseBarCodeFile] recv fileName:{}, hqId:{}", file.getOriginalFilename(), hqId);
        if (file.isEmpty()) {
            log.info("上传文件为空");
            return Output.fail("上传文件为空");
        }
        try {
            return Output.ok(barCodeService.parseFile(file, hqId));
        } catch (Exception e) {
            e.printStackTrace();
            log.info("[parseBarCodeFile] happen err:{}", e.getMessage());
            return Output.fail(e.getMessage());
        }
    }

    @GetMapping("delete")
    public Output deleteMoreRecord() {
        log.info("[deleteMoreRecord]");
        materialBarCodeDao.delete(32885);
        return Output.ok();
    }
}
