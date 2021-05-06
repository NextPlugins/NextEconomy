package com.nextplugins.economy.listener.events.update;

import com.google.common.collect.Lists;
import com.nextplugins.economy.api.event.operations.AsyncRankingUpdateEvent;
import com.nextplugins.economy.api.event.operations.MoneyTopPlayerUpdateEvent;
import com.nextplugins.economy.api.model.account.Account;
import com.nextplugins.economy.configuration.RankingValue;
import com.nextplugins.economy.dao.repository.AccountRepository;
import com.nextplugins.economy.ranking.storage.RankingStorage;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.time.Instant;
import java.util.List;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@RequiredArgsConstructor
public class RankingUpdateListener implements Listener {

    private final AccountRepository accountRepository;
    private final RankingStorage rankingStorage;

    @EventHandler
    public void onRankingUpdate(AsyncRankingUpdateEvent event) {

        if (event.isCancelled()) return;

        val pluginManager = Bukkit.getPluginManager();

        List<Account> accounts = Lists.newLinkedList(accountRepository.selectAll(
                "ORDER BY balance DESC LIMIT " + RankingValue.get(RankingValue::rankingLimit)
        ));

        List<Account> accountsMovimentation = Lists.newLinkedList(accountRepository.selectAll(
                "ORDER BY movimentedBalance DESC LIMIT " + RankingValue.get(RankingValue::rankingLimit)
        ));

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

                pluginManager.callEvent(
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

            List<Account> accounts1 = rankingStorage.getRankByMovimentation();
            accounts1.addAll(accountsMovimentation);
        }

    }

}
