package com.hchc.alarm.pack;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by wangrong 2020/5/12
 * @author wangrong
 */
@Setter
@Getter
public class KdsConsoleInfo {

    private String brandName;
    private String branchName;
    private String uuid;
    private String versionCode;
    private String heartTime = "";
    private int wxCount;
    private String openState;
    private boolean offLine;
}
