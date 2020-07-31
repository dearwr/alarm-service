package com.hchc.alarm.pack;

import com.hchc.alarm.model.MallServiceBO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * @author wangrong
 */
@Getter
@Setter
public class MallConsoleInfo {

    private List<String> cities;

    private List<MallServiceBO> malls;

    private Map<String, List<MallServiceBO>> brandMalls;

}
