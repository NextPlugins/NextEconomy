package com.nextplugins.economy.task.registry;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.configuration.GeneralConfiguration;
import com.nextplugins.economy.task.AccountSaveTask;
import lombok.Data;
import org.bukkit.scheduler.BukkitScheduler;

@Data(staticConstructor = "of")
public final class TaskRegistry {

    private final NextEconomy plugin;

    public void register() {

        BukkitScheduler scheduler = plugin.getServer().getScheduler();

        // account save

        int accountSaveDelay = GeneralConfiguration.get(GeneralConfiguration::saveDelay);

        scheduler.runTaskTimerAsynchronously(
                plugin,
                new AccountSaveTask(plugin.getAccountStorage(), plugin.getAccountDAO()),
                0,
                accountSaveDelay * 20L
        );

    }

}
