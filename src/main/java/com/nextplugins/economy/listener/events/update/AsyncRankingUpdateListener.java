package com.nextplugins.economy.listener.events.update;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.event.operations.AsyncMoneyTopPlayerChangedEvent;
import com.nextplugins.economy.api.event.operations.AsyncRankingUpdateEvent;
import com.nextplugins.economy.api.group.GroupWrapperManager;
import com.nextplugins.economy.api.model.account.SimpleAccount;
import com.nextplugins.economy.configuration.RankingValue;
import com.nextplugins.economy.dao.repository.AccountRepository;
import com.nextplugins.economy.ranking.CustomRankingRegistry;
import com.nextplugins.economy.ranking.storage.RankingStorage;
import com.nextplugins.economy.ranking.util.RankingChatBody;
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
    private final RankingChatBody rankingChatBody;
    private final GroupWrapperManager groupManager;

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onRankingUpdate(AsyncRankingUpdateEvent event) {
        val pluginInstance = NextEconomy.getInstance();
        val loadTime = Stopwatch.createStarted();
        val pluginManager = Bukkit.getPluginManager();

        val rankingType = RankingValue.get(RankingValue::rankingType);
        val tycoonTag = RankingValue.get(RankingValue::tycoonTagValue);

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

            int position = 1;
            for (SimpleAccount account : accounts) {
                rankingStorage.getRankByCoin().put(account.getUsername(), account);

                if (rankingType.equals("CHAT")) {
                    val body = RankingValue.get(RankingValue::chatModelBody);
                    val group = groupManager.getGroup(account.getUsername());

                    rankingChatBody.getBodyLines().set(position, body
                            .replace("$position", String.valueOf(position))
                            .replace("$prefix", group.getPrefix())
                            .replace("$suffix", group.getSuffix())
                            .replace("$player", account.getUsername())
                            .replace("$tycoon", position == 1 ? tycoonTag : "")
                            .replace("$amount", account.getBalanceFormated())
                    );
                }

                position++;
            }

            rankingStorage.getRankByMovimentation().addAll(accountsMovimentation);

            if (lastAccount != null) {

                val topAccount = rankingStorage.getTopPlayer(false);
                if (!lastAccount.getUsername().equals(topAccount.getUsername())) pluginManager.callEvent(new AsyncMoneyTopPlayerChangedEvent(lastAccount, topAccount));

            }

        } else {
            pluginInstance.getLogger().info("[Ranking] Não tem nenhum jogador no ranking");
        }

        pluginInstance.getLogger().log(Level.INFO, "[Ranking] Atualização do ranking feita com sucesso. ({0})", loadTime);

        val instance = CustomRankingRegistry.getInstance();
        if (!instance.isEnabled()) return;

        val visualTime = Stopwatch.createStarted();
        pluginInstance.getLogger().info("[Ranking] Iniciando atualização de ranking visual");

        // Leave from async. Entities can't be spawned in async.
        Bukkit.getScheduler().runTaskLater(pluginInstance, instance.getRunnable(), 20L);

        pluginInstance.getLogger().log(Level.INFO, "[Ranking] Atualização de ranking visual finalizada. ({0})", visualTime);
    }

}
