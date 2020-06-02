package com.hchc.alarm.task;

import com.hchc.alarm.dao.hchc.BranchMallDao;
import com.hchc.alarm.dao.hchc.MallRecordDao;
import com.hchc.alarm.model.BranchCheckBO;
import com.hchc.alarm.model.MallBranchBO;
import com.hchc.alarm.service.MallCheckService;
import com.hchc.alarm.util.DatetimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.hchc.alarm.constant.MallConstant.MARK_FULL_NAME_MAP;

/**
 * @author wangrong
 * @date 2020-05-27
 */
@Service
@Slf4j
public class MallDataCheckTask {

    @Autowired
    private BranchMallDao branchMallDao;
    @Autowired
    private MallRecordDao mallRecordDao;
    @Autowired
    private MallCheckService mallCheckService;

    @Scheduled(cron = " 0 05 05 * * ? ")
    public void checkMallData() {
        List<String> types = Arrays.asList("immediate", "daily2");
        List<MallBranchBO> immediateBranches = branchMallDao.queryBranchInfos(types);
        if (CollectionUtils.isEmpty(immediateBranches)) {
            log.info("[checkMallData] 未查询到商场");
            return;
        }
        Date endTime = DatetimeUtil.getDayStart(new Date());
        Date startTime = DatetimeUtil.addDay(endTime, -1);
        String startText = DatetimeUtil.format(startTime, "yyyyMMdd");
        String endText = startText;
        BranchCheckBO branchCheckBO;
        for (MallBranchBO branchBO : immediateBranches) {
            if (MARK_FULL_NAME_MAP.get(branchBO.getMark()) == null) {
                log.info("[checkMallData] not find mapping for mark:{}, branchId:{}", branchBO.getMark(), branchBO.getBranchId());
                continue;
            }
            branchCheckBO = new BranchCheckBO(branchBO);
            branchCheckBO.setStartTime(startTime);
            branchCheckBO.setEndTime(endTime);
            branchCheckBO.setStartText(startText);
            branchCheckBO.setEndText(endText);
            branchCheckBO.setMall(branchBO.getMark());
            log.info("[checkMallData] start check branchId:{}", branchBO.getBranchId());
            mallCheckService.saveFile(branchCheckBO, mallRecordDao.queryPushFailOrders(branchCheckBO));
            log.info("[checkMallData] end check branchId:{}", branchBO.getBranchId());
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}