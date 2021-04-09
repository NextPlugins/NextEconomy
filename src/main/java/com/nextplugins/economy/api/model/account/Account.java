package com.nextplugins.economy.api.model.account;

import com.nextplugins.economy.configuration.values.FeatureValue;
import lombok.AccessLevel;
import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {

    private final String userName;
    private double balance;
    private double movimentedBalance;
    private int transactions;

    public static Account createDefault(String name) {

        return create(
                name,
                FeatureValue.get(FeatureValue::initialBalance),
                0,
                FeatureValue.get(FeatureValue::initialBalance) >= 0 ? 1 : 0
        );

    }

    public static Account create(String name, double balance, double movimentedBalance, int transactions) {

        return new Account(
                name,
                balance,
                movimentedBalance,
                transactions
        );

    }

    public synchronized void depositAmount(double amount) {

        this.balance = balance + amount;
        ++this.transactions;

    }

    public synchronized void withdrawAmount(double amount) {

        this.balance = balance - amount;

        ++this.transactions;
        this.movimentedBalance += amount;

    }

    public synchronized boolean hasAmount(double amount) {
        return this.balance >= amount;
    }

}
