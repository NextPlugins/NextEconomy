package com.nextplugins.economy.ranking;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.configuration.RankingValue;
import com.nextplugins.economy.model.ranking.HologramSupportType;
import com.nextplugins.economy.ranking.loader.LocationLoader;
import com.nextplugins.economy.ranking.types.ArmorStandRunnable;
import com.nextplugins.economy.ranking.types.HologramRunnable;
import com.nextplugins.economy.ranking.types.NPCRunnable;
import lombok.Data;
import lombok.Getter;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

import java.util.logging.Level;

@Data
public class CustomRankingRegistry {

    @Getter private static final CustomRankingRegistry instance = new CustomRankingRegistry();

    private NextEconomy plugin;

    private boolean enabled;
    private Runnable runnable;

    private HologramSupportType hologramAPI;

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

        this.hologramAPI = findHologramAPI(pluginManager);
        if (hologramAPI == HologramSupportType.NONE) {
            plugin.getLogger().log(Level.WARNING,
                    "Dependência não encontrada ({0}) O ranking em NPC, Holograma e ArmorStand não serão usados.",
                    "HolographicDisplays, DecentHolograms ou CMI"
            );

            return;
        }

        boolean isNpc = type.equalsIgnoreCase("npc");
        if (isNpc && !pluginManager.isPluginEnabled("ProtocolLib")) {
            plugin.getLogger().log(Level.WARNING,
                    "Dependência não encontrada ({0}) O ranking em NPC não será usado.",
                    "ProtocolLib"
            );

            type = "hologram";
        }

        LocationLoader.of(plugin, plugin.getLocationManager()).loadLocations();

        if (isNpc) {
            runnable = new NPCRunnable(plugin, hologramAPI);
        } else if (type.equalsIgnoreCase("armorstand")) {
            runnable = new ArmorStandRunnable(plugin, plugin.getLocationManager(), plugin.getRankingStorage(), hologramAPI);
        } else {
            runnable = new HologramRunnable(plugin, plugin.getLocationManager(), plugin.getRankingStorage(), hologramAPI);
        }

        enabled = true;
        plugin.getLogger().info("Sistema de NPC e ArmorStand registrado com sucesso.");
    }

    private HologramSupportType findHologramAPI(PluginManager pluginManager) {
        for (HologramSupportType value : HologramSupportType.values()) {
            if (pluginManager.isPluginEnabled(value.getPluginName())) {
                return value;
            }
        }

        return HologramSupportType.NONE;
    }

}
