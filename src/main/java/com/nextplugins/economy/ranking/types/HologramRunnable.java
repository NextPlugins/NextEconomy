package com.nextplugins.economy.ranking.types;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Holograms.CMIHologram;
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
import java.util.Collection;
import java.util.List;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@RequiredArgsConstructor
public final class HologramRunnable implements Runnable {

    public static final List<String> HOLOGRAMS = Lists.newLinkedList();

    private final NextEconomy plugin;
    private final LocationManager locationManager;
    private final RankingStorage rankingStorage;

    private final boolean holographicDisplays;

    @Override
    public void run() {

        if (holographicDisplays) HologramsAPI.getHolograms(plugin).forEach(Hologram::delete);
        else {
            for (val entry : HOLOGRAMS) {
                val cmiHologram = CMI.getInstance().getHologramManager().getHolograms().get(entry);
                if (cmiHologram == null) continue;
                CMI.getInstance().getHologramManager().removeHolo(cmiHologram);
            }

        }

        HOLOGRAMS.clear();

        if (locationManager.getLocationMap().isEmpty()) return;

        val accounts = rankingStorage.getRankByCoin();
        val location = locationManager.getLocation(1);
        if (location == null || location.getWorld() == null) {

            plugin.getLogger().warning("A localização 1 do ranking é inválida.");
            return;

        }

        val chunk = location.getChunk();
        if (!chunk.isLoaded()) chunk.load(true);

        val playerLines = playersLines(accounts.values());

        val hologramLocation = location.clone().add(0, 5, 0);
        if (holographicDisplays) {
            val hologram = HologramsAPI.createHologram(plugin, hologramLocation);

            for (val line : RankingValue.get(RankingValue::hologramDefaultLines)) {

                if (line.equalsIgnoreCase("@players")) {
                    playerLines.forEach(hologram::appendTextLine);
                } else hologram.appendTextLine(line);

            }
        } else {

            val cmiHologram = new CMIHologram("NextEconomy", hologramLocation);
            for (val line : RankingValue.get(RankingValue::hologramDefaultLines)) {
                if (line.equalsIgnoreCase("@players")) {
                    playerLines.forEach(cmiHologram::addLine);
                } else cmiHologram.addLine(line);
            }

            CMI.getInstance().getHologramManager().addHologram(cmiHologram);
            cmiHologram.update();

            HOLOGRAMS.add("NextEconomy");

        }
    }

    public List<String> playersLines(Collection<SimpleAccount> accounts) {

        val lines = new ArrayList<String>();
        val line = RankingValue.get(RankingValue::hologramDefaultLine);

        var position = 1;
        for (val account : accounts) {
            if (position > RankingValue.get(RankingValue::hologramDefaultLimit)) break;

            val group = plugin.getGroupWrapperManager().getGroup(account.getUsername());
            lines.add(line
                    .replace("$position", String.valueOf(position))
                    .replace("$player", account.getUsername())
                    .replace("$prefix", group.getPrefix())
                    .replace("$suffix", group.getSuffix())
                    .replace("$amount", account.getBalanceFormated())
            );

            ++position;
        }

        return lines;

    }
}
