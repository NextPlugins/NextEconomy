package com.nextplugins.economy.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import org.bukkit.ChatColor;
import org.bukkit.Color;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ColorUtil {

    public static String colored(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String[] colored(String... messages) {
        for (int i = 0; i < messages.length; i++) {
            messages[i] = colored(messages[i]);
        }

        return messages;
    }

    public static List<String> colored(List<String> description) {

        return description.stream()
                .map(ColorUtil::colored)
                .collect(Collectors.toList());

    }

    public static java.awt.Color getColorByHex(String hex) {
        return java.awt.Color.decode(hex);
    }

    public static Color getBukkitColorByHex(String hex) {

        val decode = getColorByHex(hex);
        return Color.fromRGB(decode.getRed(), decode.getGreen(), decode.getBlue());

    }

}
