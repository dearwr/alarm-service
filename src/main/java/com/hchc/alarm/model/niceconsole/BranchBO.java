package com.hchc.alarm.model.niceconsole;

import lombok.Getter;
import lombok.Setter;

/**
 * @author wangrong
 * @date 2020-06-18
 */
@Getter
@Setter
public class BranchBO {

    private long id;

    private String name;

    private String businessHours;

    private String province;

    private String city;

    private String address;
}
