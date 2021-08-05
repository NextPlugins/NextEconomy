package com.nextplugins.economy.api.group;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.group.impl.LuckPermsGroupWrapper;
import com.nextplugins.economy.api.group.impl.PermissionsExGroupWrapper;
import com.nextplugins.economy.api.group.impl.VaultGroupWrapper;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public final class GroupWrapperManager {

    private GroupWrapper wrapper;

    public void init() {

        val pluginManager = Bukkit.getPluginManager();
        if (pluginManager.isPluginEnabled("PermissionsEx")) wrapper = new PermissionsExGroupWrapper();
        else if (pluginManager.isPluginEnabled("LuckPerms")) wrapper = new LuckPermsGroupWrapper();
        else wrapper = new VaultGroupWrapper();

        wrapper.setup();

        NextEconomy.getInstance().getLogger().info("[Groups] Binded with '" + wrapper.getClass().getSimpleName() + "'");

    }

    public String getPrefix(String playerName) {
        val prefix = wrapper.getPrefix(playerName);
        if (prefix != null && !prefix.isEmpty() && !prefix.endsWith(" ")) return prefix + " ";

        return prefix;
    }


}
