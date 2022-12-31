package com.nextplugins.economy.group;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.group.impl.NextTestServerGroupWrapper;
import com.nextplugins.economy.group.impl.VaultGroupWrapper;
import lombok.val;
import org.bukkit.Bukkit;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public final class GroupWrapperManager {

    private GroupWrapper wrapper;

    public void init() {
        val pluginManager = Bukkit.getPluginManager();
        if (pluginManager.isPluginEnabled("NextTestServer")) wrapper = new NextTestServerGroupWrapper();
        else wrapper = new VaultGroupWrapper();

        wrapper.setup();

        NextEconomy.getInstance().getLogger().info("[Grupos] Integrado com sucesso com o plugin '" + wrapper.getClass().getSimpleName() + "'");
    }

    public Group getGroup(String playerName) {
        return wrapper.getGroup(playerName);
    }

}
