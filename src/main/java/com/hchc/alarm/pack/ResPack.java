package com.hchc.alarm.pack;

import com.hchc.alarm.util.StringUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by jwing on 10/03/2017.
 */
@Getter
@Setter
public class ResPack {

    private String messageId;
    private long timestamp = System.currentTimeMillis();
    private String code;
    private String message;
    private Object data;

    public ResPack() {

    }

    public ResPack(String code, String message, Object data) {
        this.messageId = StringUtil.generateMessageId("vip");
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static ResPack ok() {
        return ok(null);
    }

    public static ResPack ok(Object data) {
        return new ResPack("0", "ok", data);
    }

    public static ResPack fail(String message) {
        return new ResPack("999", message, null);
    }

}
