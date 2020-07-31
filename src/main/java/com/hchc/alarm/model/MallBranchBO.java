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

    private String mark;

    private String url;
    private String urlHost;
    private String ftpHost;

    private String pushMethod;

}
