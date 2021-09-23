package com.nextplugins.economy.util;

import com.nextplugins.economy.configuration.FeatureValue;
import de.tr7zw.changeme.nbtapi.NBTItem;
import lombok.val;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class CheckUtil {

    public static ItemStack createCheck(double checkValue) {
        val checkSection = FeatureValue.get(FeatureValue::checkItem);

        List<String> lore = new ArrayList<>();
        for (String line : checkSection.getStringList("lore")) {
            String colored = ColorUtil.colored(line);
            String amount = colored.replace("$amount", NumberUtils.format(checkValue));
            lore.add(amount);
        }

        val checkItem = new ItemBuilder(Material.valueOf(checkSection.getString("material")), checkSection.getInt("data"))
                .name(ColorUtil.colored(checkSection.getString("display-name")))
                .setLore(lore)
                .wrap();

        val nbtItem = new NBTItem(checkItem);
        nbtItem.setDouble("value", checkValue);

        return nbtItem.getItem();
    }

}
