package com.nextplugins.economy.task;

import com.nextplugins.economy.api.event.operations.EconomyRankingUpdateEvent;
import com.nextplugins.economy.api.model.Account;
import com.nextplugins.economy.dao.AccountDAO;
import com.nextplugins.economy.storage.RankingStorage;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;

import java.time.Instant;
import java.util.Set;

@RequiredArgsConstructor
public final class AccountRankingTask implements Runnable {

    private final AccountDAO accountDAO;
    private final RankingStorage rankingStorage;

    @Override
    public void run() {

        Set<Account> accounts = accountDAO.selectAll("ORDER BY balance DESC LIMIT 10");

        if (!accounts.isEmpty()) {

            rankingStorage.getRankingAccounts().clear();
            accounts.forEach(rankingStorage::addAccount);

            EconomyRankingUpdateEvent rankingUpdateEvent = new EconomyRankingUpdateEvent(accounts, Instant.now(), true);
            Bukkit.getPluginManager().callEvent(rankingUpdateEvent);

        }

    }

}
