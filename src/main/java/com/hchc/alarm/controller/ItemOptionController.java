package com.hchc.alarm.controller;

import com.hchc.alarm.dao.hchc.OrderItemOptionDao;
import com.hchc.alarm.pack.Output;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author wangrong
 * @date 2020-07-08
 */
@RestController
@RequestMapping("/item")
@Slf4j
public class ItemOptionController {

    @Autowired
    private OrderItemOptionDao orderItemOptionDao;

    @PostMapping("deleteOptions")
    public Output deleteOptionIds(@RequestBody Option option) {
        if (option == null || CollectionUtils.isEmpty(option.getIdList())) {
            return Output.fail("idList is empty");
        }
        orderItemOptionDao.delete(option.getIdList());
        return Output.ok();
    }

    private static class Option{
        private List<Long> idList;

        public List<Long> getIdList() {
            return idList;
        }

        public void setIdList(List<Long> idList) {
            this.idList = idList;
        }
    }
}
