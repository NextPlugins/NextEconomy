package com.nextplugins.economy.dao.repository.adapter;

import com.henryfabio.sqlprovider.executor.adapter.SQLResultAdapter;
import com.henryfabio.sqlprovider.executor.result.SimpleResultSet;
import com.nextplugins.economy.api.model.account.SimpleAccount;
import com.nextplugins.economy.util.NumberUtils;

public final class SimpleAccountAdapter implements SQLResultAdapter<SimpleAccount> {

    @Override
    public SimpleAccount adaptResult(SimpleResultSet resultSet) {
        String accountOwner = resultSet.get("owner");

        double accountBalance = resultSet.get("balance");
        double movimentedBalance = resultSet.get("movimentedBalance");

        int transactionsQuantity = resultSet.get("transactionsQuantity");

        return SimpleAccount.generate()
                .username(accountOwner)
                .balanceFormated(NumberUtils.format(accountBalance))
                .movimentedBalanceFormated(NumberUtils.format(movimentedBalance))
                .transactionsQuantity(transactionsQuantity)
                .result();
    }

}
