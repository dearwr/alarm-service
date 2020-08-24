package com.hchc.alarm.task;

import com.hchc.alarm.constant.RePushMallConstant;
import com.hchc.alarm.model.RePushMallBO;
import com.hchc.alarm.service.RePushMallService;
import com.hchc.alarm.util.DatetimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author wangrong
 * @date 2020-06-22
 */
@Service
@Slf4j
public class RePushMallTask {

    @Autowired
    private RePushMallService rePushMallService;

    /**
     * 晚上定时补传
     */
    @Scheduled(cron = " 0 25 5  * * ? ")
    public void rePushOnNight() {
        log.info("[rePushOnNight] start");
        Date end = DatetimeUtil.getDayStart(new Date());
        Date start = DatetimeUtil.addDay(end, -1);
        String abbDate = DatetimeUtil.dayText(start);
        rePushMallService.pushMallList(RePushMallConstant.rePushMalls, start, end, abbDate, RePushMallConstant.MALL_ORDER_URL);
    }

    /**
     * 白天补传
     */
    @Scheduled(cron = "0 0 10,15,21,23 * * ? ")
    public void rePushOnDay() {
        List<RePushMallBO> rePushMalls = rePushMallService.queryValidMalls();
        Date start = DatetimeUtil.dayBegin(new Date());
        String abbDate = DatetimeUtil.dayText(start);
        rePushMallService.pushMallList(rePushMalls, start, new Date(), abbDate, RePushMallConstant.MALL_ORDER_URL);
    }

}
