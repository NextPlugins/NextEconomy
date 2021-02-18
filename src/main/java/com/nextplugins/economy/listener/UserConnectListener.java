package com.nextplugins.economy.listener;

import com.nextplugins.economy.storage.AccountStorage;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@RequiredArgsConstructor
public final class UserConnectListener implements Listener {

    private final AccountStorage accountStorage;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        accountStorage.getAccount(player.getUniqueId());
    }

}
