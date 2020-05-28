package com.hchc.alarm.pack;

import lombok.Data;

/**
 * Created by wangrong 2019/12/11
 */
@Data
public class MallResponse {

    private int code;
    private String message;
    private Object data;

    public MallResponse(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static MallResponse ok() {
        return ok("success");
    }

    public static MallResponse ok(String msg) {
        return ok(msg, null);
    }

    public static MallResponse ok(Object data) {
        return ok("success", data);
    }

    public static MallResponse ok(String msg, Object data) {
        return new MallResponse(0, msg, data);
    }

    public static MallResponse fail() {
        return fail("fail");
    }

    public static MallResponse fail(String msg) {
        return fail(msg, null);
    }

    public static MallResponse fail(String msg, Object data) {
        return new MallResponse(999, msg, data);
    }

}
