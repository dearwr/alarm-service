package com.hchc.alarm.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by wangrong 2020/5/12
 * @author wangrong
 */
public class RocketBaseDao {

    @Autowired
    protected JdbcTemplate rJdbcTemplate;
}
