package com.hchc.alarm.task;

import com.hchc.alarm.constant.MallConstant;
import com.hchc.alarm.model.RePushMallBO;
import com.hchc.alarm.service.RePushMallService;
import com.hchc.alarm.util.DatetimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
    @Scheduled(cron = " 0 25 1,2,3,4,5  * * ? ")
    public void rePushOnNight() {
        log.info("[rePushOnNight] start");
        List<RePushMallBO> rePushMalls = rePushMallService.queryValidMalls();
        Date end = DatetimeUtil.getDayStart(new Date());
        Date start = DatetimeUtil.addDay(end, -1);
        String abbDate = DatetimeUtil.dayText(start);
        rePushMallService.pushMallList(rePushMalls, start, end, abbDate, MallConstant.MALL_ORDER_URL);
    }

    /**
     * 白天补传
     */
    @Scheduled(cron = "0 */10 * * * ? ")
    public void rePushOnDay() {

        List<RePushMallBO> rePushMalls = rePushMallService.queryValidMalls();
        Date start = DatetimeUtil.dayBegin(new Date());
        String abbDate = DatetimeUtil.dayText(start);
        rePushMallService.pushMallList(rePushMalls, start, DatetimeUtil.addMinute(new Date(), -1), abbDate, MallConstant.MALL_ORDER_URL);
    }

    /**
     * Peets需要实时推的商场
     */
    @Scheduled(cron = "*/30 * * * * ? ")
    public void pushImmediateMalls() {
        List<RePushMallBO> rePushMalls = rePushMallService.queryValidMalls();
        rePushMalls = rePushMalls.stream()
                .filter(m -> m.getBranchId() == 6686)
                .collect(Collectors.toList());
        Date start = DatetimeUtil.dayBegin(new Date());
        String abbDate = DatetimeUtil.dayText(start);
        rePushMallService.pushMallList(rePushMalls, start, new Date(), abbDate, MallConstant.MALL_ORDER_URL);
    }

}
