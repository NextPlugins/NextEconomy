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

@Getter
@TranslateColors
@Accessors(fluent = true)
@ConfigSection("plugin")
@ConfigFile("config.yml")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GeneralConfiguration implements ConfigurationInjectable {

    @Getter private static final GeneralConfiguration instance = new GeneralConfiguration();

    // configuration

    @ConfigField("configuration.format-type") private String formatType;
    @ConfigField("configuration.save-delay") private int saveDelay;
    @ConfigField("configuration.use-bStats") private boolean useBStats;

    public static <T> T get(Function<GeneralConfiguration, T> function) {
        return function.apply(instance);
    }

}
