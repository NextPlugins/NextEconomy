package com.nextplugins.economy.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ActionBarUtils {

    private static final String NMS_VERSION;
    private static boolean useOldMethods;

    static {
        val name = Bukkit.getServer().getClass().getPackage().getName();
        NMS_VERSION = name.substring(name.lastIndexOf(".") + 1);

        if (NMS_VERSION.equalsIgnoreCase("v1_8_R1") || NMS_VERSION.startsWith("v1_7_")) { // Not sure if 1_7 works for the protocol hack?
            useOldMethods = true;
        }
    }

    public static void sendActionBar(Player player, String message) {
        try {
            Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + NMS_VERSION + ".entity.CraftPlayer");
            Object craftPlayer = craftPlayerClass.cast(player);
            Object packet;
            Class<?> packetPlayOutChatClass = Class.forName("net.minecraft.server." + NMS_VERSION + ".PacketPlayOutChat");
            Class<?> packetClass = Class.forName("net.minecraft.server." + NMS_VERSION + ".Packet");
            if (useOldMethods) {
                Class<?> chatSerializerClass = Class.forName("net.minecraft.server." + NMS_VERSION + ".ChatSerializer");
                Class<?> iChatBaseComponentClass = Class.forName("net.minecraft.server." + NMS_VERSION + ".IChatBaseComponent");
                Method m3 = chatSerializerClass.getDeclaredMethod("a", String.class);
                Object cbc = iChatBaseComponentClass.cast(m3.invoke(chatSerializerClass, "{\"text\": \"" + message + "\"}"));
                packet = packetPlayOutChatClass.getConstructor(new Class<?>[]{iChatBaseComponentClass, byte.class}).newInstance(cbc, (byte) 2);
            } else {
                Class<?> chatComponentTextClass = Class.forName("net.minecraft.server." + NMS_VERSION + ".ChatComponentText");
                Class<?> iChatBaseComponentClass = Class.forName("net.minecraft.server." + NMS_VERSION + ".IChatBaseComponent");
                try {
                    Class<?> chatMessageTypeClass = Class.forName("net.minecraft.server." + NMS_VERSION + ".ChatMessageType");
                    Object[] chatMessageTypes = chatMessageTypeClass.getEnumConstants();
                    Object chatMessageType = null;
                    for (Object obj : chatMessageTypes) {
                        if (obj.toString().equals("GAME_INFO")) {
                            chatMessageType = obj;
                        }
                    }
                    Object chatCompontentText = chatComponentTextClass.getConstructor(new Class<?>[]{String.class}).newInstance(message);
                    packet = packetPlayOutChatClass.getConstructor(new Class<?>[]{iChatBaseComponentClass, chatMessageTypeClass}).newInstance(chatCompontentText, chatMessageType);
                } catch (ClassNotFoundException cnfe) {
                    Object chatCompontentText = chatComponentTextClass.getConstructor(new Class<?>[]{String.class}).newInstance(message);
                    packet = packetPlayOutChatClass.getConstructor(new Class<?>[]{iChatBaseComponentClass, byte.class}).newInstance(chatCompontentText, (byte) 2);
                }
            }
            Method craftPlayerHandleMethod = craftPlayerClass.getDeclaredMethod("getHandle");
            Object craftPlayerHandle = craftPlayerHandleMethod.invoke(craftPlayer);
            Field playerConnectionField = craftPlayerHandle.getClass().getDeclaredField("playerConnection");
            Object playerConnection = playerConnectionField.get(craftPlayerHandle);
            Method sendPacketMethod = playerConnection.getClass().getDeclaredMethod("sendPacket", packetClass);
            sendPacketMethod.invoke(playerConnection, packet);
        } catch (Exception e) {
            // newer versions
        }
    }
}
