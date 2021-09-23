package com.nextplugins.economy.listener.events.chat;

import com.nextplugins.economy.api.model.interactions.registry.InteractionRegistry;
import lombok.AllArgsConstructor;
import lombok.val;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@AllArgsConstructor
public class DefaultChatListener implements Listener {

    private final InteractionRegistry interactionRegistry;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) return;

        val player = event.getPlayer();
        if (!interactionRegistry.getOperation().contains(player.getName())) return;

        interactionRegistry.getOperation().remove(player.getName());
        event.setCancelled(true);
    }

}
