package com.nextplugins.economy.ranking.manager;

import com.google.common.collect.Maps;
import lombok.Getter;
import org.bukkit.Location;

import java.util.Map;

public final class LocationManager {

    @Getter private final Map<Integer, Location> locationMap;

    public LocationManager() {
        locationMap = Maps.newLinkedHashMap();
    }

    public Location getLocation(int position) {
        return locationMap.getOrDefault(position, null);
    }

}
