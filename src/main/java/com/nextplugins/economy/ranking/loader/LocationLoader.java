package com.nextplugins.economy.ranking.loader;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.ranking.manager.LocationManager;
import com.nextplugins.economy.ranking.util.LocationUtil;
import lombok.Data;
import org.bukkit.Location;

import java.util.List;

@Data(staticConstructor = "of")
public final class LocationLoader {

    private final NextEconomy plugin;
    private final LocationManager locationManager;

    public void loadLocations() {
        List<String> locations = plugin.getNpcConfig().getStringList("npc.locations");
        if (locations == null || locations.isEmpty()) {
            plugin.getLogger().info("Não foi encontrado nenhuma localização para gerar os NPCs!");
            return;
        }

        for (String line : locations) {
            int position = Integer.parseInt(line.split(" ")[0]);
            Location location = LocationUtil.byStringNoBlock(line.split(" ")[1].split(","));

            locationManager.getLocationMap().put(position, location);
        }

        plugin.getLogger().info(
                "Foram carregados " + locationManager.getLocationMap().size() + " posições no ranking de npc!"
        );
    }

}
