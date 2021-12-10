package com.nextplugins.economy.listener.events.update;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.event.operations.AsyncMoneyTopPlayerChangedEvent;
import com.nextplugins.economy.api.event.operations.AsyncRankingUpdateEvent;
import com.nextplugins.economy.api.group.Group;
import com.nextplugins.economy.api.group.GroupWrapperManager;
import com.nextplugins.economy.api.model.account.SimpleAccount;
import com.nextplugins.economy.configuration.DiscordValue;
import com.nextplugins.economy.configuration.RankingValue;
import com.nextplugins.economy.dao.repository.AccountRepository;
import com.nextplugins.economy.ranking.CustomRankingRegistry;
import com.nextplugins.economy.ranking.storage.RankingStorage;
import com.nextplugins.economy.ranking.util.RankingChatBody;
import com.nextplugins.economy.util.ColorUtil;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Arrays;
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

        val pluginInstance = NextEconomy.getInstance();
        val pluginManager = Bukkit.getPluginManager();

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

        val discordEnabled = NextEconomy.getInstance().getDiscordCommandRegistry().isEnabled();
        if (discordEnabled) {
            val discordSRV = DiscordSRV.getPlugin();
            val jda = discordSRV.getJda();
            val guild = jda.getGuilds().get(0);

            removeDiscordRoles(guild);
        }

        if (!accounts.isEmpty()) {
            val rankingType = RankingValue.get(RankingValue::rankingType);
            val tycoonTag = RankingValue.get(RankingValue::tycoonTagValue);
            val chatRanking = rankingType.equals("CHAT");

            val bodyLines = new LinkedList<String>();
            val stringBuilder = DiscordValue.get(DiscordValue::enable) ? new StringBuilder() : null;
            int position = 1;
            for (SimpleAccount account : accounts) {
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
                    val discordSRV = DiscordSRV.getPlugin();
                    val jda = discordSRV.getJda();
                    val guild = jda.getGuilds().get(0);

                    addDiscordRole(account, position, guild);
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
    }

    private void addDiscordRole(SimpleAccount account, int position, Guild guild) {
        val roleID = position == 1
                ? RankingValue.get(RankingValue::tycoonRoleId)
                : RankingValue.get(RankingValue::tycoonRichRoleId);

        val role = guild.getRoleById(roleID);
        if (role != null) {
            val uuid = Bukkit.getOfflinePlayer(account.getUsername()).getUniqueId();
            val discordId = DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(uuid);

            guild.addRoleToMember(discordId, role).queue();
        }
    }

    private void removeDiscordRoles(Guild guild) {
        val roles = Arrays.asList(RankingValue.get(RankingValue::tycoonRoleId), RankingValue.get(RankingValue::tycoonRichRoleId));
        for (long roleID : roles) {
            if (roleID == 0) return;

            val role = guild.getRoleById(roleID);
            if (role == null) return;

            for (Member member : guild.getMembersWithRoles(role)) {
                guild.removeRoleFromMember(member, role).queue();
            }
        }
    }

}
