package com.nextplugins.economy.api.model.account;

import com.google.common.collect.Lists;
import com.nextplugins.economy.api.model.account.historic.AccountBankHistoric;
import com.nextplugins.economy.api.model.account.transaction.TransactionType;
import com.nextplugins.economy.configuration.FeatureValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.val;

import java.util.LinkedList;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {

    private final String userName;
    private double balance;
    private double movimentedBalance;
    private int transactionsQuantity;

    private LinkedList<AccountBankHistoric> transactions;

    public static Account createDefault(String name) {

        return create(
                name,
                FeatureValue.get(FeatureValue::initialBalance),
                0,
                0,
                Lists.newLinkedList()
        );

    }

    public static Account create(String name,
                                 double balance,
                                 double movimentedBalance,
                                 int transactionsQuantity,
                                 LinkedList<AccountBankHistoric> transactions) {

        return new Account(
                name,
                balance,
                movimentedBalance,
                transactionsQuantity,
                transactions
        );

    }

    public synchronized void createTransaction(String owner,
                                               double amount,
                                               TransactionType transactionType) {

        if (transactionType == TransactionType.WITHDRAW) {

            movimentedBalance += amount;
            amount *= -1;

        }

        this.balance += amount;
        if (this.balance < 0) this.balance = 0;

        if (owner != null) {

            ++transactionsQuantity;

            val historic = AccountBankHistoric.builder()
                    .target(owner)
                    .amount(amount < 0 ? amount * -1 : amount)
                    .type(transactionType)
                    .build();

            if (transactions.size() >= 56) transactions.remove(0);
            transactions.add(historic);

        }

    }

    public synchronized boolean hasAmount(double amount) {
        return this.balance >= amount;
    }

}
