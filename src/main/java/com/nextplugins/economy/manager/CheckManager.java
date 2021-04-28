package com.nextplugins.economy.manager;

import com.nextplugins.economy.configuration.FeatureValue;
import com.nextplugins.economy.util.ColorUtil;
import com.nextplugins.economy.util.ItemBuilder;
import com.nextplugins.economy.util.NumberUtils;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.stream.Collectors;

public final class CheckManager {

    public static ItemStack createCheck(double checkValue) {
        final ConfigurationSection checkSection = FeatureValue.get(FeatureValue::checkItem);

        final ItemStack checkItem = new ItemBuilder(Material.valueOf(checkSection.getString("material")), checkSection.getInt("data"))
                .name(ColorUtil.colored(checkSection.getString("display-name")))
                .setLore(
                        checkSection.getStringList("lore").stream()
                                .map(ColorUtil::colored)
                                .map(s -> s.replace("$amount", NumberUtils.format(checkValue)))
                                .collect(Collectors.toList())
                )
                .wrap();

        final NBTItem checkNBT = new NBTItem(checkItem);

        checkNBT.setDouble("value", checkValue);

        return checkNBT.getItem();
    }

}
