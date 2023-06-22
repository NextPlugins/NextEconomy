package com.nextplugins.economy.model.ranking.impl;

import com.nextplugins.economy.model.ranking.IHologramHolder;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.DecentHologramsAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.Location;

import java.util.List;
import java.util.Random;

/**
 * @author <a href="https://github.com/Yuhtin">Yuhtin</a>
 */
public class DecentHologramsHolder implements IHologramHolder {

    @Override
    public void destroyHolograms(List<String> holograms) {
        if (holograms == null) {
            for (Hologram hologram : DecentHologramsAPI.get().getHologramManager().getHolograms()) {
                if (hologram.getName().startsWith("NextEconomy")) {
                    DHAPI.removeHologram(hologram.getName());
                }
            }

            return;
        }

        holograms.forEach(DHAPI::removeHologram);
    }

    @Override
    public String createHologram(Location location, List<String> lines) {
        Hologram hologram = DHAPI.createHologram("NextEconomy-" + new Random().nextInt(1000), location, false, lines);
        return hologram.getName();
    }

}
