package com.nextplugins.economy.dao.repository.adapter;

import com.henryfabio.sqlprovider.executor.adapter.SQLResultAdapter;
import com.henryfabio.sqlprovider.executor.result.SimpleResultSet;
import com.nextplugins.economy.api.model.account.Account;
import com.nextplugins.economy.api.model.account.historic.AccountBankHistoric;
import com.nextplugins.economy.util.LinkedListHelper;

public final class AccountAdapter implements SQLResultAdapter<Account> {

    private static final LinkedListHelper<AccountBankHistoric> PARSER = new LinkedListHelper<>();

    @Override
    public Account adaptResult(SimpleResultSet resultSet) {

        String accountOwner = resultSet.get("owner");
        String transactions = resultSet.get("transactions");

        double accountBalance = resultSet.get("balance");

        double movimentedBalance = resultSet.get("movimentedBalance");
        int transactionsQuantity = resultSet.get("transactionsQuantity");

        long discordId = -1;
        boolean receiveCoins = true;

        try {

            Integer discord = resultSet.get("discordId");
            discordId = discord.longValue();

            receiveCoins = resultSet.get("receiveCoins");

        } catch (NullPointerException ignored) { }

        return Account.generate()
                .username(accountOwner)
                .balance(accountBalance)
                .receiveCoins(receiveCoins)
                .movimentedBalance(movimentedBalance)
                .transactionsQuantity(transactionsQuantity)
                .transactions(PARSER.fromJson(transactions))
                .discordId(discordId)
                .result();

    }

}
