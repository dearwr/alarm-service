package com.hchc.alarm.pack;

import com.hchc.alarm.model.MallService;
import lombok.Data;

import java.util.List;

/**
 * @author wangrong
 */
@Data
public class MallConsoleInfo {

    private List<String> cities;

    private List<MallService> malls;

}
