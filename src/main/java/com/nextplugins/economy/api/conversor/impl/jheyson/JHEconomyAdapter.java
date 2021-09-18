package com.nextplugins.economy.api.conversor.impl.jheyson;

import com.henryfabio.sqlprovider.executor.adapter.SQLResultAdapter;
import com.henryfabio.sqlprovider.executor.result.SimpleResultSet;
import com.nextplugins.economy.api.model.account.Account;
import com.nextplugins.economy.util.NumberUtils;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class JHEconomyAdapter implements SQLResultAdapter<Account> {

    @Override
    public Account adaptResult(SimpleResultSet resultSet) {
        return Account.generate()
                .username(resultSet.get("RealNick"))
                .balance(NumberUtils.parse(resultSet.get("Money")))
                .result();
    }
}
