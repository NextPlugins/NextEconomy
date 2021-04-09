package com.nextplugins.economy.ranking;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.configuration.values.RankingValue;
import com.nextplugins.economy.ranking.loader.LocationLoader;
import com.nextplugins.economy.ranking.runnable.ArmorStandRunnable;
import com.nextplugins.economy.ranking.runnable.NPCRunnable;
import lombok.Data;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;

@Data(staticConstructor = "of")
public class CustomRankingRegistry {

    private final NextEconomy plugin;

    protected final PluginManager MANAGER = Bukkit.getPluginManager();
    protected final String CITIZENS = "Citizens";
    protected final String HOLOGRAPHIC_DISPLAYS = "HolographicDisplays";

    @Getter private static boolean enabled;
    @Getter private static Runnable runnable;

    public void register() {

        String type = RankingValue.get(RankingValue::npcType);
        if (type.equalsIgnoreCase("nothing")) {

            plugin.getLogger().info(
                    "Cancelando registro de ranking por NPC e ArmorStand, você desativou na ranking.yml."
            );

            return;

        }


        if (!MANAGER.isPluginEnabled(HOLOGRAPHIC_DISPLAYS)) {

            plugin.getLogger().warning(
                    String.format("Dependência não encontrada (%s) O ranking em NPC e ArmorStand não serão usados.",
                            HOLOGRAPHIC_DISPLAYS
                    )
            );

            return;

        }

        boolean isNpc = type.equalsIgnoreCase("npc");
        if (isNpc && !MANAGER.isPluginEnabled(CITIZENS)) {

            plugin.getLogger().warning(
                    String.format("Dependência não encontrada (%s) O ranking em NPC não será usado.",
                            CITIZENS
                    )
            );

            return;

        }

        LocationLoader.of(plugin, plugin.getLocationManager()).loadLocations();

        int updateDelay = RankingValue.get(RankingValue::updateDelay);

        runnable = isNpc
                ? new NPCRunnable(plugin, plugin.getLocationManager(), plugin.getRankingStorage())
                : new ArmorStandRunnable(plugin, plugin.getLocationManager(), plugin.getRankingStorage());

        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskTimer(
                plugin,
                runnable,
                15,
                updateDelay * 20L
        );

        enabled = true;
        plugin.getLogger().info("Sistema de NPC e ArmorStand registrado com sucesso.");

    }

}
