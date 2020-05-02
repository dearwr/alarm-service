package com.hchc.alarm.model;

import lombok.Data;

/**
 * Created by wangrong 2020/4/27
 */
@Data
public class BranchInfo extends Branch{

    private long hqId;
    private long branchId;
    private String mark;

    // http、webservice
    private String url;     // 正式地址
    private String urlHost;
    private String urlPort;

    // ftp
    private String ftpHost;    // ftp.host
    private int ftpPort;       // ftp.port

    private String pushMethod;

}
