package com.nextplugins.economy.util;

import com.nextplugins.economy.NextEconomy;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class TitleUtils {

    public static void sendTitle(Player player, String message, int fadeIn, int stay, int fadeOut) {
        val internalApi = NextEconomy.getInstance().getInternalTitleAPI();
        if (internalApi == null) return;

        internalApi.sendTitle(player, message, fadeIn, stay, fadeOut);
    }

    public static void sendPacketsToAll(List<Object> packets) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            sendPacketsToPlayer(onlinePlayer, packets);
        }
    }

    public static List<Object> buildTitlePackets(String message, int fadeIn, int stay, int fadeOut) {
        val internalApi = NextEconomy.getInstance().getInternalTitleAPI();
        if (internalApi == null) return null;

        return internalApi.buildPackets(message, fadeIn, stay, fadeOut);
    }

    public static void sendPacketsToPlayer(Player player, List<Object> packets) {
        for (Object packet : packets) {
            PacketUtils.sendPacket(player, packet);
        }
    }

}
