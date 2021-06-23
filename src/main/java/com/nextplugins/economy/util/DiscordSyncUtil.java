package com.nextplugins.economy.util;

import com.nextplugins.economy.configuration.DiscordValue;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.util.DiscordUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DiscordSyncUtil {

    public static boolean isEnable() {
        return DiscordValue.get(DiscordValue::enable) && Bukkit.getPluginManager().isPluginEnabled("DiscordSRV");
    }

    public static String getUserTag(Player player) {
        if (!isEnable()) return "&cSistema desativado";

        val plugin = DiscordSRV.getPlugin();
        val accountLinkManager = plugin.getAccountLinkManager();

        val discordId = accountLinkManager.getDiscordId(player.getUniqueId());
        if (discordId == null) return "&cNão vinculado";

        val user = DiscordUtil.getJda().getUserById(discordId);
        if (user == null) return "&cUsuário não encontrado, vincule novamente";

        return user.getAsTag();
    }

}
