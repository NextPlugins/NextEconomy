package com.nextplugins.economy.configuration;

import com.henryfabio.minecraft.configinjector.common.annotations.ConfigField;
import com.henryfabio.minecraft.configinjector.common.annotations.ConfigFile;
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
@ConfigFile("discord.yml")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DiscordValue implements ConfigurationInjectable {

    @Getter private static final DiscordValue instance = new DiscordValue();

    // configuration

    @ConfigField("config.enable") private boolean enable;
    @ConfigField("config.prefix") private String prefix;

    // emojis

    @ConfigField("emojis.error") private String errorEmoji;
    @ConfigField("emojis.invalid") private String invalidEmoji;
    @ConfigField("emojis.loading") private String loadingEmoji;
    @ConfigField("emojis.success") private String successEmoji;

    public static <T> T get(Function<DiscordValue, T> function) {
        return function.apply(instance);
    }

}

