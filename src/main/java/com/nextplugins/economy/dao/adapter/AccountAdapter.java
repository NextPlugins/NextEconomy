package com.nextplugins.economy.dao.adapter;

import com.henryfabio.sqlprovider.executor.adapter.SQLResultAdapter;
import com.henryfabio.sqlprovider.executor.result.SimpleResultSet;
import com.nextplugins.economy.api.model.Account;

import java.util.UUID;

public final class AccountAdapter implements SQLResultAdapter<Account> {

    @Override
    public Account adaptResult(SimpleResultSet resultSet) {
        UUID accountOwner = UUID.fromString(resultSet.get("owner"));
        double accountBalance = resultSet.get("balance");

        return Account.builder()
                .owner(accountOwner)
                .balance(accountBalance)
                .build();

    }

}
