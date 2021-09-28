package com.nextplugins.economy.listener.events.user;

import com.nextplugins.economy.api.model.account.storage.AccountStorage;
import lombok.AllArgsConstructor;
import lombok.val;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.concurrent.CompletableFuture;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
@AllArgsConstructor
public class UserConnectionListener implements Listener {

    private final AccountStorage accountStorage;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        val cache = accountStorage.getCache().synchronous();
        val account = cache.getIfPresent(event.getPlayer().getName());
        if (account == null) return;

        cache.invalidate(event.getPlayer().getName());
        cache.cleanUp();

        accountStorage.findAccount(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        val cache = accountStorage.getCache().synchronous();
        val account = cache.getIfPresent(event.getPlayer().getName());
        if (account == null) return;

        accountStorage.getAccountRepository().updateOne(account);
        cache.invalidate(event.getPlayer().getName());
        cache.cleanUp();
    }

}
