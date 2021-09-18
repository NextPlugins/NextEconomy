package com.nextplugins.economy.api.conversor.impl.ystore;

import com.henryfabio.sqlprovider.executor.SQLExecutor;
import com.nextplugins.economy.api.conversor.Conversor;
import com.nextplugins.economy.api.model.account.Account;

import java.util.Set;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class YEconomyConversor extends Conversor {

    protected YEconomyConversor(String conversorName, String table, SQLExecutor executor) {
        super(conversorName, table, executor);
    }

    @Override
    public Set<Account> lookupPlayers() {
        return getExecutor().resultManyQuery(
                "SELECT * FROM " + getTable(),
                statement -> {},
                YEconomyAdapter.class
        );
    }

}
