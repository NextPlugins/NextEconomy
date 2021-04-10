package com.nextplugins.economy.vault;

import com.nextplugins.economy.util.ColorUtil;
import lombok.val;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

public class VaultGroupHook {

    private Chat chat;
    private Permission permission;

    public void init(Plugin plugin) {

        RegisteredServiceProvider<Permission> permissionProvider = plugin.getServer().getServicesManager().getRegistration(Permission.class);
        RegisteredServiceProvider<Chat> chatProvider = plugin.getServer().getServicesManager().getRegistration(Chat.class);

        this.permission = permissionProvider.getProvider();
        this.chat = chatProvider.getProvider();

    }

    public String getGroupPrefix(OfflinePlayer player) {

        val playerGroups = this.permission.getPlayerGroups("test", player);
        if (playerGroups.length < 1) return "";

        String playerGroup = playerGroups[0];
        if (!playerGroup.endsWith(" ")) playerGroup += " ";

        return ColorUtil.colored(this.chat.getGroupPrefix("test", playerGroup));

    }

}
