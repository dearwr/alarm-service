package com.hchc.alarm.service;

import com.hchc.alarm.constant.MallConstant;
import com.hchc.alarm.dao.hchc.HBranchMallDao;
import com.hchc.alarm.model.BranchInfo;
import com.hchc.alarm.model.MallService;
import com.hchc.alarm.pack.MallConsoleInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.hchc.alarm.constant.MallConstant.*;

/**
 * Created by wangrong 2020/5/18
 */
@Service
@Slf4j
public class BranchMallService {

    @Autowired
    private HBranchMallDao hBranchMallDao;

    public MallConsoleInfo queryMallConsoleInfos() {
        List<BranchInfo> branchInfos = new ArrayList<>();
        List<BranchInfo> branchInfos1 = hBranchMallDao.queryBranchInfos();
        List<BranchInfo> branchInfos2 = FLIP_BRANCH_DATA;
        if (CollectionUtils.isEmpty(branchInfos1) && CollectionUtils.isEmpty(branchInfos2)) {
            return null;
        } else if (!CollectionUtils.isEmpty(branchInfos1) && CollectionUtils.isEmpty(branchInfos2)) {
            branchInfos = branchInfos1;
        } else if (!CollectionUtils.isEmpty(branchInfos2) && CollectionUtils.isEmpty(branchInfos1)) {
            branchInfos = branchInfos2;
        }else {
            branchInfos.addAll(branchInfos1);
            branchInfos.addAll(branchInfos2);
        }

        Map<String, List<BranchInfo>> mallBranches = branchInfos.stream()
                .filter(b -> {  // 过滤mark名称存在的
                    if (MARK_FULL_NAME_MAP.get(b.getMark()) == null) {
                        log.info("[queryMallConsoleInfos] app cache not exist data mark : {}", b.getMark());
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.groupingBy(BranchInfo::getMark)); // 按mark分组

        List<String> cities = new ArrayList<>();
        List<MallService> malls = new ArrayList<>();
        List<String> list;
        MallService mallService;
        String fullName;
        String city;
        for (String mark : mallBranches.keySet()) {
            mallService = new MallService();
            mallService.setMark(mark);
            fullName = MARK_FULL_NAME_MAP.get(mark);
            mallService.setName(fullName.substring(2));
            city = fullName.substring(0, 2);
            mallService.setCity(city);
            cities.add(city);
            mallService.setBranchInfos(mallBranches.get(mark));
            for (BranchInfo info : mallBranches.get(mark)) {
                String mCode = info.getPushMethod();
                String mName = MallConstant.PushMethod.getNameByCode(mCode);
                if (mName != null) {
                    list = new ArrayList<>();
                    list.add(mName);
                    mallService.setMethods(list);
                    break;
                }
            }
            for (BranchInfo info : mallBranches.get(mark)) {
                String pType = null;
                if (info.getUrl() != null) {
                    pType = MallConstant.PushType.webservice.name();
                } else if (info.getFtpHost() != null) {
                    pType = MallConstant.PushType.ftp.name();
                } else if (info.getUrlHost() != null) {
                    pType = MallConstant.PushType.http.name();
                }
                if (pType != null) {
                    list = new ArrayList<>();
                    list.add(pType);
                    mallService.setTypes(list);
                    break;
                }
            }
            // 存在相同商场，合并数据
            boolean existMall = false;
            for (MallService s : malls) {
                if (s.getName().equals(mallService.getName()) && s.getCity().equals(mallService.getCity())) {
                    existMall = true;
                    s.setMark(s.getMark() + "、" + mallService.getMark());
                    s.getMethods().addAll(mallService.getMethods());
                    s.getTypes().addAll(mallService.getTypes());
                    boolean existBranch;
                    for (BranchInfo newBranch : mallService.getBranchInfos()) {
                        existBranch = false;
                        for (BranchInfo oldBranch : s.getBranchInfos()) {
                            if (newBranch.getBranchName().equals(oldBranch.getBranchName())) {
                                existBranch = true;
                                break;
                            }
                        }
                        if (!existBranch) {
                            s.getBranchInfos().add(newBranch);
                        }
                    }
                    break;
                }
            }
            if (!existMall) {
                malls.add(mallService);
            }
        }
        // 商场名排序
        malls.sort((m1, m2) -> CHINESE_COMPARATOR.compare(m1.getName(), m2.getName()));
        // 城市名排序
        List<String> cityNames = cities.stream().distinct().sorted(CHINESE_COMPARATOR::compare).collect(Collectors.toList());
        MallConsoleInfo mallConsoleInfo = new MallConsoleInfo();
        mallConsoleInfo.setMalls(malls);
        mallConsoleInfo.setCities(cityNames);
        return mallConsoleInfo;
    }
}
