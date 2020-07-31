package com.hchc.alarm.pack;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author wangrong
 * @date 2020-05-29
 */
@Setter
@Getter
public class MallCheckInfo {

    private String day;
    private boolean status;
    private Map<String, String> detail;

}
