package com.hchc.alarm;

import com.hchc.alarm.dao.rocket.KdsMessageBaseDao;
import com.hchc.alarm.util.DatetimeUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

@SpringBootTest
class AlarmApplicationTests {

	@Autowired
	private KdsMessageBaseDao kdsMessageDao;

	@Test
	void contextLoads() {
		Date start = DatetimeUtil.dayBegin(new Date());
		Date end = DatetimeUtil.dayEnd(start);
		String uuid = "76a95185-8646-3653-a648-4d671c59e3adKDS";
		List<String> orderList =  kdsMessageDao.queryAllPushed(3710, uuid, start, end);
		System.out.println("size:"+ orderList.size());
	}

}
