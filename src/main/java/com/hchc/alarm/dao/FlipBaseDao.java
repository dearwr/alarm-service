package com.hchc.alarm.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by wangrong 2020/5/19
 */
public class FlipBaseDao {

    @Autowired
    protected JdbcTemplate fJdbcTemplate;
}
