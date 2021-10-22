package com.nextplugins.economy.api.conversor.impl.storm;

import com.google.gson.Gson;
import com.henryfabio.sqlprovider.executor.adapter.SQLResultAdapter;
import com.henryfabio.sqlprovider.executor.result.SimpleResultSet;
import com.nextplugins.economy.api.model.account.Account;
import lombok.val;

public class StormEconomyAdapter implements SQLResultAdapter<Account> {

    private final Gson accountParser = new Gson();

    @Override
    public Account adaptResult(SimpleResultSet resultSet) {
        val user = accountParser.fromJson((String) resultSet.get("json"), StormEconomyUser.class);
        return Account.generate()
                .username(user.getJogador())
                .balance(user.getCoins())
                .transactions(user.getTransactions())
                .result();
    }
}
