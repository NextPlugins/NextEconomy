package com.nextplugins.economy.ranking;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.configuration.RankingConfiguration;
import com.nextplugins.economy.ranking.loader.LocationLoader;
import com.nextplugins.economy.ranking.runnable.NPCRunnable;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

@Data(staticConstructor = "of")
public class NPCRankingRegistry {

    private final NextEconomy plugin;

    public void register() {
        new LocationLoader(plugin, plugin.getLocationManager()).loadLocations();

        int updateDelay = RankingConfiguration.get(RankingConfiguration::updateDelay);

        BukkitScheduler scheduler = Bukkit.getScheduler();

        scheduler.runTaskTimer(
                plugin,
                new NPCRunnable(plugin, plugin.getLocationManager(), plugin.getRankingStorage()),
                15,
                updateDelay * 20L
        );
    }

}
