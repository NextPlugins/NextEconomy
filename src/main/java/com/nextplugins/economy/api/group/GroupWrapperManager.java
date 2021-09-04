package com.nextplugins.economy.api.group;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.group.impl.LuckPermsGroupWrapper;
import com.nextplugins.economy.api.group.impl.PermissionsExGroupWrapper;
import com.nextplugins.economy.api.group.impl.VaultGroupWrapper;
import lombok.val;
import org.bukkit.Bukkit;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public final class GroupWrapperManager {

    private final GroupWrapper wrapper = new VaultGroupWrapper();

    public void init() {

/*        val pluginManager = Bukkit.getPluginManager();
        if (pluginManager.isPluginEnabled("PermissionsEx")) wrapper = new PermissionsExGroupWrapper();
        else if (pluginManager.isPluginEnabled("LuckPerms")) wrapper = new LuckPermsGroupWrapper();
        else wrapper = new VaultGroupWrapper();*/

        wrapper.setup();

        NextEconomy.getInstance().getLogger().info("[Grupos] Integrado com sucesso com o plugin '" + wrapper.getClass().getSimpleName() + "'");

    }

    public Group getGroup(String playerName) {
        return wrapper.getGroup(playerName);
    }

}
