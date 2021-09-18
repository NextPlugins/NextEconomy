package com.nextplugins.economy.api.conversor.impl.essentials.essentialsx;

import com.henryfabio.sqlprovider.executor.SQLExecutor;
import com.nextplugins.economy.api.conversor.Conversor;
import com.nextplugins.economy.api.model.account.Account;

import java.util.Set;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class EssentialsXConversor extends Conversor {

    protected EssentialsXConversor(String conversorName, String table, SQLExecutor executor) {
        super(conversorName, table, executor);
    }

    @Override
    public Set<Account> lookupPlayers() {
        return super.lookupPlayers();
    }

}
