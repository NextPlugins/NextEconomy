package com.nextplugins.economy.listener.events.discord;

import com.nextplugins.economy.NextEconomy;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.ListenerPriority;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordGuildMessageReceivedEvent;
import github.scarsz.discordsrv.objects.managers.AccountLinkManager;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DiscordListener implements Listener {

    private final AccountLinkManager manager;
    private final NextEconomy plugin;

    public static void bind(NextEconomy plugin) {

        val discordListener = new DiscordListener(DiscordSRV.getPlugin().getAccountLinkManager(), plugin);
        DiscordSRV.api.subscribe(discordListener);

    }

    @Subscribe(priority = ListenerPriority.MONITOR)
    public void discordMessage(DiscordGuildMessageReceivedEvent event) {

        if (event.get)

        val offlinePlayer = Bukkit.getOfflinePlayer(manager.getUuid(event.getAuthor().getId()));


    }

}
