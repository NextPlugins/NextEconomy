package com.nextplugins.economy.api.title;

import org.bukkit.entity.Player;

import java.util.List;

public interface InternalTitleAPI {

    void sendTitle(Player player, String message, int fadeIn, int stay, int fadeOut);

    List<Object> buildPackets(String message, int fadeIn, int stay, int fadeOut);

    default void clearTitle(Player player) {
        sendTitle(player, "", 0, 0, 0);
    }

}
