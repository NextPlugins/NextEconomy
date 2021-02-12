package com.nextplugins.economy.listener;

import com.nextplugins.economy.storage.AccountStorage;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public final class UserDisconnectListener implements Listener {

    private final AccountStorage accountStorage;

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        accountStorage.purge(player.getUniqueId());
    }

}
