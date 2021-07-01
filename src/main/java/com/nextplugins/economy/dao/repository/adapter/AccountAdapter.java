package com.nextplugins.economy.dao.repository.adapter;

import com.henryfabio.sqlprovider.executor.adapter.SQLResultAdapter;
import com.henryfabio.sqlprovider.executor.result.SimpleResultSet;
import com.nextplugins.economy.api.model.account.Account;
import com.nextplugins.economy.api.model.account.historic.AccountBankHistoric;
import com.nextplugins.economy.util.LinkedListHelper;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.util.DiscordUtil;
import lombok.val;
import org.bukkit.Bukkit;

public final class AccountAdapter implements SQLResultAdapter<Account> {

    private static final LinkedListHelper<AccountBankHistoric> PARSER = new LinkedListHelper<>();

    @Override
    public Account adaptResult(SimpleResultSet resultSet) {

        String accountOwner = resultSet.get("owner");
        String transactions = resultSet.get("transactions");

        double accountBalance = resultSet.get("balance");

        double movimentedBalance = resultSet.get("movimentedBalance");
        int transactionsQuantity = resultSet.get("transactionsQuantity");

        int receiveCoins = resultSet.get("receiveCoins");

        val accountLinkManager = DiscordSRV.getPlugin().getAccountLinkManager();
        val discordId = accountLinkManager.getDiscordId(Bukkit.getOfflinePlayer(accountOwner).getUniqueId());
        val user = discordId == null ? null : DiscordUtil.getJda().getUserById(discordId);

        return Account.generate()
                .username(accountOwner)
                .balance(accountBalance)
                .receiveCoins(receiveCoins != 0)
                .movimentedBalance(movimentedBalance)
                .transactionsQuantity(transactionsQuantity)
                .transactions(PARSER.fromJson(transactions))
                .discordName(user == null ? "Não vinculado" : user.getAsTag())
                .result();

    }

}
