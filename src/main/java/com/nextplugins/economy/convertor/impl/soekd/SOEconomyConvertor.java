package com.nextplugins.economy.convertor.impl.soekd;

import com.nextplugins.economy.convertor.Convertor;
import com.nextplugins.economy.model.account.Account;

import java.util.Set;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class SOEconomyConvertor extends Convertor {

    @Override
    public Set<Account> lookupPlayers() {
        return getExecutor().resultManyQuery(
                "SELECT * FROM " + getTable(),
                statement -> {},
                SOEconomyAdapter.class
        );
    }
}
