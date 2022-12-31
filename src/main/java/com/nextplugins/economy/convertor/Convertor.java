package com.nextplugins.economy.convertor;

import com.google.common.collect.Sets;
import com.henryfabio.sqlprovider.executor.SQLExecutor;
import com.nextplugins.economy.model.account.Account;
import lombok.Data;

import java.util.Set;

@Data
public abstract class Convertor {

    private String conversorName;
    private String table;
    private SQLExecutor executor;

    public Set<Account> lookupPlayers() {
        return Sets.newHashSet();
    }

}
