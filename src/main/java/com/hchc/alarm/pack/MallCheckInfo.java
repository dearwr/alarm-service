package com.hchc.alarm.pack;

import lombok.Data;

import java.util.Map;

/**
 * @author wangrong
 * @date 2020-05-29
 */
@Data
public class MallCheckInfo {

    private String day;
    private boolean status;
    private Map<String, String> detail;

}
