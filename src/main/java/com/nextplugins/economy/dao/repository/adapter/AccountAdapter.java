package com.nextplugins.economy.dao.repository.adapter;

import com.henryfabio.sqlprovider.executor.adapter.SQLResultAdapter;
import com.henryfabio.sqlprovider.executor.result.SimpleResultSet;
import com.nextplugins.economy.api.model.account.Account;
import com.nextplugins.economy.api.model.account.historic.AccountBankHistoric;
import com.nextplugins.economy.util.LinkedListHelper;
import lombok.val;

public final class AccountAdapter implements SQLResultAdapter<Account> {

    private static final LinkedListHelper<AccountBankHistoric> PARSER = new LinkedListHelper<>();

    @Override
    public Account adaptResult(SimpleResultSet resultSet) {

        String accountOwner = resultSet.get("owner");
        String transactions = resultSet.get("transactions");

        double accountBalance = resultSet.get("balance");

        double movimentedBalance = resultSet.get("movimentedBalance");
        int transactionsQuantity = resultSet.get("transactionsQuantity");

        long discordId = 0;

        try {

            Integer discord = resultSet.get("discordId");
            discordId = discord.longValue();

        } catch (NullPointerException ignored) { }


        return Account.create(
                accountOwner,
                accountBalance,
                movimentedBalance,
                transactionsQuantity,
                PARSER.fromJson(transactions),
                discordId
        );

    }

}
