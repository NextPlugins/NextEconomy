package com.nextplugins.economy.ranking.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public final class LocationUtil {

    public static Location byStringNoBlock(String... s) {
        return new Location(
                Bukkit.getWorld(s[0]),
                Double.parseDouble(s[1]),
                Double.parseDouble(s[2]),
                Double.parseDouble(s[3]),
                Float.parseFloat(s[4]),
                Float.parseFloat(s[5])
        );
    }

    public static String byLocationNoBlock(Location l) {
        return l.getWorld().getName() + ","
                + l.getX() + ","
                + l.getY() + ","
                + l.getZ() + ","
                + l.getYaw() + ","
                + l.getPitch();
    }

    public static Location byStringBlock(String... s) {
        return new Location(
                Bukkit.getWorld(s[0]),
                Double.parseDouble(s[1]),
                Double.parseDouble(s[2]),
                Double.parseDouble(s[3])
        );
    }

    public static String byLocationBlock(Location l) {
        return l.getWorld().getName() + ","
                + l.getX() + ","
                + l.getY() + ","
                + l.getZ();
    }

}
