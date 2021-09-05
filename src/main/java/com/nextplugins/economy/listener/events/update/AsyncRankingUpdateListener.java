package com.nextplugins.economy.listener.events.update;

import com.google.common.base.Stopwatch;
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
import com.nextplugins.economy.util.ColorUtil;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.LinkedList;
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

        NextEconomy.getInstance().getAccountStorage().getCache().synchronous().invalidateAll();

        SimpleAccount lastAccount = null;
        if (!rankingStorage.getRankByCoin().isEmpty()) {
            lastAccount = rankingStorage.getTopPlayer(false);
        }

        rankingStorage.getRankByCoin().clear();
        rankingStorage.getRankByMovimentation().clear();

        rankingStorage.getRankByMovimentation().addAll(accountRepository.selectSimpleAll(
                "ORDER BY movimentedBalance DESC LIMIT " + RankingValue.get(RankingValue::rankingLimit)
        ));

        val accounts = accountRepository.selectSimpleAll(
                "ORDER BY balance DESC LIMIT " + RankingValue.get(RankingValue::rankingLimit)
        );

        if (!accounts.isEmpty()) {
            val rankingType = RankingValue.get(RankingValue::rankingType);
            val tycoonTag = RankingValue.get(RankingValue::tycoonTagValue);
            val chatRanking = rankingType.equals("CHAT");

            val bodyLines = new LinkedList<String>();
            int position = 1;
            for (SimpleAccount account : accounts) {
                if (position == 1) rankingStorage.setTopPlayer(account.getUsername());
                rankingStorage.getRankByCoin().put(account.getUsername(), account);

                if (chatRanking) {
                    val body = RankingValue.get(RankingValue::chatModelBody);
                    val group = groupManager.getGroup(account.getUsername());
                    bodyLines.add(body
                            .replace("$position", String.valueOf(position))
                            .replace("$prefix", group.getPrefix())
                            .replace("$suffix", group.getSuffix())
                            .replace("$player", account.getUsername())
                            .replace("$tycoon", position == 1 ? tycoonTag : "")
                            .replace("$amount", account.getBalanceFormated()));
                }

                position++;
            }
            rankingChatBody.setBodyLines(bodyLines.toArray(new String[]{}));

            if (lastAccount != null) {

                val topAccount = rankingStorage.getTopPlayer();
                if (!lastAccount.getUsername().equals(topAccount))
                    pluginManager.callEvent(new AsyncMoneyTopPlayerChangedEvent(
                            lastAccount,
                            rankingStorage.getTopPlayer(false))
                    );

            }

        } else {
            rankingChatBody.setBodyLines(new String[]{ColorUtil.colored(
                    "  &cNenhum jogador está no ranking!"
            )});
            pluginInstance.getLogger().info("[Ranking] Não tem nenhum jogador no ranking");
        }

        pluginInstance.getLogger().log(Level.INFO, "[Ranking] Atualização do ranking feita com sucesso. ({0})", loadTime);

        val instance = CustomRankingRegistry.getInstance();
        if (!instance.isEnabled()) return;

        // Leave from async. Entities can't be spawned in async.
        Bukkit.getScheduler().runTaskLater(pluginInstance, instance.getRunnable(), 20L);

    }

}
