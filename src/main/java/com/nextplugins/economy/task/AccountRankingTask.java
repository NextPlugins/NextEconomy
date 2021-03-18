package com.nextplugins.economy.task;

import com.google.common.collect.Lists;
import com.nextplugins.economy.api.event.operations.MoneyTopPlayerUpdateEvent;
import com.nextplugins.economy.api.model.Account;
import com.nextplugins.economy.dao.AccountDAO;
import com.nextplugins.economy.storage.RankingStorage;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;

import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
public final class AccountRankingTask implements Runnable {

    private final AccountDAO accountDAO;
    private final RankingStorage rankingStorage;

    @Override
    public void run() {

        List<Account> accounts = Lists.newLinkedList(accountDAO.selectAll("ORDER BY balance DESC LIMIT 10"));

        if (!accounts.isEmpty()) {

            Account lastAccount = null;
            if (rankingStorage.getRankingAccounts().size() >= 1) {
                lastAccount = rankingStorage.getRankingAccounts().get(0);
                rankingStorage.getRankingAccounts().clear();
            }

            accounts.forEach(rankingStorage.getRankingAccounts()::add);

            if (lastAccount != null) {

                Account topAccount = rankingStorage.getRankingAccounts().get(0);
                if (lastAccount.getOwner().equals(topAccount.getOwner())) return;

                Bukkit.getPluginManager().callEvent(
                        MoneyTopPlayerUpdateEvent.builder()
                                .lastMoneyTop(lastAccount)
                                .moneyTop(topAccount)
                                .updateInstant(Instant.now())
                                .async(true)
                                .build()
                );
            }

        }

    }

}
