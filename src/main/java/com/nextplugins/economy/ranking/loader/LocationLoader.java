package com.nextplugins.economy.ranking.loader;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.ranking.manager.LocationManager;
import com.nextplugins.economy.ranking.util.LocationUtil;
import lombok.Data;
import lombok.val;

@Data(staticConstructor = "of")
public final class LocationLoader {

    private final NextEconomy plugin;
    private final LocationManager locationManager;

    public void loadLocations() {
        if (!plugin.getNpcConfig().contains("npc.locations")) return;

        val locations = plugin.getNpcConfig().getStringList("npc.locations");
        if (locations.isEmpty()) {
            plugin.getLogger().info("Não foi encontrado nenhuma localização para gerar os NPCs!");
            return;
        }

        for (val line : locations) {
            val position = Integer.parseInt(line.split(" ")[0]);
            val location = LocationUtil.byStringNoBlock(line.split(" ")[1].split(","));

            locationManager.getLocationMap().put(position, location);
        }

        plugin.getLogger().info(
                "Foram carregados " + locationManager.getLocationMap().size() + " posições no ranking de npc!"
        );
    }

}
