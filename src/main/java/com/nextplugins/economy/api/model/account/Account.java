package com.nextplugins.economy.api.model.account;

import com.nextplugins.economy.configuration.values.FeatureValue;
import lombok.*;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {

    private final String userName;
    private double balance;

    public static Account createDefault(String name) {
        return new Account(name, FeatureValue.get(FeatureValue::initialBalance));
    }

    public static Account create(String name, double balance) {
        return new Account(name, balance);
    }

    public synchronized void depositAmount(double amount) {
        this.balance = balance + amount;
    }

    public synchronized void withdrawAmount(double amount) {
        this.balance = balance - amount;
    }

    public synchronized boolean hasAmount(double amount) {
        return this.balance >= amount;
    }

}
