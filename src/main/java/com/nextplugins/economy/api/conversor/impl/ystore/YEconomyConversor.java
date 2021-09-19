package com.nextplugins.economy.api.conversor.impl.ystore;

import com.nextplugins.economy.api.conversor.Conversor;
import com.nextplugins.economy.api.model.account.Account;

import java.util.Set;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class YEconomyConversor extends Conversor {

    @Override
    public Set<Account> lookupPlayers() {
        return getExecutor().resultManyQuery(
                "SELECT * FROM " + getTable(),
                statement -> {},
                YEconomyAdapter.class
        );
    }

}
