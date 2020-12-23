package com.hchc.alarm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author wangrong
 * @date 2020-08-07
 */
public class Test {

    public static void main(String[] args) {
        writeData();
    }

    private static void writeData() {
        File file = new File("E:/XSJL.DB");
        byte[] buffer = new byte[1024];
        StringBuilder sb = new StringBuilder();
        try (FileInputStream ins = new FileInputStream(file)) {
            while (ins.read(buffer) != -1) {
                sb.append(new String(buffer, StandardCharsets.UTF_8));
            }
            System.out.println(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
