package com.nextplugins.economy.api.conversor;

import com.google.common.collect.Sets;
import com.henryfabio.sqlprovider.executor.SQLExecutor;
import com.nextplugins.economy.api.model.account.Account;
import lombok.Getter;

import java.util.Set;

@Getter
public abstract class Conversor {

    private final String conversorName;
    private final String table;
    private final SQLExecutor executor;

    protected Conversor(String conversorName, String table, SQLExecutor executor) {
        this.conversorName = conversorName;
        this.table = table;
        this.executor = executor;
    }

    public Set<Account> lookupPlayers() {
        return Sets.newHashSet();
    }

}
