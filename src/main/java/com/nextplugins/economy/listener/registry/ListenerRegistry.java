package com.nextplugins.economy.listener.registry;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.configuration.values.RankingValue;
import com.nextplugins.economy.listener.UserConnectionListener;
import com.nextplugins.economy.listener.chat.TycoonTagRegister;
import com.nextplugins.economy.listener.operation.MoneyGiveListener;
import com.nextplugins.economy.listener.operation.MoneySetListener;
import com.nextplugins.economy.listener.operation.MoneyWithdrawListener;
import com.nextplugins.economy.listener.transaction.TransactionRequestListener;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

import java.util.logging.Logger;

@Data(staticConstructor = "of")
public final class ListenerRegistry {

    private final NextEconomy plugin;

    public void register() {
        Logger logger = plugin.getLogger();
        try {
            PluginManager pluginManager = Bukkit.getPluginManager();

            pluginManager.registerEvents(
                    new UserConnectionListener(plugin.getAccountStorage()),
                    plugin
            );

            // operations

            pluginManager.registerEvents(
                    new MoneyGiveListener(),
                    plugin
            );
            pluginManager.registerEvents(
                    new MoneySetListener(),
                    plugin
            );
            pluginManager.registerEvents(
                    new MoneyWithdrawListener(),
                    plugin
            );

            // transactions

            pluginManager.registerEvents(
                    new TransactionRequestListener(),
                    plugin
            );

            // tycoon tag

            if (RankingValue.get(RankingValue::useTycoonTag)) {
                pluginManager.registerEvents(
                        new TycoonTagRegister(),
                        plugin
                );
            }

            logger.info("Listeners registrados com sucesso!");
        } catch (Throwable t) {
            t.printStackTrace();
            logger.severe("Não foi possível registrar os listeners.");
        }
    }

}
