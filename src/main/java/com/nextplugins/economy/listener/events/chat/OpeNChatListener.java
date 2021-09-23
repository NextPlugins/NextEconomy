package com.nextplugins.economy.listener.events.chat;

import com.nextplugins.economy.api.model.interactions.registry.InteractionRegistry;
import com.nextplugins.economy.ranking.storage.RankingStorage;
import com.nextplugins.economy.util.ColorUtil;
import com.nickuc.chat.api.events.PublicMessageEvent;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

@RequiredArgsConstructor
public final class OpeNChatListener implements Listener {

    private final RankingStorage rankingStorage;
    private final InteractionRegistry interactionRegistry;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(PublicMessageEvent event) {
        if (event.isCancelled()) return;

        val player = event.getSender();
        if (interactionRegistry.getOperation().contains(player.getName())) {

            interactionRegistry.getOperation().remove(player.getName());

            event.setCancelled(true);
            return;

        }

        val textComponent = new TextComponent(rankingStorage.getTycoonTag(player.getName()));
        val hoverEvent = new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                TextComponent.fromLegacyText(ColorUtil.colored("&7Magnata do servidor."))
        );

        textComponent.setHoverEvent(hoverEvent);

        event.setTag("tycoon", textComponent);
    }

}
