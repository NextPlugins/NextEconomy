package com.nextplugins.economy.interactions.viewplayer;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.configuration.values.MessageValue;
import com.nextplugins.economy.util.EventAwaiter;
import com.nickuc.chat.api.events.PublicMessageEvent;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class ViewPlayerInteractionManager {

    private Consumer<PublicMessageEvent> consumer;

    public void sendRequisition(Player player) {

        EventAwaiter.newAwaiter(PublicMessageEvent.class, NextEconomy.getInstance())
                .expiringAfter(1, TimeUnit.MINUTES)
                .withTimeOutAction(() -> player.sendMessage(MessageValue.get(MessageValue::noTime)))
                .filter(event -> event.getSender().getName().equals(player.getName()))
                .thenAccept(consumer)
                .await(false);

    }

    public ViewPlayerInteractionManager init() {

        consumer = event -> {

            event.setCancelled(true);

            val player = event.getSender();

            String message = event.getMessage();
            if (message.equalsIgnoreCase("cancelar")) {

                player.sendMessage(MessageValue.get(MessageValue::interactionCancelled));
                return;

            }

            Bukkit.getScheduler().runTask(NextEconomy.getInstance(), () -> player.performCommand("money " + message));

        };

        return this;

    }

}
