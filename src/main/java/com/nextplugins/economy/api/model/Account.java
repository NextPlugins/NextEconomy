package com.nextplugins.economy.api.model;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class Account {

    private UUID owner;
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
