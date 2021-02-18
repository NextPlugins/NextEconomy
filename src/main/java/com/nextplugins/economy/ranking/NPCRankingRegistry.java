package com.nextplugins.economy.ranking;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.configuration.values.RankingValue;
import com.nextplugins.economy.ranking.loader.LocationLoader;
import com.nextplugins.economy.ranking.runnable.NPCRunnable;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;

@Data(staticConstructor = "of")
public class NPCRankingRegistry {

    private final NextEconomy plugin;

    protected final PluginManager MANAGER = Bukkit.getPluginManager();
    protected final String CITIZENS = "Citizens";
    protected final String HOLOGRAPHIC_DISPLAYS = "HolographicDisplays";

    public static boolean isEnabled;

    public void register() {
        if (!MANAGER.isPluginEnabled(CITIZENS) || !MANAGER.isPluginEnabled(HOLOGRAPHIC_DISPLAYS)) {
            plugin.getLogger().warning(
                    String.format("Dependências não encontradas (%s, %s) O ranking em NPC não será usado.",
                            CITIZENS,
                            HOLOGRAPHIC_DISPLAYS
                    )
            );
            isEnabled = false;
            return;
        }

        new LocationLoader(plugin, plugin.getLocationManager()).loadLocations();

        int updateDelay = RankingValue.get(RankingValue::updateDelay);

        BukkitScheduler scheduler = Bukkit.getScheduler();

        scheduler.runTaskTimer(
                plugin,
                new NPCRunnable(plugin, plugin.getLocationManager(), plugin.getRankingStorage()),
                15,
                updateDelay * 20L
        );

        isEnabled = true;
    }

}
