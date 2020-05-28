package com.hchc.alarm.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by wangrong 2020/4/27
 * @author wangrong
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MallBranchBO extends BranchBO {

    private long hqId;
    private long branchId;
    private String mark;

    // http、webservice
    /**
     * 正式地址
     */
    private String url;
    private String urlHost;
    private String urlPort;

    // ftp
    /**
     * host
     */
    private String ftpHost;
    /**
     * port
     */
    private int ftpPort;

    private String pushMethod;

}
