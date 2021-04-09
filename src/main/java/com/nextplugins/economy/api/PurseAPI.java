package com.nextplugins.economy.api;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.event.operations.AsyncPurseUpdateEvent;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PurseAPI {

    @Getter private static final PurseAPI instance = new PurseAPI();

    private int purse;
    private long nextUpdate;

    public void updatePurse(int newValue) {

        Bukkit.getScheduler().runTaskAsynchronously(NextEconomy.getInstance(), () -> {

            AsyncPurseUpdateEvent asyncPurseUpdateEvent = new AsyncPurseUpdateEvent(
                    newValue,
                    purse,
                    Instant.now(),
                    Instant.ofEpochMilli(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(600))
            );

            PluginManager pluginManager = Bukkit.getPluginManager();
            pluginManager.callEvent(asyncPurseUpdateEvent);

        });

    }

}
