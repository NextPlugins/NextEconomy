package com.nextplugins.economy.dao.repository.adapter;

import com.henryfabio.sqlprovider.executor.adapter.SQLResultAdapter;
import com.henryfabio.sqlprovider.executor.result.SimpleResultSet;
import com.nextplugins.economy.api.model.account.Account;
import com.nextplugins.economy.util.LinkedListHelper;

public final class AccountAdapter implements SQLResultAdapter<Account> {

    @Override
    public Account adaptResult(SimpleResultSet resultSet) {

        String accountOwner = resultSet.get("owner");
        double accountBalance = resultSet.get("balance");
        double movimentedBalance = resultSet.get("movimentedBalance");
        int transactionsQuantity = resultSet.get("transactionsQuantity");
        String transactions = resultSet.get("transactions");

        return Account.create(
                accountOwner,
                accountBalance,
                movimentedBalance,
                transactionsQuantity,
                LinkedListHelper.fromJson(transactions)
        );

    }

}
