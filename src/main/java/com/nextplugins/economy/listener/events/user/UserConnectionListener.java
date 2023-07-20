package com.nextplugins.economy.listener.events.user;

import com.nextplugins.economy.model.account.Account;
import com.nextplugins.economy.model.account.storage.AccountStorage;
import lombok.AllArgsConstructor;
import lombok.val;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@AllArgsConstructor
public class UserConnectionListener implements Listener {

    private final AccountStorage accountStorage;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        val account = accountStorage.findAccount(event.getPlayer());
        if (account.getUuid() != null && !account.getUsername().equalsIgnoreCase(event.getPlayer().getName())) {
            account.setUsername(event.getPlayer().getName());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Account account = accountStorage.findAccountByName(event.getPlayer().getName());
        if (account != null) {
            accountStorage.getAccounts().remove(account.getUuid());
            accountStorage.saveOne(account);
        }
    }

}
