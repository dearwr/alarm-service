package com.hchc.alarm.constant;

import com.hchc.alarm.model.RePushMallBO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wangrong
 * @date 2020-06-22
 */
public class RePushMallConstant {

    public static final String MARKUP_TEST_URL = "http://120.78.232.8:9501/pushOrderToMall/pushToTestMall";

    // 补推商场
    public static List<RePushMallBO> rePushMalls = new ArrayList<>();

    static {
        rePushMalls.add(new RePushMallBO(2581, 3759, "shgrandgetaway"));
        rePushMalls.add(new RePushMallBO(2498, 3745, "huarun"));
        rePushMalls.add(new RePushMallBO(2498, 3746, "huarun"));
        rePushMalls.add(new RePushMallBO(633, 2165, "huarun"));
        rePushMalls.add(new RePushMallBO(1516, 4933, "huarun"));
        rePushMalls.add(new RePushMallBO(1721, 3958, "bfc"));
        rePushMalls.add(new RePushMallBO(2439, 3417, "bofu"));
        rePushMalls.add(new RePushMallBO(1516, 4070, "guangzhoutianhuan"));
        rePushMalls.add(new RePushMallBO(1721, 4152, "jinyaoqiantan"));
        rePushMalls.add(new RePushMallBO(633, 3544, "jinyaoqiantan"));
        rePushMalls.add(new RePushMallBO(2439, 3447, "airport"));
        rePushMalls.add(new RePushMallBO(2248, 4059, "spk"));
        rePushMalls.add(new RePushMallBO(338, 577, "kerry"));
        rePushMalls.add(new RePushMallBO(237, 2081, "bfc"));
        rePushMalls.add(new RePushMallBO(257, 554, "kerry"));
        rePushMalls.add(new RePushMallBO(2581, 3759, "shgrandgetaway"));
        rePushMalls.add(new RePushMallBO(172, 4811, "dingxiang"));
        rePushMalls.add(new RePushMallBO(2765, 4045, "bubugao"));
        rePushMalls.add(new RePushMallBO(1516, 3125, "starcocopark"));
    }

    // 检查数据商场
    public static List<RePushMallBO> checkMalls;

    static {
        checkMalls = rePushMalls;
        checkMalls.add(new RePushMallBO(2439, 3449, "bubugao"));
        checkMalls.add(new RePushMallBO(2498, 3742, "wujiang"));
        checkMalls.add(new RePushMallBO(1290, 3644, "huarun"));
        checkMalls.add(new RePushMallBO(2439, 3441, "ninedragon"));
    }
}
