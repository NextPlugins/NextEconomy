package com.nextplugins.economy.convertor.impl.storm;

import com.google.common.collect.Lists;
import com.nextplugins.economy.model.account.historic.AccountBankHistoric;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.LinkedList;

@Getter
@AllArgsConstructor
public class StormEconomyUser {

    private final double coins;
    private final String jogador; // cringe
    private final LinkedList<AccountBankHistoric> transactions = Lists.newLinkedList();

}