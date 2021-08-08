package com.nextplugins.economy.listener.events.chat;

import br.net.fabiozumbi12.UltimateChat.Bukkit.API.SendChannelMessageEvent;
import com.nextplugins.economy.configuration.RankingValue;
import com.nextplugins.economy.api.model.interactions.registry.InteractionRegistry;
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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(SendChannelMessageEvent event) {

        if (event.isCancelled()) return;

        val player = event.getSender();
        if (interactionRegistry.getWaitingForCancel().contains(player.getName())) {

            interactionRegistry.getWaitingForCancel().remove(player.getName());

            event.setCancelled(true);
            return;

        }

        if (rankingStorage.getRankByCoin().isEmpty()) return;

        val tycoonAccount = rankingStorage.getRankByCoin().get(0);
        val tycoonTag = player.getName().equalsIgnoreCase(tycoonAccount.getUsername())
                ? RankingValue.get(RankingValue::tycoonTagValue)
                : RankingValue.get(RankingValue::tycoonRichTagValue);

        event.addTag("tycoon", tycoonTag);

    }

}
