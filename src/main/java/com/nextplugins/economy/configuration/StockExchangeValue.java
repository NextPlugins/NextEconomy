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
public final class StockExchangeValue implements ConfigurationInjectable {

    @Getter private static final StockExchangeValue instance = new StockExchangeValue();

    @ConfigField("enable") private boolean enable;
    @ConfigField("message-disabled-worlds") private List<String> worlds;
    @ConfigField("method") private String messageMethod;

    @ConfigField("average") private int average;
    @ConfigField("minValue") private int minValue;
    @ConfigField("maxValue") private int maxValue;

    public static <T> T get(Function<StockExchangeValue, T> function) {
        return function.apply(instance);
    }

}
