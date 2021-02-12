package com.nextplugins.economy.api.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Account {

    private String owner;
    private double balance;

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
