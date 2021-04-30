package com.nextplugins.economy.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.util.Arrays;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class TitleUtils {

    public static void sendTitle(Player player, String message, int fadeIn, int stay, int fadeOut) {
        try {
            sendTitlePacket(player, buildTitlePackets(message, fadeIn, stay, fadeOut));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void sendTitleToAll(String message, int fadeIn, int stay, int fadeOut) {

        Object[] packets = buildTitlePackets(message, fadeIn, stay, fadeOut);
        try {

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                sendTitlePacket(onlinePlayer, packets);
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    public static Object[] buildTitlePackets(String message, int fadeIn, int stay, int fadeOut) {

        String[] split = message.split("<nl>");
        String title = ColorUtil.colored(split[0]);
        String subtitle = ColorUtil.colored(split[1]);

        return new Object[] {
                buildPacket(title, "TITLE", fadeIn, stay, fadeOut),
                buildPacket(subtitle, "SUBTITLE", fadeIn, stay, fadeOut)
        };

    }

    public static void sendTitlePacket(Player player, Object[] packets) {
        Arrays.stream(packets).forEach(packet -> PacketUtils.sendPacket(player, packet));
    }

    private static Object buildPacket(String message, String type, int fadeIn, int stay, int fadeOut) {

        try {
            Object component = PacketUtils.getNMSClass("IChatBaseComponent")
                    .getDeclaredClasses()[0]
                    .getMethod("a", String.class)
                    .invoke(null, "{\"text\":\"" + message + "\"}");

            Constructor constructor = PacketUtils.getNMSClass("PacketPlayOutTitle")
                    .getConstructor(
                            PacketUtils.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0],
                            PacketUtils.getNMSClass("IChatBaseComponent"),
                            Integer.TYPE,
                            Integer.TYPE,
                            Integer.TYPE
                    );

            Object packet = constructor.newInstance(
                    PacketUtils.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField(type).get(null),
                    component, fadeIn, stay, fadeOut
            );

            return packet;

        } catch (Exception ignored) {
        }

        return null;
    }

}
