package com.nextplugins.economy.model.ranking;

import org.bukkit.Location;

import java.util.List;

/**
 * @author <a href="https://github.com/Yuhtin">Yuhtin</a>
 */
public interface IHologramHolder {

    /**
     * Destroy all holograms from the holder
     *
     * @param holograms the holograms to be destroyed
     */
    void destroyHolograms(List<String> holograms);

    /**
     * Create a hologram
     *
     * @param location where the hologram will be created
     * @param lines the lines of the hologram
     * @return the hologram id
     */
    String createHologram(Location location, List<String> lines);

}
