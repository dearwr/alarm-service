package com.hchc.alarm.constant;

import java.text.Collator;
import java.util.*;

public class MallConstant {

    // 中文比较器
    public static final Collator CHINESE_COMPARATOR = Collator.getInstance(java.util.Locale.CHINA);

    public enum PushMethod {
        immediate("immediate", "实时"),
        daily("daily", "定时"),
        daily2("daily2", "定时");

        private String pMethod;
        private String pName;

        PushMethod(String pMethod, String pName) {
            this.pMethod = pMethod;
            this.pName = pName;
        }

        public static String getNameByMethod(String pMethod) {
            for (PushMethod pushMethod : PushMethod.values()) {
                if (pushMethod.pMethod.equals(pMethod)) {
                    return pushMethod.pName;
                }
            }
            return null;
        }
    }

    public enum PushType {
        ftp, http, webservice;
    }

    public static final Map<String, String> MARK_NAME_MAP = new HashMap<>();

    static {
        MARK_NAME_MAP.put("lavenue", "上海尚嘉中心");
        MARK_NAME_MAP.put("superbrand", "上海陆家嘴中心");
        MARK_NAME_MAP.put("shanghaitower", "上海中心");
        MARK_NAME_MAP.put("daning", "上海大宁国际");
        MARK_NAME_MAP.put("hfmixc", "合肥万象城");
        MARK_NAME_MAP.put("metrocity", "上海美罗城");
        MARK_NAME_MAP.put("thehzmixc", "杭州万象城");
        MARK_NAME_MAP.put("globalharbor", "上海环球港");
        MARK_NAME_MAP.put("therockbund", "上海外滩");
        MARK_NAME_MAP.put("jiuguang", "上海久光百货");
        MARK_NAME_MAP.put("kerry", "上海嘉里中心");
        MARK_NAME_MAP.put("kerryliberte", "上海嘉亭荟");
        MARK_NAME_MAP.put("fenglan", "北京枫蓝国际中心");
        MARK_NAME_MAP.put("fenglandaily", "北京枫蓝国际中心");
        MARK_NAME_MAP.put("reelhttp", "上海芮欧百货");
        MARK_NAME_MAP.put("theplace", "上海南丰城");
        MARK_NAME_MAP.put("themixc","太原万象城" );
        MARK_NAME_MAP.put("thejnmixc","济南万象城" );
        MARK_NAME_MAP.put("themixcshijiazhuang","石家庄万象城" );
        MARK_NAME_MAP.put("crystalcenter","上海晶品购物中心" );
        MARK_NAME_MAP.put("luone","上海凯德晶萃广场" );
        MARK_NAME_MAP.put("raffles", "上海长宁来福士");
        MARK_NAME_MAP.put("kingdee", "上海白玉兰广场");
        MARK_NAME_MAP.put("theszmixc", "深圳万象天地");
        MARK_NAME_MAP.put("theshmixc","上海万象城");
        MARK_NAME_MAP.put("thejsmixc", "无锡万象城");
        MARK_NAME_MAP.put("shringtrade", "上海环贸");
        MARK_NAME_MAP.put("szonesquarecity", "深圳壹方城");
        MARK_NAME_MAP.put("szraffles", "深圳湾万象城");
        MARK_NAME_MAP.put("wfcentral", "北京王府中环");
        MARK_NAME_MAP.put("shanghaisyw", "上海尚悦湾");
        MARK_NAME_MAP.put("bfc", "上海BFC外滩金融中心");
        MARK_NAME_MAP.put("suzhoucenter", "苏州中心");
        MARK_NAME_MAP.put("starcocopark", "深圳COCOPARK购物中心");
        MARK_NAME_MAP.put("jindi", "上海金地广场");
        MARK_NAME_MAP.put("bubugao", "长沙步步高梅溪新天地");
        MARK_NAME_MAP.put("ninedragon", "长沙九龙仓国金中心");
        MARK_NAME_MAP.put("wincor", "长沙坡子街悦方");
        MARK_NAME_MAP.put("airport", "长沙市黄花机场");
        MARK_NAME_MAP.put("rglaifushi", "上海来福士");
        MARK_NAME_MAP.put("why", "长沙文和友海信广场");
        MARK_NAME_MAP.put("newxzg","北京新中关");
        MARK_NAME_MAP.put("shgrandgetaway","上海港汇恒隆广场");
        MARK_NAME_MAP.put("coolcat","上海外滩金融服务中心");
        MARK_NAME_MAP.put("huanqiu", "上海环球金融中心");
        MARK_NAME_MAP.put("jinhongqiao", "上海金虹桥商场");
        MARK_NAME_MAP.put("dingxiang", "上海丁香国际商业中心");
        MARK_NAME_MAP.put("spk", "北京SKP");
        MARK_NAME_MAP.put("bofu", "长沙泊富icity美好生活中心");
        MARK_NAME_MAP.put("bohuasquare", "上海博华广场");
        MARK_NAME_MAP.put("wanxiangcheng","厦门万象城");
        MARK_NAME_MAP.put("jinyaoqiantan", "上海晶耀前滩");
        MARK_NAME_MAP.put("guangzhoutianhuan", "广州天环广场");
        MARK_NAME_MAP.put("huarun", "上海华润时代广场");
        MARK_NAME_MAP.put("beijinguomao", "北京国贸");
        MARK_NAME_MAP.put("wujiang", "苏州吴江万象汇");
    }

}
