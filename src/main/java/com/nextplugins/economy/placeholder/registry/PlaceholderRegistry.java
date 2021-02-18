package com.nextplugins.economy.placeholder.registry;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.placeholder.EconomyPlaceholderHook;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

@Data(staticConstructor = "of")
public final class PlaceholderRegistry {

    private final NextEconomy plugin;

    private static final PluginManager MANAGER = Bukkit.getPluginManager();
    private static final String PLACEHOLDERS_API = "PlaceholderAPI";

    public void register() {
        if (!MANAGER.isPluginEnabled(PLACEHOLDERS_API)) {
            plugin.getLogger().warning(
                    String.format("Dependência não encontrada (%s). A placeholder não poderá ser usada.",
                            PLACEHOLDERS_API
                    )
            );
            return;
        }

        new EconomyPlaceholderHook(plugin).register();
        plugin.getLogger().info("Placeholder registrada com sucesso!");
    }

}
