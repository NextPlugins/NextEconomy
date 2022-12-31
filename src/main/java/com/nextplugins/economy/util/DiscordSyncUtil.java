package com.nextplugins.economy.util;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.util.DiscordUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DiscordSyncUtil {

    public static boolean isEnable() {
        return DiscordValue.get(DiscordValue::enable) && Bukkit.getPluginManager().isPluginEnabled("DiscordSRV");
    }

    public static String getUserTag(OfflinePlayer player) {
        if (!isEnable()) return null;

        val plugin = DiscordSRV.getPlugin();
        val accountLinkManager = plugin.getAccountLinkManager();
        if (accountLinkManager == null) return null;

        val discordId = accountLinkManager.getDiscordId(player.getUniqueId());
        if (discordId == null) return null;

        val user = DiscordUtil.getJda().getUserById(discordId);
        if (user == null) return null;

        return user.getAsTag();
    }

}
