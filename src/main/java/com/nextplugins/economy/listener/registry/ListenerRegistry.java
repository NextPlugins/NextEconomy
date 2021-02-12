package com.nextplugins.economy.listener.registry;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.listener.UserConnectListener;
import com.nextplugins.economy.listener.UserDisconnectListener;
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
                    new UserConnectListener(plugin.getAccountStorage()),
                    plugin
            );
            pluginManager.registerEvents(
                    new UserDisconnectListener(plugin.getAccountStorage()),
                    plugin
            );

            logger.info("Listeners registrados com sucesso!");
        } catch (Throwable t) {
            t.printStackTrace();
            logger.severe("Não foi possível registrar os listeners.");
        }
    }

}
