package com.nextplugins.economy.configuration.registry;

import com.henryfabio.minecraft.configinjector.bukkit.injector.BukkitConfigurationInjector;
import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.configuration.GeneralConfiguration;
import lombok.Data;

@Data(staticConstructor = "of")
public final class ConfigurationRegistry {

    private final NextEconomy plugin;

    public void register() {
        BukkitConfigurationInjector configurationInjector = new BukkitConfigurationInjector(plugin);

        configurationInjector.saveDefaultConfiguration(
                plugin,
                "messages.yml",
                "ranking.yml"
        );

        configurationInjector.injectConfiguration(
                GeneralConfiguration.instance()
        );
    }

}
