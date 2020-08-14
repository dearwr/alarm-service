package com.hchc.alarm.service;

import com.alibaba.fastjson.JSON;
import com.hchc.alarm.model.PushMall;
import com.hchc.alarm.pack.Output;
import com.hchc.alarm.pack.QueueInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Created by wangrong 2020/5/13
 * @author wangrong
 */
@Service
@Slf4j
public class RemoteService {

    public static final String VIP_URL = "https://vip.51hchc.com";

    @Autowired
    private RestTemplate restTemplate;

    public int getWxQueueCount(int hqId, int branchId) {
        String url = VIP_URL + "/order/queueSummaryinfo?hqId=" + hqId + "&branchId=" + branchId;
        Output response = restTemplate.getForEntity(url, Output.class).getBody();
        log.info("[getWxQueueCount] response :{}", JSON.toJSONString(response));
        QueueInfo queueInfo = JSON.parseObject(JSON.toJSONString(response.getData()), QueueInfo.class);
        return queueInfo.getQueueCount();
    }

    public Output pushOrders(PushMall pushMall, String url) {
        Output output = restTemplate.postForEntity(url, pushMall, Output.class).getBody();
        log.info("[pushUnSuccessOrder] res :{}", JSON.toJSONString(output));
        return output;
    }
}
