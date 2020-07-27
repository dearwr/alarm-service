package com.hchc.alarm.dao.hchc;

import com.hchc.alarm.dao.HcHcBaseDao;
import org.springframework.stereotype.Repository;

@Repository
public class SqlDao extends HcHcBaseDao {

    public void execute(String sql) {
        hJdbcTemplate.execute(sql);
    }
}
