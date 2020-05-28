package com.hchc.alarm.enums;

/**
 * @author wangrong
 * @date 2020-05-28
 */
public enum  PushMethodEm {

    //实时
    immediate("immediate", "实时"),
    //定时
    daily("daily", "定时"),
    //定时
    daily2("daily2", "定时");

    private String pCode;
    private String pName;

    PushMethodEm(String pCode, String pName) {
        this.pCode = pCode;
        this.pName = pName;
    }

    public static String getNameByCode(String pCode) {
        for (PushMethodEm pushMethod : PushMethodEm.values()) {
            if (pushMethod.pCode.equals(pCode)) {
                return pushMethod.pName;
            }
        }
        return null;
    }
}
