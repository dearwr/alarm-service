package com.hchc.alarm.task;

import com.hchc.alarm.constant.RePushMallConstant;
import com.hchc.alarm.dao.hchc.MallRecordDao;
import com.hchc.alarm.model.PushMall;
import com.hchc.alarm.model.RePushMallBO;
import com.hchc.alarm.pack.Output;
import com.hchc.alarm.service.RemoteService;
import com.hchc.alarm.util.DatetimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author wangrong
 * @date 2020-06-22
 */
@Service
@Slf4j
public class RePushMallTask {

    @Autowired
    private RemoteService remoteService;
    @Autowired
    private MallRecordDao mallRecordDao;

    /**
     * 向mall旧服务器补传
     */
    @Scheduled(cron = " 0 45 3-5  * * ? ")
    public void oldServerPush() {
        Date start = DatetimeUtil.dayBegin(new Date());
        Date end = DatetimeUtil.dayEnd(new Date());
        String abbDate = DatetimeUtil.dayText(end);
        pushMallList(RePushMallConstant.rePushMalls, start, end, abbDate, RePushMallConstant.MARKUP_TEST_URL);
    }

    public void pushMallList(List<RePushMallBO> rePushMallBOList, Date start, Date end, String abbDate, String url) {
        for (RePushMallBO mall : rePushMallBOList) {
            List<String> orderNos = mallRecordDao.queryAllUnPushOrderNo(mall.getHqId(), mall.getBranchId(), start, end, abbDate, mall.getMallName());
            if (orderNos == null || orderNos.isEmpty()) {
                log.info("hqId={},branchId={},name={} 没有需要补传的订单", mall.getHqId(), mall.getBranchId(), mall.getMallName());
                continue;
            }
            log.info("hqId={},branchId={},name={} 开始补传订单，orderNos={}", mall.getHqId(), mall.getBranchId(), mall.getMallName(), orderNos);
            try {
                PushMall pushMall = new PushMall(mall.getHqId(), mall.getBranchId(), start, end, orderNos);
                Output output = remoteService.pushUnSuccessOrder(pushMall, url);
                if (output != null && "0".equals(output.getCode())) {
                    log.info("hqId={},branchId={},name={} 补传成功", mall.getHqId(), mall.getBranchId(), mall.getMallName());
                } else {
                    log.info("hqId={},branchId={},name={} 补传失败，fallOrderList={}", mall.getHqId(), mall.getBranchId(), mall.getMallName(), output.getData());
                }
            } catch (Exception e) {
                log.info("hqId={},branchId={},name={} 补传发生异常：{}", mall.getHqId(), mall.getBranchId(), mall.getMallName(), e.getMessage());
            }

            try {
                TimeUnit.MILLISECONDS.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
