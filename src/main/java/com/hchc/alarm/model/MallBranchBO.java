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

    private String mark;

    private String url;
    private String urlHost;
    private String ftpHost;

    private String pushMethod;

}
