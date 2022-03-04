package com.nextplugins.economy.ranking.storage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.event.operations.AsyncRankingUpdateEvent;
import com.nextplugins.economy.configuration.RankingValue;
import com.nextplugins.economy.model.account.SimpleAccount;
import lombok.Data;
import lombok.val;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

@Data
public final class RankingStorage {

    private final LinkedHashMap<String, SimpleAccount> rankByCoin = Maps.newLinkedHashMap();
    private final ArrayList<SimpleAccount> rankByMovimentation = Lists.newArrayList();

    private String topPlayer;
    private long nextUpdateMillis;
    private boolean updating;

    public boolean updateRanking(boolean force) {
        if (!force && nextUpdateMillis > System.currentTimeMillis()) return false;

        val plugin = NextEconomy.getInstance();
        val pluginManager = Bukkit.getPluginManager();
        val updateDelayMillis = TimeUnit.SECONDS.toMillis(RankingValue.get(RankingValue::updateDelay));

        nextUpdateMillis = System.currentTimeMillis() + updateDelayMillis;

        Bukkit.getScheduler().runTaskLaterAsynchronously(
                plugin,
                () -> pluginManager.callEvent(new AsyncRankingUpdateEvent()),
                30L
        );

        return true;
    }

    public String getTycoonTag(String playerName) {
        if (!rankByCoin.containsKey(playerName)) return "";

        return topPlayer.equals(playerName)
                ? RankingValue.get(RankingValue::tycoonTagValue)
                : RankingValue.get(RankingValue::tycoonRichTagValue);
    }

    /**
     * Retrieve top player
     *
     * @param movimentationRanking if true, will get the player with the largest amount of money moved, instead of the richest
     * @return player's account
     */
    public @Nullable SimpleAccount getTopPlayer(boolean movimentationRanking) {
        if (movimentationRanking) {
            if (rankByMovimentation.isEmpty()) return null;
            else return rankByMovimentation.get(0);
        } else {
            if (rankByCoin.isEmpty()) return null;
            else return rankByCoin.get(topPlayer);
        }
    }

}
