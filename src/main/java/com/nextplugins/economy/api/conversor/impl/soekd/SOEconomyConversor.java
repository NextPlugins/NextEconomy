package com.nextplugins.economy.api.conversor.impl.soekd;

import com.henryfabio.sqlprovider.connector.SQLConnector;
import com.henryfabio.sqlprovider.executor.SQLExecutor;
import com.nextplugins.economy.api.conversor.Conversor;
import com.nextplugins.economy.api.model.account.Account;

import java.util.Set;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class SOEconomyConversor extends Conversor {

    protected SOEconomyConversor(String conversorName, String table, SQLConnector connector) {
        super(conversorName, table, new SQLExecutor(connector));
    }

    @Override
    public Set<Account> lookupPlayers() {
        return getExecutor().resultManyQuery(
                "SELECT * FROM " + getTable(),
                statement -> {},
                SOEconomyAdapter.class
        );
    }
}
