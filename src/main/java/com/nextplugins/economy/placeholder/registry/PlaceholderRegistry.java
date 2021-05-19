package com.nextplugins.economy.placeholder.registry;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.placeholder.EconomyPlaceholderHook;
import lombok.Data;
import org.bukkit.Bukkit;

@Data(staticConstructor = "of")
public final class PlaceholderRegistry {

    private final NextEconomy plugin;

    public void register() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            plugin.getLogger().warning(
                    "Dependência não encontrada (PlaceholderAPI). A placeholder não poderá ser usada."
            );
            return;
        }

        new EconomyPlaceholderHook(plugin).register();
        plugin.getLogger().info("Placeholder registrada com sucesso!");
    }

}
