package com.nextplugins.economy.api.model.account.transaction;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum TransactionType {

    DEPOSIT("Recebeu"),
    WITHDRAW("Enviou");

    @Getter private final String message;

}
