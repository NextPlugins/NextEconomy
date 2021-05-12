package com.nextplugins.economy.listener.events.chat;

import br.com.devpaulo.legendchat.api.events.ChatMessageEvent;
import com.nextplugins.economy.configuration.RankingValue;
import com.nextplugins.economy.api.model.interactions.registry.InteractionRegistry;
import com.nextplugins.economy.ranking.storage.RankingStorage;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

@RequiredArgsConstructor
public final class LegendChatListener implements Listener {

    private final RankingStorage rankingStorage;
    private final InteractionRegistry interactionRegistry;

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerChat(ChatMessageEvent event) {

        if (event.isCancelled()) return;

        val player = event.getSender();

        val users = interactionRegistry.getPayInteractionManager().getPlayers().keySet();
        users.addAll(interactionRegistry.getLookupInteractionManager().getUsersInOperation());

        if (users.contains(player.getName())) {

            event.setCancelled(true);
            return;

        }

        if (rankingStorage.getRankByCoin().isEmpty()) return;

        val tycoonAccount = rankingStorage.getRankByCoin().get(0);
        val tycoonTag = player.getName().equalsIgnoreCase(tycoonAccount.getUserName())
                ? RankingValue.get(RankingValue::tycoonTagValue)
                : RankingValue.get(RankingValue::tycoonRichTagValue);

        event.setTagValue("tycoon", tycoonTag);


    }

}
