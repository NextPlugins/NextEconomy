package com.nextplugins.economy.util.title;

import com.nextplugins.economy.util.ColorUtil;
import lombok.val;
import lombok.var;
import org.bukkit.entity.Player;

import java.util.List;

class BukkitTitleAPI implements InternalTitleAPI {

    BukkitTitleAPI() {}

    @Override
    public void sendTitle(Player player, String message, int fadeIn, int stay, int fadeOut) {
        var titleMessage = ColorUtil.colored(message);
        if (!message.contains("<nl>")) titleMessage = message + "<nl>";

        val split = titleMessage.split("<nl>");
        val title = split[0];
        val subtitle = split[1];

        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }

    @Override
    public List<Object> buildPackets(String message, int fadeIn, int stay, int fadeOut) {
        return null;
    }

}
