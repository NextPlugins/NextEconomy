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

import java.util.List;
import java.util.function.Function;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Getter
@TranslateColors
@Accessors(fluent = true)
@ConfigSection("plugin.configuration.purse")
@ConfigFile("configuration.yml")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PurseValue implements ConfigurationInjectable {

    @Getter private static final PurseValue instance = new PurseValue();

    @ConfigField("enable") private boolean enable;
    @ConfigField("messageDisabledWorlds") private List<String> worlds;
    @ConfigField("method") private String messageMethod;

    @ConfigField("media") private int media;
    @ConfigField("minValue") private int minValue;
    @ConfigField("maxValue") private int maxValue;
    @ConfigField("nextUpdate") private int nextUpdate;

    public static <T> T get(Function<PurseValue, T> function) {
        return function.apply(instance);
    }

}
