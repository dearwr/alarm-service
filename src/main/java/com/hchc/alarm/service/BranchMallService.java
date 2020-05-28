package com.hchc.alarm.service;

import com.hchc.alarm.constant.MallConstant;
import com.hchc.alarm.dao.hchc.BranchMallDao;
import com.hchc.alarm.model.MallBranch;
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
 * @author wangrong
 */
@Service
@Slf4j
public class BranchMallService {

    @Autowired
    private BranchMallDao branchMallDao;

    public MallConsoleInfo queryMallConsoleInfos() {
        List<MallBranch> branchInfos = new ArrayList<>();
        List<MallBranch> mallBranchInfos1 = branchMallDao.queryBranchInfos(null);
        // flip服务器对接商场数据
        List<MallBranch> mallBranchInfos2 = FLIP_MALL_BRANCH_DATA;
        if (!CollectionUtils.isEmpty(mallBranchInfos1)) {
            branchInfos.addAll(mallBranchInfos1);
        }
        if (!CollectionUtils.isEmpty(mallBranchInfos2)) {
            branchInfos.addAll(mallBranchInfos2);
        }

        Map<String, List<MallBranch>> mallBranches = branchInfos.stream()
                // 过滤mark名称存在的
                .filter(b -> {
                    if (MARK_FULL_NAME_MAP.get(b.getMark()) == null) {
                        log.info("[queryMallConsoleInfos] app cache not exist data mark : {}", b.getMark());
                        return false;
                    }
                    return true;
                })
                // 按mark分组
                .collect(Collectors.groupingBy(MallBranch::getMark));

        List<MallService> malls = new ArrayList<>();
        List<String> cities = new ArrayList<>();
        handleMallBranches(mallBranches, malls, cities);
        // 商场名排序
        malls.sort((m1, m2) -> CHINESE_COMPARATOR.compare(m1.getName(), m2.getName()));
        // 城市名排序
        List<String> cityNames = cities.stream().distinct().sorted(CHINESE_COMPARATOR::compare).collect(Collectors.toList());
        // 筛选品牌名及对应的商场
        Map<String, List<MallService>> brandMalls = new HashMap<>(128);
        List<MallService> mallList;
        String brandName;
        boolean exitMall;
        for (MallService mall : malls) {
            for (MallBranch mallBranch : mall.getMallBranches()) {
                brandName = mallBranch.getBrandName();
                if (brandName == null) {
                    continue;
                }
                exitMall = false;
                if (brandMalls.containsKey(brandName)) {
                    for (MallService brandMall : brandMalls.get(brandName)) {
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

    private void handleMallBranches(Map<String, List<MallBranch>> mallBranches, List<MallService> malls, List<String> cities) {
        MallService mallService;
        for (String mark : mallBranches.keySet()) {
            cities.add(MARK_FULL_NAME_MAP.get(mark).substring(0, 2));
            mallService = new MallService();
            mallService.setMark(mark);
            mallService.setName(MARK_FULL_NAME_MAP.get(mark).substring(2));
            mallService.setCity(MARK_FULL_NAME_MAP.get(mark).substring(0, 2));
            mallService.setMallBranches(mallBranches.get(mark));
            for (MallBranch info : mallBranches.get(mark)) {
                String mCode = info.getPushMethod();
                String mName = MallConstant.PushMethod.getNameByCode(mCode);
                if (mName != null) {
                    mallService.setMethods(new ArrayList<>(Collections.singletonList(mName)));
                    break;
                }
            }
            for (MallBranch info : mallBranches.get(mark)) {
                String pType = null;
                if (info.getUrl() != null) {
                    pType = MallConstant.PushType.webservice.name();
                } else if (info.getFtpHost() != null) {
                    pType = MallConstant.PushType.ftp.name();
                } else if (info.getUrlHost() != null) {
                    pType = MallConstant.PushType.http.name();
                }
                if (pType != null) {
                    mallService.setTypes(new ArrayList<>(Collections.singletonList(pType)));
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
                    for (MallBranch newBranch : mallService.getMallBranches()) {
                        existBranch = false;
                        for (MallBranch oldBranch : s.getMallBranches()) {
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
                malls.add(mallService);
            }
        }
    }
}
