package com.hchc.alarm.util;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by jwing on 10/03/2017.
 */
public class StringUtil {
    public static String MACHINE_SEQ = "";
    private static AtomicInteger SOLO = new AtomicInteger();
    private static AtomicInteger orderCounter = new AtomicInteger();
    //线上订单
    public static final String AIR = "AIR";
    //外送订单
    public static final String DELIVERY = "DEL";
    //商城订单
    public static final String MALL = "MAL";

    public static String generateMessageId(String flag){
        int countVal = CounterUtil.getCounter(SOLO, 10000);
        return String.format("%s%s%s%04d", DatetimeUtil.now(), flag, MACHINE_SEQ, countVal);
    }

    public static String generateOrderFlow(long hqId, long branchId, String tag) {
        return String.format("%04d%03d%s%s%05d", hqId, branchId, DatetimeUtil.simpleNow(), tag, getCounter(orderCounter, 100000));
    }

    private static int getCounter(AtomicInteger c, int max) {
        if (c.get() < max) {
            int curr = c.incrementAndGet();
            if (curr < max) {
                return curr;
            }
            if (curr == max) {//curr == max
                c.set(1);
                return 1;
            }
        }
        if (c.get() == max) {
            throw new RuntimeException("unreach code on get counter algorithm");
        }
        while (c.get() > max) {
            continue;
        }
        return getCounter(c, max);
    }

    public static String randomCode(int count){
        String code = "";
        for (int i = 0; i < count; i++){
            int number = ThreadLocalRandom.current().nextInt(10);
            code += number;
        }
        return code;
    }

    public static boolean isAllNumber(String code){
        if (code.isEmpty()){
            return false;
        }
        for (int i = 0; i < code.length(); i++){
            char c = code.charAt(i);
            if (c < '0' || c > '9'){
                return false;
            }
        }
        return true;
    }

    public static String toCamelCase(String name){
        StringBuilder sb = new StringBuilder();
        String[] path = name.split("_");
        sb.append(path[0]);
        for (int i = 1; i <path.length ; i++) {
            sb.append(path[i].substring(0,1).toUpperCase());
            if(path[i].length()>1){
                sb.append(path[i].substring(1));
            }
        }
        return sb.toString();
    }

    public static boolean isDiningWXDeliveryNo(String no){
        return no.contains(DELIVERY);
    }
    public static boolean isDiningWXAirNo(String no){
        return no.contains(AIR);
    }
    public static boolean isMallNo(String no){
        return no.contains(MALL);
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
