package com.hchc.alarm.service;

import com.hchc.alarm.dao.hchc.BranchMallDao;
import com.hchc.alarm.enums.PushMethodEm;
import com.hchc.alarm.enums.PushTypeEm;
import com.hchc.alarm.model.MallBranchBO;
import com.hchc.alarm.model.MallServiceBO;
import com.hchc.alarm.pack.MallConsoleInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.hchc.alarm.constant.MallConstant.CHINESE_COMPARATOR;
import static com.hchc.alarm.constant.MallConstant.MARK_NAME_MAP;

/**
 * @author wangrong
 */
@Service
@Slf4j
public class BranchMallService {

    @Autowired
    private BranchMallDao branchMallDao;

    public MallConsoleInfo queryMallConsoleInfos() {
        List<MallBranchBO> branchInfos = branchMallDao.queryBranchInfos(null);
        Map<String, List<MallBranchBO>> mallBranches = branchInfos.stream()
                // 过滤mall不存在的
                .filter(b -> {
                    if (MARK_NAME_MAP.get(b.getMallName()) == null) {
                        log.info("[queryMallConsoleInfos] not find mapping for mall:{}, branchId:{}", b.getMallName(), b.getBranchId());
                        return false;
                    }
                    return true;
                })
                // 按mark分组
                .collect(Collectors.groupingBy(MallBranchBO::getMallName));

        List<MallServiceBO> malls = new ArrayList<>();
        List<String> cities = new ArrayList<>();
        handleMallBranches(mallBranches, malls, cities);
        // 商场名排序
        malls.sort((m1, m2) -> CHINESE_COMPARATOR.compare(m1.getName(), m2.getName()));
        // 城市名排序
        List<String> cityNames = cities.stream().distinct().sorted(CHINESE_COMPARATOR::compare).collect(Collectors.toList());
        // 筛选品牌名及对应的商场
        Map<String, List<MallServiceBO>> brandMalls = new HashMap<>(128);
        List<MallServiceBO> mallList;
        String brandName;
        boolean exitMall;
        for (MallServiceBO mall : malls) {
            for (MallBranchBO mallBranch : mall.getMallBranches()) {
                brandName = mallBranch.getBrandName();
                if (brandName == null) {
                    continue;
                }
                exitMall = false;
                if (brandMalls.containsKey(brandName)) {
                    for (MallServiceBO brandMall : brandMalls.get(brandName)) {
                        if (brandMall.getName().equals(mall.getName()) && brandMall.getCity().equals(mall.getCity())) {
                            exitMall = true;
                            break;
                        }
                    }
                    if (!exitMall) {
                        brandMalls.get(brandName).add(mall);
                    }
                } else {
                    mallList = new ArrayList<>();
                    mallList.add(mall);
                    brandMalls.put(brandName, mallList);
                }
            }
        }
        MallConsoleInfo mallConsoleInfo = new MallConsoleInfo();
        mallConsoleInfo.setMalls(malls);
        mallConsoleInfo.setCities(cityNames);
        mallConsoleInfo.setBrandMalls(brandMalls);
        return mallConsoleInfo;
    }

    private void handleMallBranches(Map<String, List<MallBranchBO>> mallBranches, List<MallServiceBO> malls, List<String> cities) {
        MallServiceBO mallServiceBO;
        String pCode;
        String pName;
        String pType;
        for (String mall : mallBranches.keySet()) {
            cities.add(MARK_NAME_MAP.get(mall).substring(0, 2));
            mallServiceBO = new MallServiceBO();
            mallServiceBO.setMall(mall);
            mallServiceBO.setName(MARK_NAME_MAP.get(mall));
            mallServiceBO.setCity(MARK_NAME_MAP.get(mall).substring(0, 2));
            mallServiceBO.setMallBranches(mallBranches.get(mall));
            for (MallBranchBO info : mallBranches.get(mall)) {
                pCode = info.getType();
                pName = PushMethodEm.getNameByCode(pCode);
                if (pName != null) {
                    mallServiceBO.setMethod(pName);
                    break;
                }
            }
            for (MallBranchBO info : mallBranches.get(mall)) {
                pType = null;
                if (info.getUrl() != null) {
                    pType = PushTypeEm.webservice.name();
                } else if (info.getFtpHost() != null) {
                    pType = PushTypeEm.ftp.name();
                } else if (info.getUrlHost() != null) {
                    pType = PushTypeEm.http.name();
                }
                if (pType != null) {
                    mallServiceBO.setType(pType);
                    break;
                }
            }
            malls.add(mallServiceBO);
        }
    }
}
