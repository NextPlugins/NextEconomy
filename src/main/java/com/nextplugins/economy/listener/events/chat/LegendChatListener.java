package com.nextplugins.economy.listener.events.chat;

import br.com.devpaulo.legendchat.api.events.ChatMessageEvent;
import com.nextplugins.economy.registry.InteractionRegistry;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

@RequiredArgsConstructor
public final class LegendChatListener implements Listener {

    private final InteractionRegistry interactionRegistry;

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerChat(ChatMessageEvent event) {

        if (event.isCancelled()) return;

        val users = interactionRegistry.getSendMoneyInteractionManager().getPlayers().keySet();
        users.addAll(interactionRegistry.getViewPlayerInteractionManager().getUsersInOperation());

        if (!users.contains(event.getSender().getName())) return;

        event.setCancelled(true);

    }

}
