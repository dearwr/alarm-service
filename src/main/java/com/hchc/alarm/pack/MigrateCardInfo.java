package com.hchc.alarm.pack;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author wangrong
 * @date 2020-12-15
 */
@Getter
@Setter
public class MigrateCardInfo {

    private long hqId;
    private String cardId;
    private String kid;
    private String password;
    private BigDecimal balance;
    private boolean giveCard;
    private boolean needPush;
}
