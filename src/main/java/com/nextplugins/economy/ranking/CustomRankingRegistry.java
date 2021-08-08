package com.nextplugins.economy.ranking;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.configuration.RankingValue;
import com.nextplugins.economy.ranking.loader.LocationLoader;
import com.nextplugins.economy.ranking.runnable.ArmorStandRunnable;
import com.nextplugins.economy.ranking.runnable.HologramRunnable;
import com.nextplugins.economy.ranking.runnable.NPCRunnable;
import lombok.Data;
import lombok.Getter;
import lombok.val;
import org.bukkit.Bukkit;

import java.util.logging.Level;

@Data
public class CustomRankingRegistry {

    @Getter private static final CustomRankingRegistry instance = new CustomRankingRegistry();

    private NextEconomy plugin;

    private boolean enabled;
    private Runnable runnable;

    public static CustomRankingRegistry of(NextEconomy plugin) {

        instance.setPlugin(plugin);
        return instance;

    }

    public void register() {

        val pluginManager = Bukkit.getPluginManager();

        String type = RankingValue.get(RankingValue::npcType);
        if (type.equalsIgnoreCase("nothing")) {

            plugin.getLogger().info(
                    "Cancelando registro de ranking por NPC, Holograma e ArmorStand, você desativou na ranking.yml."
            );

            return;

        }


        if (!pluginManager.isPluginEnabled("HolographicDisplays")) {

            plugin.getLogger().log(Level.WARNING,
                    "Dependência não encontrada ({0}) O ranking em NPC, Holograma e ArmorStand não serão usados.",
                    "HolographicDisplays"
            );

            return;

        }

        boolean isNpc = type.equalsIgnoreCase("npc");
        if (isNpc && !pluginManager.isPluginEnabled("Citizens")) {

            plugin.getLogger().log(Level.WARNING,
                    "Dependência não encontrada ({0}) O ranking em NPC não será usado.",
                    "Citizens"
            );

            return;

        }

        LocationLoader.of(plugin, plugin.getLocationManager()).loadLocations();

        if (isNpc) runnable = new NPCRunnable(plugin, plugin.getLocationManager(), plugin.getRankingStorage());
        else if (type.equalsIgnoreCase("armorstand")) runnable = new ArmorStandRunnable(plugin, plugin.getLocationManager(), plugin.getRankingStorage());
        else runnable = new HologramRunnable(plugin, plugin.getLocationManager(), plugin.getRankingStorage());

        enabled = true;
        plugin.getLogger().info("Sistema de NPC e ArmorStand registrado com sucesso.");

    }

}
