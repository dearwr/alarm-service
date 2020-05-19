package com.hchc.alarm.model;

import java.util.Objects;

/**
 * Created by wangrong 2020/4/27
 */
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BranchInfo info = (BranchInfo) o;
        return branchId == info.branchId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(branchId, brandName, branchName);
    }

    public long getHqId() {
        return hqId;
    }

    public void setHqId(long hqId) {
        this.hqId = hqId;
    }

    public long getBranchId() {
        return branchId;
    }

    public void setBranchId(long branchId) {
        this.branchId = branchId;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlHost() {
        return urlHost;
    }

    public void setUrlHost(String urlHost) {
        this.urlHost = urlHost;
    }

    public String getUrlPort() {
        return urlPort;
    }

    public void setUrlPort(String urlPort) {
        this.urlPort = urlPort;
    }

    public String getFtpHost() {
        return ftpHost;
    }

    public void setFtpHost(String ftpHost) {
        this.ftpHost = ftpHost;
    }

    public int getFtpPort() {
        return ftpPort;
    }

    public void setFtpPort(int ftpPort) {
        this.ftpPort = ftpPort;
    }

    public String getPushMethod() {
        return pushMethod;
    }

    public void setPushMethod(String pushMethod) {
        this.pushMethod = pushMethod;
    }
}
