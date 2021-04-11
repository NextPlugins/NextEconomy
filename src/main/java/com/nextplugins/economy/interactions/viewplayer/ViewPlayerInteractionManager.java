package com.nextplugins.economy.interactions.viewplayer;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.configuration.values.MessageValue;
import com.nextplugins.economy.util.EventAwaiter;
import lombok.val;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class ViewPlayerInteractionManager {

    private Consumer<AsyncPlayerChatEvent> consumer;

    public void sendRequisition(Player player) {

        EventAwaiter.newAwaiter(AsyncPlayerChatEvent.class, NextEconomy.getInstance())
                .expiringAfter(1, TimeUnit.MINUTES)
                .withTimeOutAction(() -> player.sendMessage(MessageValue.get(MessageValue::noTime)))
                .filter(event -> event.getPlayer().getName().equals(player.getName()))
                .thenAccept(consumer)
                .await(false);

    }

    public ViewPlayerInteractionManager init() {

        consumer = event -> {

            event.setCancelled(true);

            val player = event.getPlayer();

            String message = event.getMessage();
            if (message.equalsIgnoreCase("cancelar")) {

                player.sendMessage(MessageValue.get(MessageValue::interactionCancelled));
                return;

            }

            if (message.contains(" ")) message = message.split(" ")[0];

            player.performCommand("money " + message);

        };

        return this;

    }

}
