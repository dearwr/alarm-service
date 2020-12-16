package com.hchc.alarm.pack;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author wangrong
 * @date 2020-12-15
 */
@Setter
@Getter
public class ActiveCardInfo {

    private String kid;

    private String cardId;

    private BigDecimal balance;

}
