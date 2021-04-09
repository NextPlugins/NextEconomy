package com.nextplugins.economy.dao.adapter;

import com.henryfabio.sqlprovider.executor.adapter.SQLResultAdapter;
import com.henryfabio.sqlprovider.executor.result.SimpleResultSet;
import com.nextplugins.economy.api.model.account.Account;

public final class AccountAdapter implements SQLResultAdapter<Account> {

    @Override
    public Account adaptResult(SimpleResultSet resultSet) {

        String accountOwner = resultSet.get("owner");
        double accountBalance = resultSet.get("balance");
        double movimentedBalance = resultSet.get("movimentedBalance");
        int transactions = resultSet.get("transactions");

        return Account.create(accountOwner, accountBalance, movimentedBalance, transactions);

    }

}
