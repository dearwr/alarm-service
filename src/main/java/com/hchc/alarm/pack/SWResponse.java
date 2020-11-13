package com.hchc.alarm.pack;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author wangrong
 * @date 2020-11-10
 */
@Getter
@Setter
public class SWResponse {

    private Object header;
    private Body body;


    @Getter
    @Setter
    public static class Body {
        private String errorCode;
        private String cardNo;
        private List<CardList> orCardList;
    }

    @Getter
    @Setter
    public static class CardList {
        private String cardMon;
        private String cardNo;
        private String isregister;
    }
}
