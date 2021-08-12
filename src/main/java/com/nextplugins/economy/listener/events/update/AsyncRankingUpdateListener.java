package com.nextplugins.economy.listener.events.update;

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
                lastAccount = rankingStorage.getRankByCoin().get(0);
            }

            rankingStorage.getRankByCoin().clear();
            rankingStorage.getRankByMovimentation().clear();

            rankingStorage.getRankByCoin().addAll(accounts);
            rankingStorage.getRankByMovimentation().addAll(accountsMovimentation);

            if (lastAccount != null) {

                val topAccount = rankingStorage.getRankByCoin().get(0);
                if (!lastAccount.getUsername().equals(topAccount.getUsername())) pluginManager.callEvent(new AsyncMoneyTopPlayerChangedEvent(lastAccount, topAccount));

            }

        } else {
            NextEconomy.getInstance().getLogger().info("[Ranking] Não tem nenhum jogador no ranking");
            return;
        }

        NextEconomy.getInstance().getLogger().info("[Ranking] Atualização do ranking feita com sucesso");

        val instance = CustomRankingRegistry.getInstance();
        if (!instance.isEnabled()) return;

        NextEconomy.getInstance().getLogger().info("[Ranking] Iniciando atualização de ranking visual");

        // Leave from async. Entities can't be spawned in async.
        Bukkit.getScheduler().runTaskLater(NextEconomy.getInstance(), instance.getRunnable(), 20L);

        NextEconomy.getInstance().getLogger().info("[Ranking] Atualização de ranking visual finalizada");

    }

}
