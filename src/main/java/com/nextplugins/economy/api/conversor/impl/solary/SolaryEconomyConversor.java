package com.nextplugins.economy.api.conversor.impl.solary;

import com.henryfabio.sqlprovider.executor.SQLExecutor;
import com.nextplugins.economy.api.conversor.Conversor;
import com.nextplugins.economy.api.model.account.Account;

import java.util.Set;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class SolaryEconomyConversor extends Conversor {

    protected SolaryEconomyConversor(String conversorName, String table, SQLExecutor executor) {
        super(conversorName, table, executor);
    }

    @Override
    public Set<Account> lookupPlayers() {
        return getExecutor().resultManyQuery(
                "SELECT * FROM " + getTable(),
                statement -> {},
                SolaryEconomyAdapter.class
        );
    }

}
