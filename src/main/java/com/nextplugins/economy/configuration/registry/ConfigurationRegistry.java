package com.nextplugins.economy.configuration.registry;

import com.henryfabio.minecraft.configinjector.bukkit.injector.BukkitConfigurationInjector;
import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.configuration.*;
import lombok.Data;

@Data(staticConstructor = "of")
public final class ConfigurationRegistry {

    private final NextEconomy plugin;

    public void register() {
        BukkitConfigurationInjector configurationInjector = new BukkitConfigurationInjector(plugin);

        configurationInjector.saveDefaultConfiguration(
                plugin,
                "messages.yml",
                "ranking.yml",
                "inventories.yml"
        );

        configurationInjector.injectConfiguration(
                FeatureValue.instance(),
                MessageValue.instance(),
                RankingValue.instance(),
                InventoryValue.instance(),
                PurseValue.instance()
        );

        getPlugin().getLogger().info("Configurações registradas e injetadas com sucesso.");
    }

}
