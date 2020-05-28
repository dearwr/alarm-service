package com.hchc.alarm.task;

import com.hchc.alarm.dao.hchc.BranchMallDao;
import com.hchc.alarm.model.MallBranch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import java.util.List;

/**
 * @author wangrong
 * @date 2020-05-27
 */
public class MallDataCheckTask {

    @Autowired
    private BranchMallDao branchMallDao;

    @Scheduled(cron = " 0 15 06 * * ? ")
    public void checkMallData() {
        List<MallBranch> immediateBranches = branchMallDao.queryBranchInfos("immediate");
    }

}
