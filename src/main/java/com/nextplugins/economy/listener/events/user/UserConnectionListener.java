package com.nextplugins.economy.listener.events.user;

import com.nextplugins.economy.api.model.account.storage.AccountStorage;
import lombok.AllArgsConstructor;
import lombok.val;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
@AllArgsConstructor
public class UserConnectionListener implements Listener {

    private final AccountStorage accountStorage;

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

        val future = accountStorage.getCache().getIfPresent(event.getPlayer().getName());
        if (future == null) return;

        val account = future.join();
        accountStorage.saveOne(account);

        accountStorage.getCache().synchronous().invalidate(account);

    }

}
