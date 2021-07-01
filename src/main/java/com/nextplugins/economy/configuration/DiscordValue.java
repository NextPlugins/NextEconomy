package com.nextplugins.economy.configuration;

import com.henryfabio.minecraft.configinjector.common.annotations.ConfigField;
import com.henryfabio.minecraft.configinjector.common.annotations.ConfigFile;
import com.henryfabio.minecraft.configinjector.common.annotations.TranslateColors;
import com.henryfabio.minecraft.configinjector.common.injector.ConfigurationInjectable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.bukkit.configuration.ConfigurationSection;

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

    @ConfigField("embed.title") private String embedTitle;
    @ConfigField("embed.image") private String embedImage;
    @ConfigField("embed.imagefooter") private String embedFooterImage;
    @ConfigField("embed.footer") private String embedFooter;
    @ConfigField("embed.color") private String embedColor;
    @ConfigField("embed.date") private Boolean embedDate;

    @ConfigField("embed.fields") private ConfigurationSection embedFields;

    public static <T> T get(Function<DiscordValue, T> function) {
        return function.apply(instance);
    }

}

