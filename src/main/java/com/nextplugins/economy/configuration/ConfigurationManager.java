package com.nextplugins.economy.configuration;

import com.nextplugins.economy.NextEconomy;
import lombok.Data;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Data(staticConstructor = "of")
public final class ConfigurationManager {

    private final String config;

    public ConfigurationManager saveDefault() {

        NextEconomy instance = NextEconomy.getInstance();
        instance.saveResource(this.config, false);

        return this;

    }

    public Configuration load() {

        NextEconomy instance = NextEconomy.getInstance();

        return YamlConfiguration.loadConfiguration(
                new File(instance.getDataFolder(), this.config)
        );

    }


}
