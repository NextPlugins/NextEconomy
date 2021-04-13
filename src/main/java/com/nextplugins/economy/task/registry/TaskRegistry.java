package com.nextplugins.economy.task.registry;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.configuration.values.FeatureValue;
import com.nextplugins.economy.configuration.values.PurseValue;
import com.nextplugins.economy.configuration.values.RankingValue;
import com.nextplugins.economy.task.AccountRankingTask;
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
                new AccountSaveTask(plugin.getAccountStorage(), plugin.getAccountDAO()),
                0,
                accountSaveDelay * 20L
        );

        // ranking update

        int accountRankingUpdateDelay = RankingValue.get(RankingValue::updateDelay);

        scheduler.runTaskTimerAsynchronously(
                plugin,
                new AccountRankingTask(plugin.getAccountDAO(), plugin.getRankingStorage()),
                0,
                accountRankingUpdateDelay * 20L
        );

        // purse update

        int purseUpdateDelay = PurseValue.get(PurseValue::nextUpdate);

        scheduler.runTaskTimer(
                plugin,
                new PurseUpdateTask(),
                0,
                purseUpdateDelay * 20L
        );

        plugin.getLogger().info("Tasks registradas com sucesso.");

    }

}
