package com.hchc.alarm.controller;

import com.hchc.alarm.dao.hchc.BranchKdsBaseDao;
import com.hchc.alarm.dao.hchc.KdsOperationLogDao;
import com.hchc.alarm.dao.hchc.KdsOrderDao;
import com.hchc.alarm.entity.BranchKds;
import com.hchc.alarm.pack.Output;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("kds")
@Slf4j
public class CheckKdsController {

    @Autowired
    private KdsOperationLogDao kdsOperationLogDao;
    @Autowired
    private BranchKdsBaseDao branchKdsBaseDao;
    @Autowired
    private KdsOrderDao kdsOrderDao;

    @GetMapping("/delete")
    public Output delete(@RequestParam(required = false, defaultValue = "0") int hqId,
                         @RequestParam(required = false, defaultValue = "0") int branchId,
                         @RequestParam(required = false) String uuid) {
        log.info("[delete] recv param hqId:{}, branchId:{}, uuid:{}", hqId, branchId, uuid);
        if (hqId <= 0 && branchId <= 0 && StringUtils.isEmpty(uuid)) {
            return Output.fail("param is empty");
        }
        return Output.ok(branchKdsBaseDao.delete(hqId, branchId, uuid));
    }

    @GetMapping("/add")
    public Output add(int branchId, int hqId) {
        log.info("[add] recv param branchId:{}, hqId:{}", branchId, hqId);
        BranchKds kdsDO = new BranchKds();
        kdsDO.setHqId(hqId);
        kdsDO.setBranchId(branchId);
        String uuid = UUID.randomUUID().toString();
        boolean uuidExit = branchKdsBaseDao.queryUUidExit(uuid);
        while (uuidExit) {
            uuid = UUID.randomUUID().toString();
            uuidExit = branchKdsBaseDao.queryUUidExit(uuid);
        }
        kdsDO.setUuid(uuid);
        return Output.ok(branchKdsBaseDao.saveOne(kdsDO));
    }

//    @GetMapping("/checkForUpdate")
//    public Output checkForUpdate() {
//        List<Integer> branchIdList = kdsOperationLogDao.queryAllBranchIdList();
//        List<Integer> noLogList = new ArrayList<>();
//        List<Integer> exitList = new ArrayList<>();
//        List<Integer> addList = new ArrayList<>();
//        BranchKdsDO kdsDO;
//        Date start = DatetimeUtil.addDay(new Date(), -1);
//        boolean uuidExit;
//        String uuid;
//        for (Integer branchId : branchIdList) {
//            kdsDO = kdsOperationLogDao.queryOneRecord(branchId, start);
//            if (kdsDO == null) {
//                noLogList.add(branchId);
//                continue;
//            }
//            if (branchKdsBaseDao.queryExist(branchId)) {
//                exitList.add(branchId);
//                continue;
//            }
//            uuid = UUID.randomUUID().toString();
//            uuidExit = branchKdsBaseDao.queryUUidExit(uuid);
//            while (uuidExit) {
//                uuid = UUID.randomUUID().toString();
//                uuidExit = branchKdsBaseDao.queryUUidExit(uuid);
//            }
//            kdsDO.setUuid(uuid);
//            if (branchKdsBaseDao.saveOne(kdsDO) == 1) {
//                addList.add(branchId);
//            }
//        }
//        Kds kds = new Kds();
//        kds.setNoLogList(noLogList);
//        kds.setExitList(exitList);
//        kds.setAddList(addList);
//        return Output.ok(kds);
//    }
//
//    @GetMapping("/checkForDelete")
//    public Output checkForDelete() {
//        List<Integer> branchIdList = branchKdsBaseDao.queryAllBranchIds();
//        List<Integer> noLogList = new ArrayList<>();
//        BranchKdsDO kdsDO;
//        Date start = DatetimeUtil.addDay(new Date(), -1);
//        for (Integer branchId : branchIdList) {
//            kdsDO = kdsOperationLogDao.queryOneRecord(branchId, start);
//            if (kdsDO == null) {
//                noLogList.add(branchId);
//                branchKdsBaseDao.delete(branchId, null);
//            }
//        }
//        return Output.ok(noLogList);
//    }

    @GetMapping("/order/complete")
    public Output orderComplete(int branchId, String startTime, String endTime) {
        log.info("[orderComplete] recv param branchId:{}, startTime:{}, endTime:{}", branchId, startTime, endTime);
        return Output.ok(kdsOrderDao.orderComplete(branchId, startTime, endTime));
    }

    @Getter
    @Setter
    private static class Kds {

        List<Integer> noLogList;
        List<Integer> exitList;
        List<Integer> addList;

    }

}
