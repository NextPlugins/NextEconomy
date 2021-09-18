package com.nextplugins.economy.api.conversor.impl.atlas;

import com.henryfabio.sqlprovider.executor.adapter.SQLResultAdapter;
import com.henryfabio.sqlprovider.executor.result.SimpleResultSet;
import com.nextplugins.economy.api.model.account.Account;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class AtlasEconomyAdapter implements SQLResultAdapter<Account> {

    @Override
    public Account adaptResult(SimpleResultSet resultSet) {
        return Account.generate()
                .username(resultSet.get("jogador"))
                .balance(resultSet.get("coins"))
                .result();
    }
}
