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

import java.util.List;
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

    // trocar pra configuration section e criar uma lista de embeds
    @ConfigField("embed.viewMoney.title") private String viewMoneyTitle;
    @ConfigField("embed.viewMoney.image") private String viewMoneyImage;
    @ConfigField("embed.viewMoney.imagefooter") private String viewMoneyFooterImage;
    @ConfigField("embed.viewMoney.footer") private String viewMoneyFooter;
    @ConfigField("embed.viewMoney.color") private String viewMoneyColor;
    @ConfigField("embed.viewMoney.date") private boolean viewMoneyDate;
    @ConfigField("embed.viewMoney.fields") private ConfigurationSection viewMoneyFields;

    @ConfigField("embed.top.title") private String topTitle;
    @ConfigField("embed.top.mage") private String topImage;
    @ConfigField("embed.top.imagefooter") private String topFooterImage;
    @ConfigField("embed.top.footer") private String topFooter;
    @ConfigField("embed.top.color") private String topColor;
    @ConfigField("embed.top.date") private boolean topDate;
    @ConfigField("embed.top.description") private List<String> topDescription;

    public static <T> T get(Function<DiscordValue, T> function) {
        return function.apply(instance);
    }

}

