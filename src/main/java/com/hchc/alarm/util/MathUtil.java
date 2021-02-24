package com.hchc.alarm.util;

import java.math.BigDecimal;

/**
 * @author wangrong
 * @date 2021-02-23
 */
public class MathUtil {

    public static double roundHalfUpToBigDouble(double val) {
        BigDecimal b = new BigDecimal(val);
        return b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
