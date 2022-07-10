package com.nextplugins.economy.listener.events.chat;

import br.net.fabiozumbi12.UltimateChat.Bukkit.API.SendChannelMessageEvent;
import com.nextplugins.economy.model.interactions.registry.InteractionRegistry;
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
        if (interactionRegistry.getOperation().contains(player.getName())) {

            interactionRegistry.getOperation().remove(player.getName());

            event.setCancelled(true);
            return;

        }

        event.addTag("tycoon", rankingStorage.getTycoonTag(player.getName()));
    }

}
