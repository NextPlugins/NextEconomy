package com.nextplugins.economy.dao.repository.adapter;

import com.henryfabio.sqlprovider.executor.adapter.SQLResultAdapter;
import com.henryfabio.sqlprovider.executor.result.SimpleResultSet;
import com.nextplugins.economy.model.account.SimpleAccount;
import com.nextplugins.economy.util.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class SimpleAccountAdapter implements SQLResultAdapter<SimpleAccount> {

    @Override
    public SimpleAccount adaptResult(SimpleResultSet resultSet) {
        String accountOwner = resultSet.get("owner");

        if (accountOwner.length() > 16) {
            final Player player = Bukkit.getPlayer(UUID.fromString(accountOwner));

            if (player != null) {
                accountOwner = player.getName();
            }
        }

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
