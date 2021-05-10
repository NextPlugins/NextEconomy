package com.nextplugins.economy.task.registry;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.PurseAPI;
import com.nextplugins.economy.configuration.PurseValue;
import com.nextplugins.economy.task.AsyncPurseUpdateTask;
import lombok.Data;
import lombok.val;

@Data(staticConstructor = "of")
public final class TaskRegistry {

    private final NextEconomy plugin;

    public void register() {

        val scheduler = plugin.getServer().getScheduler();

        if (PurseAPI.isAvaliable()) {

            val purseUpdateDelay = PurseValue.get(PurseValue::nextUpdate);

            scheduler.runTaskTimerAsynchronously(
                    plugin,
                    new AsyncPurseUpdateTask(),
                    0,
                    purseUpdateDelay * 20L
            );

        }

        plugin.getLogger().info("Tasks registradas com sucesso.");

    }

}
