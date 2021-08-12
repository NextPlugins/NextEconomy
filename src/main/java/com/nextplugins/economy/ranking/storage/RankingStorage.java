package com.nextplugins.economy.ranking.storage;

import com.google.common.collect.Lists;
import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.event.operations.AsyncRankingUpdateEvent;
import com.nextplugins.economy.api.model.account.SimpleAccount;
import com.nextplugins.economy.configuration.RankingValue;
import lombok.Data;
import lombok.val;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@Data
public final class RankingStorage {

    private final ArrayList<SimpleAccount> rankByCoin = Lists.newArrayList();
    private final ArrayList<SimpleAccount> rankByMovimentation = Lists.newArrayList();
    private long nextUpdateMillis;

    public boolean updateRanking(boolean force) {

        if (!force && nextUpdateMillis > System.currentTimeMillis()) return false;

        val plugin = NextEconomy.getInstance();

        val pluginManager = Bukkit.getPluginManager();
        val updateDelayMillis = TimeUnit.SECONDS.toMillis(RankingValue.get(RankingValue::updateDelay));

        nextUpdateMillis = System.currentTimeMillis() + updateDelayMillis;

        plugin.getLogger().info("[Ranking] Iniciando atualização no ranking");

        plugin.getAccountStorage().getCache().synchronous().invalidateAll();

        Bukkit.getScheduler().runTaskLaterAsynchronously(
                plugin,
                () -> pluginManager.callEvent(new AsyncRankingUpdateEvent()),
                25L
        );

        return true;

    }

}
