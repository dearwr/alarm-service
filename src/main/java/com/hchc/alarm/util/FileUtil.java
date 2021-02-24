package com.hchc.alarm.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * @author wangrong
 * @date 2021-02-23
 */
@Slf4j
public class FileUtil {

    public static void deleteFile(File file) {
        try {
            FileUtils.forceDelete(file);
            log.info("[deleteFile] 删除文件成功 filePath:{}", file.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
