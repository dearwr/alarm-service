package com.hchc.alarm.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by wangrong 2020/4/27
 * @author wangrong
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class MallBranchBO extends BranchBO {

    private String mallName;
    private String displayName;

    private String url;     // 正式地址
    private String testUrl; // 测试地址
    private String urlHost;
    private String testUrlHost;
    private String ftpHost;    // ftp.host
    private String testFtpHost;

    private String type;

}
