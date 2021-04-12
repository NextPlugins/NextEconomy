package com.nextplugins.economy.interactions.viewplayer;

import com.google.common.collect.Lists;
import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.configuration.values.MessageValue;
import com.nextplugins.economy.util.EventAwaiter;
import lombok.Getter;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class ViewPlayerInteractionManager {

    @Getter private final List<String> usersInOperation = Lists.newArrayList();
    private Consumer<AsyncPlayerChatEvent> consumer;

    public void sendRequisition(Player player) {

        EventAwaiter.newAwaiter(AsyncPlayerChatEvent.class, NextEconomy.getInstance())
                .expiringAfter(1, TimeUnit.MINUTES)
                .withTimeOutAction(() -> {

                    player.sendMessage(MessageValue.get(MessageValue::noTime));
                    usersInOperation.remove(player.getName());

                })
                .filter(event -> event.getPlayer().getName().equals(player.getName()))
                .thenAccept(consumer)
                .await(false);

    }

    public ViewPlayerInteractionManager init() {

        consumer = event -> {

            event.setCancelled(true);

            val player = event.getPlayer();
            usersInOperation.remove(player.getName());

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
