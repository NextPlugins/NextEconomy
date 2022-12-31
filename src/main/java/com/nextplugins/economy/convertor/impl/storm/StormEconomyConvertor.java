package com.nextplugins.economy.convertor.impl.storm;

import com.nextplugins.economy.convertor.Convertor;
import com.nextplugins.economy.model.account.Account;

import java.util.Set;

public class StormEconomyConvertor extends Convertor {

    @Override
    public Set<Account> lookupPlayers() {
        return getExecutor().resultManyQuery(
                "SELECT * FROM " + getTable(),
                statement -> {},
                StormEconomyAdapter.class
        );
    }

}
