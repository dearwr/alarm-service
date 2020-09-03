package com.hchc.alarm.controller;

import com.hchc.alarm.constant.RePushMallConstant;
import com.hchc.alarm.dao.hchc.MallRecordDao;
import com.hchc.alarm.model.PushMall;
import com.hchc.alarm.model.RePushMallBO;
import com.hchc.alarm.pack.Output;
import com.hchc.alarm.service.RePushMallService;
import com.hchc.alarm.service.RemoteService;
import com.hchc.alarm.util.DatetimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * @author wangrong
 * @date 2020-06-22
 */
@RestController
@RequestMapping("/MallRecord")
@Slf4j
public class MallRecordController {

    @Autowired
    private MallRecordDao mallRecordDao;
    @Autowired
    private RemoteService remoteService;
    @Autowired
    private RePushMallService rePushMallService;

    @GetMapping("/pushUnSuccess")
    public Output pushUnSuccess(long hqId, long branchId, String mallName, String abbDate) throws ParseException {
        log.info("[pushUnSuccess] recv hqId:{}, branchId:{}, mallName:{}, abbDate:{}", hqId, branchId, mallName, abbDate);
        if (StringUtils.isEmpty(mallName) || StringUtils.isEmpty(abbDate)) {
            return Output.fail("param exit empty");
        }
        Date date = DatetimeUtil.parse(abbDate, "yyyyMMdd");
        Date start = DatetimeUtil.dayBegin(date);
        Date end = DatetimeUtil.dayEnd(date);
        if (hqId == 0 || branchId == 0) {
            List<RePushMallBO> rePushMalls = rePushMallService.queryValidMalls();
            rePushMallService.pushMallList(rePushMalls, start, end, abbDate, RePushMallConstant.MALL_ORDER_URL);
            return Output.ok();
        } else {
            List<String> orderList = mallRecordDao.queryAllUnPushOrderNo(hqId, branchId, start, end, abbDate, mallName);
            if (CollectionUtils.isEmpty(orderList)) {
                return Output.ok("no UnPush orderNos");
            }
            PushMall pushMall = new PushMall(hqId, branchId, start, end, orderList);
            return remoteService.pushOrders(pushMall, RePushMallConstant.MALL_ORDER_URL);
        }
    }

}
