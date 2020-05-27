package com.hchc.alarm.pack;

import com.hchc.alarm.model.MallService;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author wangrong
 */
@Data
public class MallConsoleInfo {

    private List<String> cities;

    private List<MallService> malls;

    private Map<String, List<MallService>> brandMalls;

}
