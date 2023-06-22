package com.nextplugins.economy.model.ranking.impl;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Holograms.CMIHologram;
import com.Zrips.CMI.Modules.Holograms.HologramManager;
import com.nextplugins.economy.model.ranking.IHologramHolder;
import org.bukkit.Location;

import java.util.List;
import java.util.Random;

/**
 * @author <a href="https://github.com/Yuhtin">Yuhtin</a>
 */
public class CMIHolder implements IHologramHolder {

    @Override
    public void destroyHolograms(List<String> holograms) {
        HologramManager hologramManager = CMI.getInstance().getHologramManager();
        if (holograms == null) {
            for (CMIHologram hologram : hologramManager.getHolograms().values()) {
                if (hologram.getName().startsWith("NextEconomy")) {
                    hologramManager.removeHolo(hologram);
                }
            }

            return;
        }

        for (String hologram : holograms) {
            CMIHologram cmiHologram = hologramManager.getHolograms().get(hologram);
            if (cmiHologram == null) return;

            CMI.getInstance().getHologramManager().removeHolo(cmiHologram);
        }
    }

    @Override
    public String createHologram(Location location, List<String> lines) {
        CMIHologram cmiHologram = new CMIHologram("NextEconomy-" + new Random().nextInt(1000), location);
        lines.forEach(cmiHologram::addLine);

        CMI.getInstance().getHologramManager().addHologram(cmiHologram);
        cmiHologram.update();

        return cmiHologram.getName();
    }

}
