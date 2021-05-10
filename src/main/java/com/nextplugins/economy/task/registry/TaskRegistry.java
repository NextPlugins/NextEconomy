package com.nextplugins.economy.task.registry;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.configuration.FeatureValue;
import com.nextplugins.economy.configuration.PurseValue;
import com.nextplugins.economy.task.AccountSaveTask;
import com.nextplugins.economy.task.PurseUpdateTask;
import lombok.Data;
import org.bukkit.scheduler.BukkitScheduler;

@Data(staticConstructor = "of")
public final class TaskRegistry {

    private final NextEconomy plugin;

    public void register() {

        BukkitScheduler scheduler = plugin.getServer().getScheduler();

        // account save

        int accountSaveDelay = FeatureValue.get(FeatureValue::saveDelay);

        scheduler.runTaskTimerAsynchronously(
                plugin,
                new AccountSaveTask(plugin.getAccountStorage(), plugin.getAccountRepository()),
                0,
                accountSaveDelay * 20L
        );

        // purse update

        int purseUpdateDelay = PurseValue.get(PurseValue::nextUpdate);

        scheduler.runTaskTimerAsynchronously(
                plugin,
                new PurseUpdateTask(),
                0,
                purseUpdateDelay * 20L
        );

        plugin.getLogger().info("Tasks registradas com sucesso.");

    }

}
