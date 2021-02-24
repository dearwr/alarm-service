package com.hchc.alarm.controller;

import com.hchc.alarm.service.FileService;
import com.hchc.alarm.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

/**
 * @author wangrong
 * @date 2021-02-24
 */
@CrossOrigin
@RestController
@RequestMapping("/delivery")
@Slf4j
public class DeliveryController {

    @Autowired
    private FileService fileService;

    @GetMapping("/pickTime/file/download")
    public String fileDownload(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        String hqIdStr = request.getParameter("hqId");
        String startStr = request.getParameter("start");
        String endStr = request.getParameter("end");
        String companiesStr = request.getParameter("companies");
        log.info("[fileDownload] recv hqId:{}, startStr:{}, end:{}, companiesStr:{}", hqIdStr, startStr, endStr, companiesStr);

        if (StringUtil.isBlank(hqIdStr) || StringUtil.isBlank(startStr) ||
                StringUtil.isBlank(endStr) || StringUtil.isBlank(companiesStr)) {
            return null;
        }

        long hqId = Long.parseLong(hqIdStr);
        String[] companies = companiesStr.split(",");
        File file = fileService.createPickTimeFile(hqId, startStr, endStr, companies);
        log.info("fileDownload file name:{}", file.getName());
        if (file.exists()) {
            response.setContentType("application/octet-stream");
            response.setHeader("content-type", "application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(file.getName(), "UTF-8"));
            byte[] buffer = new byte[1024];
            try (FileInputStream fis = new FileInputStream(file);
                 BufferedInputStream bis = new BufferedInputStream(fis)) {
                OutputStream os = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
                System.out.println("success");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
