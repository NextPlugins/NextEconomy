package com.nextplugins.economy.listener.events.user;

import com.nextplugins.economy.api.model.account.storage.AccountStorage;
import lombok.AllArgsConstructor;
import lombok.val;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@AllArgsConstructor
public class UpdateNickListener implements Listener {

    private final AccountStorage accountStorage;

    // Update player nickname

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        val account = accountStorage.findAccount(event.getPlayer());
        if (account.getUuid() != null && !account.getUsername().equalsIgnoreCase(event.getPlayer().getName())) {
            account.setUsername(event.getPlayer().getName());
        }
    }

}
