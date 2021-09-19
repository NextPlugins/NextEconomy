package com.nextplugins.economy.api.conversor;

import com.google.common.collect.Sets;
import com.henryfabio.sqlprovider.executor.SQLExecutor;
import com.nextplugins.economy.api.model.account.Account;
import lombok.Data;

import java.util.Set;

@Data
public abstract class Conversor {

    private String conversorName;
    private String table;
    private SQLExecutor executor;

    public Set<Account> lookupPlayers() {
        return Sets.newHashSet();
    }

}
