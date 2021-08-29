package com.nextplugins.economy.listener.events.update;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.event.operations.AsyncRankingUpdateEvent;
import com.nextplugins.economy.api.event.operations.AsyncMoneyTopPlayerChangedEvent;
import com.nextplugins.economy.api.model.account.SimpleAccount;
import com.nextplugins.economy.configuration.RankingValue;
import com.nextplugins.economy.dao.repository.AccountRepository;
import com.nextplugins.economy.ranking.CustomRankingRegistry;
import com.nextplugins.economy.ranking.storage.RankingStorage;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.logging.Level;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@RequiredArgsConstructor
public class AsyncRankingUpdateListener implements Listener {

    private final AccountRepository accountRepository;
    private final RankingStorage rankingStorage;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRankingUpdate(AsyncRankingUpdateEvent event) {

        if (event.isCancelled()) return;

        val loadTime = Stopwatch.createStarted();
        val pluginManager = Bukkit.getPluginManager();

        val accounts = Lists.newLinkedList(accountRepository.selectSimpleAll(
                "ORDER BY balance DESC LIMIT " + RankingValue.get(RankingValue::rankingLimit)
        ));

        val accountsMovimentation = Lists.newLinkedList(accountRepository.selectSimpleAll(
                "ORDER BY movimentedBalance DESC LIMIT " + RankingValue.get(RankingValue::rankingLimit)
        ));

        if (!accounts.isEmpty()) {

            SimpleAccount lastAccount = null;
            if (!rankingStorage.getRankByCoin().isEmpty()) {
                lastAccount = rankingStorage.getTopPlayer(false);
            }

            rankingStorage.getRankByCoin().clear();
            rankingStorage.getRankByMovimentation().clear();

            for (SimpleAccount account : accounts) {
                rankingStorage.getRankByCoin().put(account.getUsername(), account);
            }

            rankingStorage.getRankByMovimentation().addAll(accountsMovimentation);

            if (lastAccount != null) {

                val topAccount = rankingStorage.getTopPlayer(false);
                if (!lastAccount.getUsername().equals(topAccount.getUsername())) pluginManager.callEvent(new AsyncMoneyTopPlayerChangedEvent(lastAccount, topAccount));

            }

        } else {
            NextEconomy.getInstance().getLogger().info("[Ranking] Não tem nenhum jogador no ranking");
        }

        NextEconomy.getInstance().getLogger().log(Level.INFO, "[Ranking] Atualização do ranking feita com sucesso. ({0})", loadTime);

        val instance = CustomRankingRegistry.getInstance();
        if (!instance.isEnabled()) return;

        val visualTime = Stopwatch.createStarted();
        NextEconomy.getInstance().getLogger().info("[Ranking] Iniciando atualização de ranking visual");

        // Leave from async. Entities can't be spawned in async.
        Bukkit.getScheduler().runTaskLater(NextEconomy.getInstance(), instance.getRunnable(), 20L);

        NextEconomy.getInstance().getLogger().log(Level.INFO, "[Ranking] Atualização de ranking visual finalizada. ({0})", visualTime);

    }

}
