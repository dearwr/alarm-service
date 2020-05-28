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
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.hchc.alarm.constant.MallConstant.*;

/**
 * @author wangrong
 */
@Service
@Slf4j
public class BranchMallService {

    @Autowired
    private BranchMallDao branchMallDao;

    public MallConsoleInfo queryMallConsoleInfos() {
        List<MallBranchBO> branchInfos = new ArrayList<>();
        List<MallBranchBO> mallBranchInfos1 = branchMallDao.queryBranchInfos(null);
        // flip服务器对接商场数据
        List<MallBranchBO> mallBranchInfos2 = FLIP_MALL_BRANCH_DATA;
        if (!CollectionUtils.isEmpty(mallBranchInfos1)) {
            branchInfos.addAll(mallBranchInfos1);
        }
        if (!CollectionUtils.isEmpty(mallBranchInfos2)) {
            branchInfos.addAll(mallBranchInfos2);
        }

        Map<String, List<MallBranchBO>> mallBranches = branchInfos.stream()
                // 过滤mark名称存在的
                .filter(b -> {
                    if (MARK_FULL_NAME_MAP.get(b.getMark()) == null) {
                        log.info("[queryMallConsoleInfos] not find mapping for mark:{}, branchId:{}", b.getMark(), b.getBranchId());
                        return false;
                    }
                    return true;
                })
                // 按mark分组
                .collect(Collectors.groupingBy(MallBranchBO::getMark));

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
        boolean existMall;
        boolean existBranch;
        for (String mark : mallBranches.keySet()) {
            cities.add(MARK_FULL_NAME_MAP.get(mark).substring(0, 2));
            mallServiceBO = new MallServiceBO();
            mallServiceBO.setMark(mark);
            mallServiceBO.setName(MARK_FULL_NAME_MAP.get(mark).substring(2));
            mallServiceBO.setCity(MARK_FULL_NAME_MAP.get(mark).substring(0, 2));
            mallServiceBO.setMallBranches(mallBranches.get(mark));
            for (MallBranchBO info : mallBranches.get(mark)) {
                pCode = info.getPushMethod();
                pName = PushMethodEm.getNameByCode(pCode);
                if (pName != null) {
                    mallServiceBO.setMethods(new ArrayList<>(Collections.singletonList(pName)));
                    break;
                }
            }
            for (MallBranchBO info : mallBranches.get(mark)) {
                pType = null;
                if (info.getUrl() != null) {
                    pType = PushTypeEm.webservice.name();
                } else if (info.getFtpHost() != null) {
                    pType = PushTypeEm.ftp.name();
                } else if (info.getUrlHost() != null) {
                    pType = PushTypeEm.http.name();
                }
                if (pType != null) {
                    mallServiceBO.setTypes(new ArrayList<>(Collections.singletonList(pType)));
                    break;
                }
            }
            // 存在相同商场，合并数据
            existMall = false;
            for (MallServiceBO s : malls) {
                if (s.getName().equals(mallServiceBO.getName()) && s.getCity().equals(mallServiceBO.getCity())) {
                    existMall = true;
                    s.setMark(s.getMark() + "、" + mallServiceBO.getMark());
                    s.getMethods().addAll(mallServiceBO.getMethods());
                    s.getTypes().addAll(mallServiceBO.getTypes());
                    for (MallBranchBO newBranch : mallServiceBO.getMallBranches()) {
                        existBranch = false;
                        for (MallBranchBO oldBranch : s.getMallBranches()) {
                            if (newBranch.getBranchName().equals(oldBranch.getBranchName())) {
                                existBranch = true;
                                break;
                            }
                        }
                        if (!existBranch) {
                            s.getMallBranches().add(newBranch);
                        }
                    }
                    break;
                }
            }
            if (!existMall) {
                malls.add(mallServiceBO);
            }
        }
    }
}
