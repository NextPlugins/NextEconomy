package com.nextplugins.economy.command.discord.registry;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.command.discord.CommandHandler;
import com.nextplugins.economy.command.discord.CommandMap;
import com.nextplugins.economy.command.discord.impl.PayMoneyCommand;
import com.nextplugins.economy.command.discord.impl.TopMoneyCommand;
import com.nextplugins.economy.command.discord.impl.ViewMoneyCommand;
import com.nextplugins.economy.configuration.DiscordValue;
import github.scarsz.discordsrv.DiscordSRV;
import lombok.*;
import org.bukkit.event.Listener;

import java.util.logging.Level;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Getter
@NoArgsConstructor
public final class DiscordCommandRegistry implements Listener {

    private Object commandHandler;
    private boolean enabled;

    public void init() {

        if (!DiscordValue.get(DiscordValue::enable)) return;

        val plugin = NextEconomy.getInstance();
        if (plugin.getPayActionDiscordManager() == null) {

            plugin.getLogger().log(Level.WARNING,
                    "Dependência não encontrada ({0}) A integração com o discord não será usada.",
                    "DiscordSRV"
            );
            return;

        }

        enabled = true;

        val commandMap = new CommandMap(DiscordValue.get(DiscordValue::prefix));
        commandMap.register("money", new ViewMoneyCommand(), "coins", "vermoney", "ver", "coin");
        commandMap.register("top", new TopMoneyCommand(), "topcoins", "moneytop", "rank", "ranking");

        val payMoneyCommand = new PayMoneyCommand(plugin.getAccountStorage(), plugin.getPayActionDiscordManager());
        commandMap.register("pay", payMoneyCommand, "moneypay", "enviar", "send", "pagar");

        commandHandler = new CommandHandler(commandMap);
        DiscordSRV.api.subscribe(commandHandler);
        DiscordSRV.getPlugin().getJda().addEventListener(plugin.getPayActionDiscordManager());

        plugin.getLogger().info("A integração com o discord foi realizada com sucesso.");

    }

}
