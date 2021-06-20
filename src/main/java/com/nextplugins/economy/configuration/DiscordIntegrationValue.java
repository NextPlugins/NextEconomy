package com.nextplugins.economy.configuration;

import com.henryfabio.minecraft.configinjector.common.annotations.ConfigField;
import com.henryfabio.minecraft.configinjector.common.annotations.ConfigFile;
import com.henryfabio.minecraft.configinjector.common.annotations.ConfigSection;
import com.henryfabio.minecraft.configinjector.common.annotations.TranslateColors;
import com.henryfabio.minecraft.configinjector.common.injector.ConfigurationInjectable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.function.Function;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
@Getter
@TranslateColors
@Accessors(fluent = true)
@ConfigSection("plugin.configuration.discord")
@ConfigFile("config.yml")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DiscordIntegrationValue implements ConfigurationInjectable {

    @Getter private static final DiscordIntegrationValue instance = new DiscordIntegrationValue();

    // configuration

    @ConfigField("enable") private boolean enable;

    public static <T> T get(Function<DiscordIntegrationValue, T> function) {
        return function.apply(instance);
    }

}

