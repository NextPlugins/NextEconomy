package com.nextplugins.economy.api.conversor.impl.storm;

import com.nextplugins.economy.api.conversor.Conversor;
import com.nextplugins.economy.model.account.Account;

import java.util.Set;

public class StormEconomyConversor extends Conversor {

    @Override
    public Set<Account> lookupPlayers() {
        return getExecutor().resultManyQuery(
                "SELECT * FROM " + getTable(),
                statement -> {},
                StormEconomyAdapter.class
        );
    }

}
