package com.nextplugins.economy.api.conversor.impl.atlas;

import com.nextplugins.economy.api.conversor.Conversor;
import com.nextplugins.economy.model.account.Account;

import java.util.Set;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class AtlasEconomyConversor extends Conversor {

    @Override
    public Set<Account> lookupPlayers() {
        return getExecutor().resultManyQuery(
                "SELECT * FROM " + getTable(),
                statement -> {
                },
                AtlasEconomyAdapter.class
        );
    }
}
