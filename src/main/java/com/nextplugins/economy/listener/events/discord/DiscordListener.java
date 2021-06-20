package com.nextplugins.economy.listener.events.discord;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.configuration.DiscordIntegrationValue;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.ListenerPriority;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordGuildMessageReceivedEvent;
import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.util.logging.Level;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Data(staticConstructor = "of")
public class DiscordListener implements Listener {

    private final NextEconomy plugin;

    public void register() {

        if (!DiscordIntegrationValue.get(DiscordIntegrationValue::enable)) return;

        if (!Bukkit.getPluginManager().isPluginEnabled("DiscordSRV")) {

            plugin.getLogger().log(Level.WARNING,
                    "Dependência não encontrada ({0}) A integração com o discord não será usada.",
                    "DiscordSRV"
            );
            return;

        }

        val discordListener = new DiscordListener(plugin);
        DiscordSRV.api.subscribe(discordListener);

    }

    @Subscribe(priority = ListenerPriority.MONITOR)
    public void discordMessage(DiscordGuildMessageReceivedEvent event) {


        var message = event.getMessage().getContentRaw().toLowerCase();

        // TODO fazer um sistema decente de comando!!!

        val prefix = DiscordIntegrationValue.get(DiscordIntegrationValue::prefix);
        if (!message.startsWith(prefix)) return;

        message = message.split(prefix)[1];

        val parts = message.split(" ");
        val command = parts[0];
        val args = parts[1];

        val accountLinkManager = DiscordSRV.getPlugin().getAccountLinkManager();
        val offlinePlayer = Bukkit.getOfflinePlayer(accountLinkManager.getUuid(event.getAuthor().getId()));
        if (command.equalsIgnoreCase("test")) {
            event.getMessage().reply("Ta certo cria!").queue();
        }

    }

}
