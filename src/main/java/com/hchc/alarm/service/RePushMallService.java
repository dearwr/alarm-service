package com.hchc.alarm.service;

import com.alibaba.fastjson.JSON;
import com.hchc.alarm.dao.hchc.BranchMallDao;
import com.hchc.alarm.dao.hchc.MallRecordDao;
import com.hchc.alarm.model.PushMall;
import com.hchc.alarm.model.RePushMallBO;
import com.hchc.alarm.pack.Output;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author wangrong
 * @date 2020-08-24
 */
@Service
@Slf4j
public class RePushMallService {

    @Autowired
    private BranchMallDao branchMallDao;
    @Autowired
    private MallRecordDao mallRecordDao;
    @Autowired
    private RemoteService remoteService;

    public List<RePushMallBO> queryValidMalls() {
        List<RePushMallBO> rePushMalls = branchMallDao.queryValidMalls();
        log.info("[queryValidMalls] malls => {}", JSON.toJSONString(rePushMalls));
        return rePushMalls.stream().filter(m -> {
            if ("peets".equals(m.getMallName()) || "seesaw".equals(m.getMallName()) || "marzano".equals(m.getMallName()) || "theplace".equals(m.getMallName()) || "raffles".equals(m.getMallName())) {  // 特殊商场
                return false;
            } else if (m.getBranchId() == 2177L || m.getBranchId() == 3441L) { // 内网、九龙仓
                return false;
            } else {
                return true;
            }
        }).collect(Collectors.toList());
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
                Output output = remoteService.pushOrders(pushMall, url);
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
