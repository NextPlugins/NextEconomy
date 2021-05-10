package com.nextplugins.economy.listener.events.chat;

import br.net.fabiozumbi12.UltimateChat.Bukkit.API.SendChannelMessageEvent;
import com.nextplugins.economy.api.model.account.Account;
import com.nextplugins.economy.configuration.RankingValue;
import com.nextplugins.economy.listener.events.interactions.registry.InteractionRegistry;
import com.nextplugins.economy.ranking.storage.RankingStorage;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
@RequiredArgsConstructor
public final class UltimateChatListener implements Listener {

    private final RankingStorage rankingStorage;
    private final InteractionRegistry interactionRegistry;

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerChat(SendChannelMessageEvent event) {

        if (event.isCancelled()) return;

        val player = event.getSender();

        val users = interactionRegistry.getPayInteractionManager().getPlayers().keySet();
        users.addAll(interactionRegistry.getLookupInteractionManager().getUsersInOperation());

        if (!users.contains(player.getName())) {

            if (rankingStorage.getRankByCoin().isEmpty()) return;

            Account tycoonAccount = rankingStorage.getRankByCoin().get(0);
            val tycoonTag = player.getName().equalsIgnoreCase(tycoonAccount.getUserName())
                    ? RankingValue.get(RankingValue::tycoonTagValue)
                    : RankingValue.get(RankingValue::tycoonRichTagValue);

            event.addTag("tycoon", tycoonTag);
            return;

        }

        event.setCancelled(true);

    }

}
