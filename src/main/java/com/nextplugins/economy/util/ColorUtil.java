package com.nextplugins.economy.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ColorUtil {

    @NotNull public static String colored(@Nullable String message) {
        if (message == null) return "";

        Pattern pattern = Pattern.compile("&#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            String hexCode = message.substring(matcher.start(), matcher.end());
            String replaceSharp = hexCode.replace("&#", "x");

            char[] ch = replaceSharp.toCharArray();
            StringBuilder builder = new StringBuilder();
            for (char c : ch) {
                builder.append("&").append(c);
            }

            message = message.replace(hexCode, builder.toString());
            matcher = pattern.matcher(message);
        }

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
