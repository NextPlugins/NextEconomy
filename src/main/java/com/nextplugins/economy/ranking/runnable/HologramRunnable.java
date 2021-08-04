package com.nextplugins.economy.ranking.runnable;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.google.common.collect.Lists;
import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.model.account.SimpleAccount;
import com.nextplugins.economy.configuration.RankingValue;
import com.nextplugins.economy.ranking.manager.LocationManager;
import com.nextplugins.economy.ranking.storage.RankingStorage;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.var;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@RequiredArgsConstructor
public final class HologramRunnable implements Runnable {

    public static final List<Hologram> HOLOGRAM = Lists.newLinkedList();

    private final NextEconomy plugin;
    private final LocationManager locationManager;
    private final RankingStorage rankingStorage;

    @Override
    public void run() {

        val accounts = rankingStorage.getRankByCoin();
        if (accounts.isEmpty()) return;

        HOLOGRAM.forEach(Hologram::delete);

        val location = locationManager.getLocation(1);
        if (location == null || location.getWorld() == null) {

            plugin.getLogger().warning("A localização 1 do ranking é inválida.");
            return;

        }

        val chunk = location.getChunk();
        if (!chunk.isLoaded()) chunk.load(true);

        val hologramLocation = location.clone().add(0, accounts.size() / 2.0, 0);
        val hologram = HologramsAPI.createHologram(plugin, hologramLocation);

        for (val line : RankingValue.get(RankingValue::hologramDefaultLines)) {

            if (line.equalsIgnoreCase("@players")) playersLines(accounts).forEach(hologram::appendTextLine);
            else hologram.appendTextLine(line);

        }


    }

    public List<String> playersLines(List<SimpleAccount> accounts) {

        val lines = new ArrayList<String>();
        val line = RankingValue.get(RankingValue::hologramDefaultLine);

        var position = 1;
        for (val account : accounts) {
            lines.add(line
                    .replace("$position", String.valueOf(position))
                    .replace("$player", account.getUsername())
                    .replace("$amount", account.getBalanceFormated())
            );

            if (position == RankingValue.get(RankingValue::hologramDefaultLimit)) break;
        }

        return lines;

    }
}
