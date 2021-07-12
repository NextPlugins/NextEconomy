package com.nextplugins.economy.dao.repository.adapter;

import com.google.common.collect.Lists;
import com.henryfabio.sqlprovider.executor.adapter.SQLResultAdapter;
import com.henryfabio.sqlprovider.executor.result.SimpleResultSet;
import com.nextplugins.economy.api.model.account.Account;
import com.nextplugins.economy.api.model.account.historic.AccountBankHistoric;
import com.nextplugins.economy.util.ListSerializerHelper;
import lombok.val;

public final class AccountAdapter implements SQLResultAdapter<Account> {

    private static final ListSerializerHelper<AccountBankHistoric> PARSER = new ListSerializerHelper<>();

    @Override
    public Account adaptResult(SimpleResultSet resultSet) {

        String accountOwner = resultSet.get("owner");
        String transactions = resultSet.get("transactions");

        double accountBalance = resultSet.get("balance");
        double movimentedBalance = resultSet.get("movimentedBalance");

        int transactionsQuantity = resultSet.get("transactionsQuantity");
        int receiveCoins = resultSet.get("receiveCoins");

        val accountBankHistorics = Lists.newLinkedList(PARSER.fromJson(transactions));

        return Account.generate()
                .username(accountOwner)
                .balance(accountBalance)
                .receiveCoins(receiveCoins != 0)
                .movimentedBalance(movimentedBalance)
                .transactionsQuantity(transactionsQuantity)
                .transactions(accountBankHistorics)
                .result();

    }

}
