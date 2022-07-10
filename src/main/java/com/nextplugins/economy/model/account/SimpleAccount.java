package com.nextplugins.economy.model.account;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
@Data
@Builder(builderMethodName = "generate", buildMethodName = "result")
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SimpleAccount {

    private final String username;
    private final String balanceFormated;
    private final String movimentedBalanceFormated;
    private final String tycoonTag;

    private final int transactionsQuantity;

}
