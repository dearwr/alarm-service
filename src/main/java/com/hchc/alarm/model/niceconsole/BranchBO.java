package com.hchc.alarm.model.niceconsole;

import lombok.Data;

/**
 * @author wangrong
 * @date 2020-06-18
 */
@Data
public class BranchBO {

    private long id;

    private String name;

    private String businessHours;

    private String province;

    private String city;

    private String address;
}
