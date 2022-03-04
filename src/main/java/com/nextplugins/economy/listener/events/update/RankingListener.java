package com.nextplugins.economy.listener.events.update;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.event.operations.AsyncMoneyTopPlayerChangedEvent;
import com.nextplugins.economy.api.event.operations.AsyncRankingUpdateEvent;
import com.nextplugins.economy.api.group.Group;
import com.nextplugins.economy.api.group.GroupWrapperManager;
import com.nextplugins.economy.configuration.DiscordValue;
import com.nextplugins.economy.configuration.RankingValue;
import com.nextplugins.economy.dao.repository.AccountRepository;
import com.nextplugins.economy.model.account.SimpleAccount;
import com.nextplugins.economy.ranking.CustomRankingRegistry;
import com.nextplugins.economy.ranking.storage.RankingStorage;
import com.nextplugins.economy.ranking.util.RankingChatBody;
import com.nextplugins.economy.util.ColorUtil;
import com.nextplugins.economy.util.DiscordUtil;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.LinkedList;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@RequiredArgsConstructor
public class RankingListener implements Listener {

    private final AccountRepository accountRepository;
    private final RankingStorage rankingStorage;
    private final RankingChatBody rankingChatBody;
    private final GroupWrapperManager groupManager;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRankingUpdate(AsyncRankingUpdateEvent event) {
        if (event.isCancelled()) return;

        Bukkit.getScheduler().runTaskAsynchronously(NextEconomy.getInstance(), () -> {
            val pluginInstance = NextEconomy.getInstance();
            val pluginManager = Bukkit.getPluginManager();

            SimpleAccount lastAccount = null;
            if (!rankingStorage.getRankByCoin().isEmpty()) {
                lastAccount = rankingStorage.getTopPlayer(false);
            }

            NextEconomy.getInstance().getAccountStorage().flushData();

            rankingStorage.getRankByCoin().clear();
            rankingStorage.getRankByMovimentation().clear();

            rankingStorage.getRankByMovimentation().addAll(accountRepository.selectSimpleAll(
                    "ORDER BY movimentedBalance DESC LIMIT " + RankingValue.get(RankingValue::rankingLimit)
            ));

            val accounts = accountRepository.selectSimpleAll(
                    "ORDER BY balance DESC LIMIT " + RankingValue.get(RankingValue::rankingLimit)
            );

            val discordEnabled = DiscordUtil.isEnabled();
            if (discordEnabled) {
                DiscordUtil.removeDiscordRoles(DiscordUtil.getGuild());
            }

            if (!accounts.isEmpty()) {
                val rankingType = RankingValue.get(RankingValue::rankingType);
                val tycoonTag = RankingValue.get(RankingValue::tycoonTagValue);
                val chatRanking = rankingType.equals("CHAT");

                val bodyLines = new LinkedList<String>();
                val stringBuilder = DiscordValue.get(DiscordValue::enable) ? new StringBuilder() : null;
                int position = 1;
                for (val account : accounts) {
                    if (position == 1) rankingStorage.setTopPlayer(account.getUsername());
                    rankingStorage.getRankByCoin().put(account.getUsername(), account);

                    Group group = chatRanking || stringBuilder != null ? groupManager.getGroup(account.getUsername()) : null;
                    if (chatRanking) {
                        val body = RankingValue.get(RankingValue::chatModelBody);
                        bodyLines.add(body
                                .replace("$position", String.valueOf(position))
                                .replace("$prefix", group.getPrefix())
                                .replace("$suffix", group.getSuffix())
                                .replace("$player", account.getUsername())
                                .replace("$tycoon", position == 1 ? tycoonTag : "")
                                .replace("$amount", account.getBalanceFormated()));
                    }

                    if (stringBuilder != null) {
                        if (position == 1) stringBuilder.append(DiscordValue.get(DiscordValue::topEmoji));

                        val line = DiscordValue.get(DiscordValue::topLine);
                        stringBuilder.append(line
                                .replace("$position", String.valueOf(position))
                                .replace("$username", account.getUsername())
                                .replace("$prefix", group.getPrefix())
                                .replace("$suffix", group.getSuffix())
                                .replace("$amount", account.getBalanceFormated())
                        ).append("\n");
                    }

                    if (discordEnabled) {
                        DiscordUtil.addDiscordRole(account, position, DiscordUtil.getGuild());
                    }

                    position++;
                }

                rankingChatBody.setMinecraftBodyLines(bodyLines.toArray(new String[]{}));
                rankingChatBody.setDiscordBodyLines(stringBuilder == null ? "" : stringBuilder.toString());

                if (lastAccount != null) {

                    val topAccount = rankingStorage.getTopPlayer();
                    if (!lastAccount.getUsername().equals(topAccount))
                        pluginManager.callEvent(new AsyncMoneyTopPlayerChangedEvent(
                                lastAccount,
                                rankingStorage.getTopPlayer(false))
                        );

                }

            } else {
                rankingChatBody.setMinecraftBodyLines(new String[]{ColorUtil.colored(
                        "  &cNenhum jogador está no ranking!"
                )});

                rankingChatBody.setDiscordBodyLines(":x: Nenhum jogador está no ranking!");
            }

            val instance = CustomRankingRegistry.getInstance();
            if (!instance.isEnabled()) return;

            Bukkit.getScheduler().runTaskLater(pluginInstance, instance.getRunnable(), 20L);
        });
    }

}
