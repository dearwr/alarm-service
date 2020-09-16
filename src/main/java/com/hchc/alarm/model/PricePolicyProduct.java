package com.hchc.alarm.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author wangrong
 * @date 2020-09-15
 */
@Setter
@Getter
public class PricePolicyProduct {

    private int id;
    private int productId;
    private double price;
    private int policyId;

}
