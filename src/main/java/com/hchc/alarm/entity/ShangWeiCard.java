package com.hchc.alarm.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @author wangrong
 * @date 2020-11-23
 */
@Setter
@Getter
public class ShangWeiCard {

    private String no;
    private String type;
    private double beforeBalance;
    private double balance;
    private double afterBalance;
}
