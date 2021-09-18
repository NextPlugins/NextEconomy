package com.nextplugins.economy.api.conversor.impl.soekd;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
@Getter
@AllArgsConstructor
public class SOEconomyUser {

    private final String playerName;
    private final double money;
    private final boolean togglePayment;

}
