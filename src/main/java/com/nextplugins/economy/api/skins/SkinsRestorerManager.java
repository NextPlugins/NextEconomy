package com.nextplugins.economy.api.skins;

import com.nextplugins.economy.NextEconomy;
import lombok.Getter;
import lombok.val;
import net.skinsrestorer.api.SkinsRestorerAPI;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class SkinsRestorerManager {

    @Getter private boolean enabled;

    public void init() {
        enabled = Bukkit.getPluginManager().isPluginEnabled("SkinsRestorer");

        if (!enabled) return;
        NextEconomy.getInstance().getLogger().info("[Skins] Integrado com sucesso com o plugin 'SkinsRestorer'");
    }

    @NotNull public String getSkinName(String player) {
        if (!enabled) return player;

        try {
            val skinName = SkinsRestorerAPI.getApi().getSkinName(player);
            return skinName == null ? player : skinName;
        } catch (Throwable ignored) {
            return player;
        }
    }

}
