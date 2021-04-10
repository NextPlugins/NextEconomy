package com.nextplugins.economy.task;

import com.google.common.collect.Lists;
import com.nextplugins.economy.api.event.operations.MoneyTopPlayerUpdateEvent;
import com.nextplugins.economy.api.model.account.Account;
import com.nextplugins.economy.configuration.values.RankingValue;
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

        List<Account> accounts = Lists.newLinkedList(accountDAO.selectAll(
                "ORDER BY balance DESC LIMIT " + RankingValue.get(RankingValue::rankingLimit
        )));

        List<Account> accountsMovimentation = Lists.newLinkedList(accountDAO.selectAll(
                "ORDER BY movimentedBalance DESC LIMIT " + RankingValue.get(RankingValue::rankingLimit
        )));

        if (!accounts.isEmpty()) {

            Account lastAccount = null;
            if (!rankingStorage.getRankByCoin().isEmpty()) {

                lastAccount = rankingStorage.getRankByCoin().get(0);
                rankingStorage.getRankByCoin().clear();

            }

            accounts.forEach(rankingStorage.getRankByCoin()::add);

            if (lastAccount != null) {

                Account topAccount = rankingStorage.getRankByCoin().get(0);
                if (lastAccount.getUserName().equals(topAccount.getUserName())) return;

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

        if (!accountsMovimentation.isEmpty()) {

            rankingStorage.getRankByMovimentation().clear();
            accountsMovimentation.forEach(rankingStorage.getRankByMovimentation()::add);

        }

    }

}
