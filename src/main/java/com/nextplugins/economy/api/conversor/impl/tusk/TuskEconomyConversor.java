package com.nextplugins.economy.api.conversor.impl.tusk;

import com.henryfabio.sqlprovider.executor.SQLExecutor;
import com.nextplugins.economy.api.conversor.Conversor;
import com.nextplugins.economy.api.model.account.Account;

import java.util.Set;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class TuskEconomyConversor extends Conversor {

    protected TuskEconomyConversor(String table, SQLExecutor executor) {
        super("TuskEconomy", table, executor);
    }

    @Override
    public Set<Account> lookupPlayers() {
        return getExecutor().resultManyQuery(
                "SELECT * FROM " + getTable(),
                statement -> {},
                TuskEconomyAdapter.class
        );
    }

}
