package com.hchc.alarm.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @author wangrong
 * @date 2020-08-15
 */
@Setter
@Getter
public class ShangWeiMch {

    /**
     * 品牌id
     */
    private long hqId;

    /**
     * 联网发行唯一标识
     */
    private String uniqueNo;

    /**
     * 连接csb发布服务的ak
     */
    private String ak;

    /**
     * 连接csb发布服务的sk
     */
    private String sk;

    /**
     * 私钥
     */
    private String privateKey;

    /**
     * CSB url
     */
    private String url;

}
