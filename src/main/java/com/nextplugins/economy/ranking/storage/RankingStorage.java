package com.nextplugins.economy.ranking.storage;

import com.google.common.collect.Lists;
import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.model.account.Account;
import com.nextplugins.economy.configuration.RankingValue;
import com.nextplugins.economy.ranking.runnable.UpdateRankingRunnable;
import lombok.Data;
import lombok.val;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Data
public final class RankingStorage {

    private UpdateRankingRunnable runnable;

    private final List<Account> rankByCoin = Lists.newLinkedList();
    private final List<Account> rankByMovimentation = Lists.newLinkedList();
    private long nextUpdateMillis = System.currentTimeMillis();

    public boolean updateRanking() {

        if (nextUpdateMillis < System.currentTimeMillis()) return false;

        val plugin = NextEconomy.getInstance();
        if (runnable == null) runnable = new UpdateRankingRunnable(plugin.getAccountRepository(), this);

        val updateDelayMillis = TimeUnit.SECONDS.toMillis(RankingValue.get(RankingValue::updateDelay));
        nextUpdateMillis = System.currentTimeMillis() + updateDelayMillis;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
        return true;

    }

}
