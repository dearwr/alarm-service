package com.hchc.alarm.pack.output;

import lombok.Data;

/**
 * Created by wangrong 2020/5/12
 */
@Data
public class KdsConsoleInfo {

    private String brandName;
    private String branchName;
    private String uuid;
    private int wxCount;
    private int kdsCount;
}
