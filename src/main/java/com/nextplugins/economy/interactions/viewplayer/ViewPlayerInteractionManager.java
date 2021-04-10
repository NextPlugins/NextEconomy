package com.nextplugins.economy.interactions.viewplayer;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.configuration.values.MessageValue;
import com.nextplugins.economy.util.EventAwaiter;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.concurrent.TimeUnit;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class ViewPlayerInteractionManager {

    public void sendRequisition(Player player) {

        EventAwaiter.newAwaiter(AsyncPlayerChatEvent.class, NextEconomy.getInstance())
                .expiringAfter(1, TimeUnit.MINUTES)
                .withTimeOutAction(() -> player.sendMessage(MessageValue.get(MessageValue::noTime)))
                .filter(event -> event.getPlayer().getName().equals(player.getName()))
                .thenAccept(event -> {

                    event.setCancelled(true);

                    String message = event.getMessage();
                    if (message.contains(" ")) message = message.split(" ")[0];

                    event.getPlayer().performCommand("money " + message);

                })
                .await(true);

    }

}
