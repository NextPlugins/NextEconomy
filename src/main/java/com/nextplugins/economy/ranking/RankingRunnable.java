package com.nextplugins.economy.ranking;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.StockExchangeAPI;
import com.nextplugins.economy.configuration.RankingValue;
import com.nextplugins.economy.model.account.SimpleAccount;
import com.nextplugins.libs.npcranking.Ranking;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RankingRunnable implements Runnable {

    private final NextEconomy plugin;
    private final Ranking ranking;
    private final RankingStorage rankingStorage;
    private final List<String> extraFormat;

    public RankingRunnable(NextEconomy plugin) {
        this.plugin = plugin;
        this.ranking = new Ranking(plugin, RankingValue.get(RankingValue::hologramFormat));
        this.extraFormat = RankingValue.get(RankingValue::extraFormat);
        this.rankingStorage = plugin.getRankingStorage();

        rankingStorage.updateRanking(true);
    }

    public NextEconomy getPlugin() {
        return plugin;
    }

    public Ranking getRanking() {
        return ranking;
    }

    public void register() {
        if (!RankingValue.get(RankingValue::npcEnabled)) return;

        final int updateDelay = RankingValue.get(RankingValue::updateDelay);

        if (!Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            plugin.getLogger().info("É necessário possuir o ProtocolLib para habilitar o ranking.");

            return;
        }

        Bukkit.getScheduler().runTaskTimer(plugin, this, 20, updateDelay);
    }

    @Override
    public void run() {
        StockExchangeAPI.getInstance().update();
        update();
    }

    public void update() {
        final List<SimpleAccount> simpleAccounts = new ArrayList<>(rankingStorage.getRankByCoin().values());

        ranking.update(simpleAccounts, SimpleAccount::getUsername, (account) ->
                extraFormat.stream()
                        .map((lines) -> lines.replace("$amount", account.getBalanceFormatted()))
                        .collect(Collectors.toList())
        );
    }

    public void destroy() {
        ranking.destroy();
    }

}
