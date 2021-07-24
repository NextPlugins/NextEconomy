package com.nextplugins.economy.api.model.account;

import com.google.common.collect.Lists;
import com.nextplugins.economy.api.model.account.historic.AccountBankHistoric;
import com.nextplugins.economy.util.NumberUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.LinkedList;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
@Data
@Builder(builderMethodName = "generate", buildMethodName = "result")
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SimpleAccount {

    private final String username;
    private final double balance;
    private final double movimentedBalance;

    private final int transactionsQuantity;
    @Builder.Default private final LinkedList<AccountBankHistoric> transactions = Lists.newLinkedList();

    public synchronized String getBalanceFormated() {
        return NumberUtils.format(getBalance());
    }

}
