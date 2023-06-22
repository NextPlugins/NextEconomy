package com.nextplugins.economy.ranking.types;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.group.Group;
import com.nextplugins.economy.configuration.RankingValue;
import com.nextplugins.economy.model.account.SimpleAccount;
import com.nextplugins.economy.model.ranking.HologramSupportType;
import com.nextplugins.economy.ranking.manager.LocationManager;
import com.nextplugins.economy.ranking.storage.RankingStorage;
import lombok.RequiredArgsConstructor;
import org.bukkit.Chunk;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.nextplugins.economy.util.ColorUtil.colored;

/**
 * @author <a href="https://github.com/Yuhtin">Yuhtin</a>
 */

@RequiredArgsConstructor
public final class HologramRunnable implements Runnable {

    private static final List<String> HOLOGRAMS = new ArrayList<>();

    private final NextEconomy plugin;
    private final LocationManager locationManager;
    private final RankingStorage rankingStorage;

    private final HologramSupportType hologramAPI;

    @Override
    public void run() {
        hologramAPI.getHolder().destroyHolograms(HOLOGRAMS);

        if (locationManager.getLocationMap().isEmpty()) return;

        Location location = locationManager.getLocation(1);
        if (location == null || location.getWorld() == null) {
            plugin.getLogger().warning("A localização 1 do ranking é inválida.");
            return;
        }

        location.add(0, 5, 0);

        Chunk chunk = location.getChunk();
        if (!chunk.isLoaded()) chunk.load(true);

        Collection<SimpleAccount> accounts = rankingStorage.getRankByCoin().values();

        List<String> hologramLines = new ArrayList<>();
        for (String line : RankingValue.get(RankingValue::hologramDefaultLines)) {
            if (line.equalsIgnoreCase("@players")) {
                hologramLines.addAll(playersLines(accounts));
            } else {
                hologramLines.add(colored(line));
            }
        }

        HOLOGRAMS.add(hologramAPI.getHolder().createHologram(location, hologramLines));
    }

    public List<String> playersLines(Collection<SimpleAccount> accounts) {
        List<String> lines = new ArrayList<>();
        String line = RankingValue.get(RankingValue::hologramDefaultLine);

        int position = 1;
        for (SimpleAccount account : accounts) {
            if (position > RankingValue.get(RankingValue::hologramDefaultLimit)) break;

            Group group = plugin.getGroupWrapperManager().getGroup(account.getUsername());
            lines.add(colored(line
                    .replace("$position", String.valueOf(position))
                    .replace("$player", account.getUsername())
                    .replace("$prefix", group.getPrefix())
                    .replace("$suffix", group.getSuffix())
                    .replace("$amount", account.getBalanceFormated())
            ));

            ++position;
        }

        return lines;
    }
}
