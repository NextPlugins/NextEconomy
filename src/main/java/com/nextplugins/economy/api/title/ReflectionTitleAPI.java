package com.nextplugins.economy.api.title;

import com.google.common.collect.Lists;
import com.nextplugins.economy.util.ColorUtil;
import com.nextplugins.economy.util.PacketUtils;
import lombok.val;
import lombok.var;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

class ReflectionTitleAPI implements InternalTitleAPI {

    ReflectionTitleAPI() {
    }

    private Class<?> getNMSClass(String name) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    @Override
    public List<Object> buildPackets(String message, int fadeIn, int stay, int fadeOut) {
        var titleMessage = ColorUtil.colored(message);
        if (!message.contains("<nl>")) titleMessage = message + "<nl>";

        val split = titleMessage.split("<nl>");
        val title = split[0];
        val subtitle = split[1];

        try {
            List<Object> packets = new ArrayList<>();
            if (title != null) {
                Object e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TIMES").get(null);
                Object chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[]{String.class}).invoke(null, "{\"text\":\"" + title + "\"}");
                Constructor<?> subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE);
                Object titlePacket = subtitleConstructor.newInstance(e, chatTitle, fadeIn, stay, fadeOut);
                packets.add(titlePacket);

                Object e2 = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null);
                Object chatTitle2 = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[]{String.class}).invoke(null, "{\"text\":\"" + title + "\"}");
                Constructor<?> subtitleConstructor2 = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"));
                Object titlePacket2 = subtitleConstructor2.newInstance(e2, chatTitle2);
                packets.add(titlePacket2);
            }

            if (subtitle != null) {
                Object e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TIMES").get(null);
                Object chatSubtitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[]{String.class}).invoke(null, "{\"text\":\"" + title + "\"}");
                Constructor<?> subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE);
                Object subtitlePacket = subtitleConstructor.newInstance(e, chatSubtitle, fadeIn, stay, fadeOut);
                packets.add(subtitlePacket);

                Object e2 = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE").get(null);
                Object chatSubtitle2 = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[]{String.class}).invoke(null, "{\"text\":\"" + subtitle + "\"}");
                Constructor<?> subtitleConstructor2 = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE);
                Object subtitlePacket2 = subtitleConstructor2.newInstance(e2, chatSubtitle2, fadeIn, stay, fadeOut);
                packets.add(subtitlePacket2);
            }

            return packets;
        } catch (Exception exception) {
            return Lists.newArrayList();
        }
    }

    @Override
    public void sendTitle(Player player, String message, int fadeIn, int stay, int fadeOut) {
        try {
            sendTitlePacket(player, buildPackets(message, fadeIn, stay, fadeOut));
        } catch (Exception e) {
        }
    }

    public static void sendTitlePacket(Player player, List<Object> packets) {
        packets.forEach(packet -> PacketUtils.sendPacket(player, packet));
    }

}
