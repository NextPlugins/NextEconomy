package com.nextplugins.economy.listener;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.configuration.FeatureValue;
import com.nextplugins.economy.configuration.PurseValue;
import com.nextplugins.economy.configuration.RankingValue;
import com.nextplugins.economy.listener.events.chat.LegendChatListener;
import com.nextplugins.economy.listener.events.chat.OpeNChatListener;
import com.nextplugins.economy.listener.events.chat.TycoonTagRegister;
import com.nextplugins.economy.listener.events.check.CheckInteractListener;
import com.nextplugins.economy.listener.events.connection.UserConnectionListener;
import com.nextplugins.economy.listener.events.operation.AsyncPurseUpdateListener;
import com.nextplugins.economy.listener.events.operation.MoneyGiveListener;
import com.nextplugins.economy.listener.events.operation.MoneySetListener;
import com.nextplugins.economy.listener.events.operation.MoneyWithdrawListener;
import com.nextplugins.economy.listener.events.transaction.TransactionRequestListener;
import com.nextplugins.economy.listener.events.update.MoneyTopUpdateListener;
import lombok.Data;
import lombok.val;
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

            if (PurseValue.get(PurseValue::enable)) {
                pluginManager.registerEvents(
                        new AsyncPurseUpdateListener(),
                        plugin
                );
            }

            // transactions

            pluginManager.registerEvents(
                    new TransactionRequestListener(),
                    plugin
            );

            // update

            pluginManager.registerEvents(
                    new MoneyTopUpdateListener(),
                    plugin
            );

            // tycoon tag

            if (RankingValue.get(RankingValue::useTycoonTag)) {
                pluginManager.registerEvents(
                        new TycoonTagRegister(),
                        plugin
                );
            }

            val interactionRegistry = getPlugin().getInteractionRegistry();

            if (pluginManager.isPluginEnabled("nChat")) {
                pluginManager.registerEvents(
                        new OpeNChatListener(interactionRegistry),
                        plugin
                );
            } else if (pluginManager.isPluginEnabled("LegendChat")) {
                pluginManager.registerEvents(
                        new LegendChatListener(interactionRegistry),
                        plugin
                );
            }

            // check system

            if (FeatureValue.get(FeatureValue::checkSystemEnabled)) {
                pluginManager.registerEvents(
                        new CheckInteractListener(plugin.getAccountStorage()),
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
