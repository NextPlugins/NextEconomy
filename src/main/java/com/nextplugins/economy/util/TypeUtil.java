package com.nextplugins.economy.util;

import com.nextplugins.economy.NextEconomy;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public final class TypeUtil {

    public static Material swapLegacy(String material, String legacy) {
        try {
            return Material.valueOf(material);
        }catch (Exception exception) {
            return Material.valueOf(legacy);
        }
    }

    public static ItemStack convertFromLegacy(String materialName, int damage) {
        if (materialName == null || materialName.equalsIgnoreCase("")) return null;

        try {
            val material = Material.valueOf("LEGACY_" + materialName);
            return new ItemStack(Bukkit.getUnsafe().fromLegacy(new MaterialData(material, (byte) damage)));
        } catch (Exception error) {
            try {
                return new ItemStack(Material.getMaterial(materialName), 1, (short) damage);
            } catch (Exception exception) {
                NextEconomy.getInstance().getLogger().warning("Material " + materialName + " is invalid!");
                return null;
            }
        }
    }

}
