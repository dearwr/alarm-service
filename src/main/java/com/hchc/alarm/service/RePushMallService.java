package com.hchc.alarm.service;

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
        return rePushMalls.stream().filter(m -> {
            if ("peets".equals(m.getMallName()) || "seesaw".equals(m.getMallName()) || "marzano".equals(m.getMallName())
                    || "theplace".equals(m.getMallName()) || "raffles".equals(m.getMallName()) || "airport".equals(m.getMallName()) || "spk".equals(m.getMallName())) {  // 特殊商场
                return false;
            } else if (m.getBranchId() == 2177L || m.getBranchId() == 3441L) { // 内网、九龙仓
                return false;
            } else {
                return true;
            }
        }).collect(Collectors.toList());
    }

    public void pushMallList(List<RePushMallBO> rePushMallBOList, Date start, Date end, String abbDate, String url) {
        String mallName;
        for (RePushMallBO mall : rePushMallBOList) {
            long hqId = mall.getHqId();
            long branchId = mall.getBranchId();
            mallName = mall.getMallName();
            List<String> orderNos = mallRecordDao.queryAllUnPushOrderNo(hqId, branchId, start, end, abbDate, mallName);
            if (orderNos == null || orderNos.isEmpty()) {
                log.info("{} {} {} 没有需要补传的订单", hqId, branchId, mallName);
                continue;
            }
            log.info("{} {} {} 开始补传订单，orderNos={}", hqId, branchId, mallName, orderNos);
            try {
                PushMall pushMall = new PushMall(hqId, branchId, start, end, orderNos);
                Output output = remoteService.pushOrders(pushMall, url);
                if (output != null && "0".equals(output.getCode())) {
                    log.info("{} {} {} 补传成功", hqId, branchId, mallName);
                } else {
                    log.info("{} {} {} 补传失败，fallOrderList={}", hqId, branchId, mallName, output.getData());
                }
            } catch (Exception e) {
                log.info("{} {} {} 补传发生异常：{}", hqId, branchId, mallName, e.getMessage());
            }
            try {
                TimeUnit.MILLISECONDS.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void pushMallTransList(List<RePushMallBO> rePushMallBOList, Date start, Date end, String abbDate, String url) {
        String mallName;
        for (RePushMallBO mall : rePushMallBOList) {
            long hqId = mall.getHqId();
            long branchId = mall.getBranchId();
            mallName = mall.getMallName();
            List<String> orderNos = mallRecordDao.queryAllUnPushTransOrders(branchId, abbDate);
            if (orderNos == null || orderNos.isEmpty()) {
                log.info("{} {} {} 没有需要补传的订单", hqId, branchId, mallName);
                continue;
            }
            log.info("{} {} {} 开始补传订单，orderNos={}", hqId, branchId, mallName, orderNos);
            try {
                PushMall pushMall = new PushMall(hqId, branchId, start, end, orderNos);
                Output output = remoteService.pushOrders(pushMall, url);
                if (output != null && "0".equals(output.getCode())) {
                    log.info("{} {} {} 补传成功", hqId, branchId, mallName);
                } else {
                    log.info("{} {} {} 补传失败，fallOrderList={}", hqId, branchId, mallName, output.getData());
                }
            } catch (Exception e) {
                log.info("{} {} {} 补传发生异常：{}", hqId, branchId, mallName, e.getMessage());
            }
            try {
                TimeUnit.MILLISECONDS.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
