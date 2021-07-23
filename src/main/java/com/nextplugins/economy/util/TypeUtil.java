package com.nextplugins.economy.util;

import org.bukkit.Material;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public final class TypeUtil {

    public static Material getType(String name) {
        try {
            return Material.getMaterial(name);
        } catch (Exception e) {
            return Material.getMaterial(name, true);
        }
    }

}
