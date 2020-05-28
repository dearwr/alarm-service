package com.hchc.alarm.util;

import com.hchc.alarm.model.CheckOrder;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * @author wangrong
 */
@Slf4j
public class ObjectUtils {

    private static final String TAB_STR = "\t";
    private static final String NEXT_LINE = "\n";

    /**
     * 通过类中属性名的键值对map返回一个该类的实例化对象（属性类型只支持string、double）
     * @param paramMap
     * @param clz
     * @param <T>
     * @return
     */
    public static <T> T getInstanceFromMap(Map<String, String> paramMap, Class<T> clz) {
        if (paramMap == null) {
            log.info("传入的paramMap为空");
            return null;
        }
        if (clz == null) {
            log.info("传入的对象为空");
            return null;
        }
        T instance;
        try {
            instance = clz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            log.info("反射创建对象失败：" + e.getMessage());
            return null;
        }
        for (String fieldName : paramMap.keySet()) {
            try {
                Field field = clz.getDeclaredField(fieldName);
                if (field != null) {
                    field.setAccessible(true);
                    if ("double".equals(field.getType().getName())) {
                        field.set(instance, Double.valueOf(paramMap.get(fieldName)));
                    }else {
                        field.set(instance, paramMap.get(fieldName));
                    }
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                log.info("未找到属性：{}", fieldName);
            }
        }
        return instance;
    }

    /**
     * 将对象属性值转化为字符串
     * @param errorOrders
     * @param mallName
     * @return
     */
    public static String convertToString(List<CheckOrder> errorOrders, String mallName) {
        StringBuilder sb = new StringBuilder();
        sb.append("#").append(errorOrders.get(0).getBranchId()).append("-").append(mallName).append(TAB_STR).append(errorOrders.size())
                .append(NEXT_LINE)
                .append(NEXT_LINE);
        if (errorOrders.size() > 30) {
            sb.append("unPush order over 30, reason: ").append(errorOrders.get(0).getRemark()).append(NEXT_LINE);
        }else {
            errorOrders.forEach(order->{
                sb.append(order.getOrderNo())
                        .append(TAB_STR)
                        .append(order.getCreateTime())
                        .append(TAB_STR)
                        .append(order.getStatus())
                        .append(TAB_STR)
                        .append(order.getPlatform())
                        .append(TAB_STR)
                        .append(order.getCreateTime())
                        .append(TAB_STR)
                        .append(order.getRemark())
                        .append(NEXT_LINE);
            });
        }
        return sb.toString();
    }
}
