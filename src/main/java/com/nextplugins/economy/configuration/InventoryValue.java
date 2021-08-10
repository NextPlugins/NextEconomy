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
@ConfigSection("inventory")
@ConfigFile("inventories.yml")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InventoryValue implements ConfigurationInjectable {

    @Getter private static final InventoryValue instance = new InventoryValue();

    // main inventory

    @ConfigField("enable") private boolean enable;

    @ConfigField("historic.config.name") private String historicInventoryName;
    @ConfigField("historic.config.size") private int historicInventorySize;

    @ConfigField("historic.depositMessage") private String depositMessage;
    @ConfigField("historic.withdrawMessage") private String withdrawMessage;

    @ConfigField("historic.item.skullName") private String historicSkullName;
    @ConfigField("historic.item.displayName") private String historicDisplayName;
    @ConfigField("historic.item.lore") private List<String> historicLore;

    @ConfigField("main.config.name") private String mainInventoryName;
    @ConfigField("main.config.size") private int mainInventorySize;

    @ConfigField("main.buttons.help") private ConfigurationSection helpButton;
    @ConfigField("main.buttons.yourMoney") private ConfigurationSection yourMoneyButton;
    @ConfigField("main.buttons.viewPlayerMoney") private ConfigurationSection viewPlayerMoneyButton;
    @ConfigField("main.buttons.sendMoney") private ConfigurationSection sendMoneyButton;
    @ConfigField("main.buttons.purse") private ConfigurationSection purseButton;
    @ConfigField("main.buttons.topMoney") private ConfigurationSection topMoneyButton;

    @ConfigField("main.buttons.purse.historyLine") private String purseHistoryLine;

    public static <T> T get(Function<InventoryValue, T> function) {
        return function.apply(instance);
    }

}
