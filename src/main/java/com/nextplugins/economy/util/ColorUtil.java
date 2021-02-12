package com.nextplugins.economy.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.ChatColor;

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

}
