package com.nextplugins.economy.command.discord.registry;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.command.discord.CommandHandler;
import com.nextplugins.economy.command.discord.CommandMap;
import com.nextplugins.economy.command.discord.impl.ViewMoneyCommand;
import com.nextplugins.economy.configuration.DiscordIntegrationValue;
import github.scarsz.discordsrv.DiscordSRV;
import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.util.logging.Level;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@NoArgsConstructor
public final class DiscordCommandRegistry implements Listener {

    private CommandHandler commandHandler;

    public void init() {

        if (!DiscordIntegrationValue.get(DiscordIntegrationValue::enable)) return;

        if (!Bukkit.getPluginManager().isPluginEnabled("DiscordSRV")) {

            NextEconomy.getInstance().getLogger().log(Level.WARNING,
                    "Dependência não encontrada ({0}) A integração com o discord não será usada.",
                    "DiscordSRV"
            );
            return;

        }

        val commandMap = new CommandMap(DiscordIntegrationValue.get(DiscordIntegrationValue::prefix));
        commandMap.register("money", new ViewMoneyCommand());

        commandHandler = new CommandHandler(commandMap);
        DiscordSRV.api.subscribe(commandHandler);

    }

}
