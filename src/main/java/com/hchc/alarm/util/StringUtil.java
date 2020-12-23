package com.hchc.alarm.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by jwing on 10/03/2017.
 */
public class StringUtil {

    public static String MACHINE_SEQ = "";

    private static AtomicInteger SOLO = new AtomicInteger();

    public static String generateMessageId(String flag) {
        int countVal = getCounter(SOLO, 10000);
        return String.format("%s%s%s%04d", DatetimeUtil.now(), flag, MACHINE_SEQ, countVal);
    }

    public static int getCounter(AtomicInteger c, int max){
        if (c.get() < max){
            int curr = c.incrementAndGet();
            if (curr < max){
                return curr;
            }
            if (curr == max){//curr == max
                c.set(1);
                return 1;
            }
        }
        if (c.get() == max){
            throw new RuntimeException("unreach code on get counter algorithm");
        }
        while (c.get() > max){
            continue;
        }
        return getCounter(c, max);
    }

    public static boolean isBlank(String cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(cs.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotBlank(String cs) {
        return !isBlank(cs);
    }
}
