package com.nextplugins.economy.model.ranking.impl;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.model.ranking.IHologramHolder;
import org.bukkit.Location;

import java.util.List;

/**
 * @author <a href="https://github.com/Yuhtin">Yuhtin</a>
 */
public class HolographicDisplaysHolder implements IHologramHolder {

    @Override
    public void destroyHolograms(List<String> holograms) {
        HologramsAPI.getHolograms(NextEconomy.getInstance()).forEach(Hologram::delete);
    }

    @Override
    public String createHologram(Location location, List<String> lines) {
        Hologram hologram = HologramsAPI.createHologram(NextEconomy.getInstance(), location);
        lines.forEach(hologram::appendTextLine);

        return "NextEconomy";
    }

}
