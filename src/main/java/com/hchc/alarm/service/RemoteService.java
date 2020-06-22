package com.hchc.alarm.service;

import com.alibaba.fastjson.JSON;
import com.hchc.alarm.model.MallBranchBO;
import com.hchc.alarm.model.PushOrder;
import com.hchc.alarm.pack.Output;
import com.hchc.alarm.pack.QueueInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Created by wangrong 2020/5/13
 * @author wangrong
 */
@Service
@Slf4j
public class RemoteService {

    public static final String VIP_URL = "https://vip.51hchc.com";
    public static final String FLIP_TEST_SERVER_URL = "http://47.103.123.87:7676";
    public static final String MARKUP_TEST_URL = "http://120.78.232.8:9501/pushOrderToMall/pushToTestMall";

    @Autowired
    private RestTemplate restTemplate;

    public int getWxQueueCount(int hqId, int branchId) {
        String url = VIP_URL + "/order/queueSummaryinfo?hqId=" + hqId + "&branchId=" + branchId;
        Output response = restTemplate.getForEntity(url, Output.class).getBody();
        log.info("[getWxQueueCount] response :{}", JSON.toJSONString(response));
        QueueInfo queueInfo = JSON.parseObject(JSON.toJSONString(response.getData()), QueueInfo.class);
        return queueInfo.getQueueCount();
    }

    public List<MallBranchBO> queryBranchInfos() {
        String url = FLIP_TEST_SERVER_URL + "/mallConsole/branchInfos";
        Output response = restTemplate.getForEntity(url, Output.class).getBody();
        log.info("[queryBranchInfos] response :{}", JSON.toJSONString(response));
        return JSON.parseArray(JSON.toJSONString(response.getData()), MallBranchBO.class);
    }

    public Output pushUnSuccessOrder(PushOrder pushOrder) {
        log.info("[pushUnSuccessOrder] req:{}", pushOrder);
        Output output = restTemplate.postForEntity(MARKUP_TEST_URL, pushOrder, Output.class).getBody();
        log.info("[pushUnSuccessOrder] res :{}", JSON.toJSONString(output));
        return output;
    }
}
