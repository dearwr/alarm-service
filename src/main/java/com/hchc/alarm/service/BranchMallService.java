package com.hchc.alarm.service;

import com.hchc.alarm.constant.MallConstant;
import com.hchc.alarm.dao.flip.FBranchMallDao;
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

import static com.hchc.alarm.constant.MallConstant.CHINESE_COMPARATOR;

/**
 * Created by wangrong 2020/5/18
 */
@Service
@Slf4j
public class BranchMallService {

    @Autowired
    private HBranchMallDao HBranchMallDao;
    @Autowired
    private FBranchMallDao fBranchMallDao;

    public MallConsoleInfo queryMallConsoleInfos() {
        List<BranchInfo> branchInfos1 = HBranchMallDao.queryMallConsoleInfos();
        List<BranchInfo> branchInfos2 = fBranchMallDao.queryMallConsoleInfos();
        List<BranchInfo> branchInfos = new ArrayList<>();
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
                    if (MallConstant.MARK_NAME_MAP.get(b.getMark()) == null) {
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
        for (String mark : mallBranches.keySet()) {
            mallService = new MallService();
            mallService.setMark(mark);
            mallService.setName(MallConstant.MARK_NAME_MAP.get(mark));
            String city = mallService.getName().substring(0, 2);
            mallService.setCity(city);
            cities.add(city);
            mallService.setBranchInfos(new HashSet<>(mallBranches.get(mark)));
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
            boolean exist = false;
            for (MallService s : malls) {
                if (s.getName().equals(mallService.getName())) {
                    exist = true;
                    s.setMark(s.getMark() + "、" + mallService.getMark());
                    s.getMethods().addAll(mallService.getMethods());
                    s.getTypes().addAll(mallService.getTypes());
                    s.getBranchInfos().addAll(mallService.getBranchInfos());
                    break;
                }
            }
            if (!exist) {
                malls.add(mallService);
            }
        }

        malls.sort((m1, m2) -> CHINESE_COMPARATOR.compare(m1.getName(), m2.getName())); // 按商场名排序
        List<String> cityNames = cities.stream().distinct().sorted(CHINESE_COMPARATOR::compare).collect(Collectors.toList());
        MallConsoleInfo mallConsoleInfo = new MallConsoleInfo();
        mallConsoleInfo.setMalls(malls);
        mallConsoleInfo.setCities(cityNames);
        return mallConsoleInfo;
    }
}
