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
        val logger = plugin.getLogger();
        val configuration = plugin.getNpcConfig();

        if (!configuration.contains("npc.locations")) return;

        val locations = configuration.getStringList("npc.locations");
        if (locations.isEmpty()) {
            logger.info("Não foi encontrado nenhuma localização para gerar os NPCs!");
            return;
        }

        for (val line : locations) {
            val position = Integer.parseInt(line.split(" ")[0]);
            val location = LocationUtil.byStringNoBlock(line.split(" ")[1].split(","));

            if (location.getWorld() == null) {
                logger.severe("A localização do NPC " + position + " está inválida!");

                continue;
            }

            locationManager.getLocationMap().put(position, location);
        }

        logger.info(
                "Foram carregados " + locationManager.getLocationMap().size() + " posições no ranking de NPC!"
        );
    }

}
