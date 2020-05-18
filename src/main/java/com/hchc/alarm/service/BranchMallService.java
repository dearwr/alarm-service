package com.hchc.alarm.service;

import com.hchc.alarm.constant.MallConstant;
import com.hchc.alarm.dao.hchc.BranchMallDao;
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
    private BranchMallDao branchMallDao;

    public MallConsoleInfo queryMallConsoleInfos() {
        List<BranchInfo> branchInfos = branchMallDao.queryMallConsoleInfos();
        if (CollectionUtils.isEmpty(branchInfos)) {
            return null;
        }
        MallConsoleInfo mallConsoleInfo = new MallConsoleInfo();
        Set<String> cities = new HashSet<>();
        List<MallService> services = new ArrayList<>();
        branchInfos.stream()
                .filter(b -> {  // 过滤mark名称存在的
                    if (MallConstant.MARK_NAME_MAP.get(b.getMark()) == null) {
                        log.info("[queryMallConsoleInfos] app cache not exist markNameMapping: {}", b.getMark());
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.groupingBy(BranchInfo::getMark))  // 按mark分组
                .forEach((mark, branchList) -> {
                    MallService mallService = new MallService();
                    mallService.setMark(mark);
                    mallService.setName(MallConstant.MARK_NAME_MAP.get(mark));
                    String city = mallService.getName().substring(0, 2);
                    mallService.setCity(city);
                    cities.add(city);
                    mallService.setBranchInfos(branchList);
                    for (BranchInfo info : branchList) {
                        String method = info.getPushMethod();
                        if (MallConstant.PushMethod.getNameByMethod(method) != null) {
                            mallService.setPushMethod(method);
                            break;
                        }
                    }
                    for (BranchInfo info : branchList) {
                        String pType = null;
                        if (info.getUrl() != null) {
                            pType = MallConstant.PushType.webservice.name();
                        } else if (info.getFtpHost() != null) {
                            pType = MallConstant.PushType.ftp.name();
                        } else if (info.getUrlHost() != null) {
                            pType = MallConstant.PushType.http.name();
                        }
                        if (pType != null) {
                            mallService.setPushType(pType);
                            break;
                        }
                    }
                    services.add(mallService);
                });
        services.sort((m1, m2) -> CHINESE_COMPARATOR.compare(m1.getName(), m2.getName())); // 排序
        mallConsoleInfo.setCities(cities);
        mallConsoleInfo.setMalls(services);
        return mallConsoleInfo;
    }
}
