package com.hchc.alarm.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by wangrong 2020/4/27
 */
@Getter
@Setter
public class BranchConfig {

    private long hqId;
    private long branchId;
    private String mallName;
    private boolean test;   // 是否测试（若是使用测试配置）
    private String type;    // 推送类型

    // http、webservice
    private String url;     // 正式地址
    private String testUrl; // 测试地址
    private String urlHost;
    private String testUrlHost;
    private String urlPort;
    private String testUrlPort;

    // ftp
    private String ftpHost;    // ftp.host
    private String testFtpHost;
    private int ftpPort;       // ftp.port
    private int testFtpPort;
    private boolean push;
    private String path;
    private int hour;
    private int minute;
    private int second;

    // 通用
    private String ip;
    private String secret;
    private String account;
    private String goodsId; // 商品id
    private String shopId;
    private String storeId;
    private String posId;
    private String goodsName;   // 商品名
    private String goodsCode;   // 商品编码
    private String username;    // 用户名
    private String password;    // 密码
    private String appKey;
    private String appSecret;
    private String cashier; // 收款员编号
    private String itemorgid;   // 货品所属机构的识别码
    private String supplierCode;    // 供应商码
    private String companyId;   // 公司标识
    private String companyName; // 公司名/商户名
    private String licensekey;  // 许可证书
    private String mallid;  // 商场编号，商场提供固定值

    // 华润
    private String apiId;   // API调用的API编码
    private String apiVersion;  // 调用的API版本号
    private String appPubId;    // 被调用API的应用编码
    private String appSubId;    // API调用方应用的编码
    private String appToken;    // API调用方授权令牌
    private String format;  // 响应格式,默认为json格式，可选值：xml或json
    private String partnerId;   // 合作伙伴身份标识
    private String signMethod;  // 生成服务请求签名字符串所使用的算法类型
    private String sysId;   // 合作伙伴系统编码
    private String signKey; // 签名

}
