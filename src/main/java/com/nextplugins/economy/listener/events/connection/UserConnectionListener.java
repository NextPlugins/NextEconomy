package com.nextplugins.economy.listener.events.connection;

import com.nextplugins.economy.storage.AccountStorage;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public final class UserConnectionListener implements Listener {

    private final AccountStorage accountStorage;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        // for load player to cache
        accountStorage.findOnlineAccount(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        accountStorage.purge(event.getPlayer().getName());
    }

}
