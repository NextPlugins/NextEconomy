package com.nextplugins.economy.api.model.account;

import com.google.common.collect.Lists;
import com.nextplugins.economy.api.model.account.historic.AccountBankHistoric;
import com.nextplugins.economy.api.model.account.transaction.TransactionType;
import com.nextplugins.economy.configuration.values.FeatureValue;
import lombok.AccessLevel;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.val;

import java.util.LinkedList;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {

    private final String userName;
    private double balance;
    private double movimentedBalance;

    private LinkedList<AccountBankHistoric> transactions;

    public static Account createDefault(String name) {

        return create(
                name,
                FeatureValue.get(FeatureValue::initialBalance),
                0,
                Lists.newLinkedList()
        );

    }

    public static Account create(String name, double balance, double movimentedBalance, LinkedList<AccountBankHistoric> transactions) {

        return new Account(
                name,
                balance,
                movimentedBalance,
                transactions
        );

    }

    public synchronized void createTransaction(String owner, double amount, TransactionType transactionType) {

        if (transactionType == TransactionType.WITHDRAW) amount *= -1;

        this.balance += amount;

        val historic = AccountBankHistoric.builder()
                .target(owner)
                .amount(amount * -1)
                .type(transactionType)
                .build();

        transactions.add(historic);

    }

    public synchronized boolean hasAmount(double amount) {
        return this.balance >= amount;
    }

}
