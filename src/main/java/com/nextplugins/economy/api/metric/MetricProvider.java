package com.nextplugins.economy.api.metric;

import com.nextplugins.economy.NextEconomy;
import lombok.Data;
import org.bstats.bukkit.Metrics;

@Data(staticConstructor = "of")
public final class MetricProvider {

    private final NextEconomy plugin;

    private final int PLUGIN_ID = 10041;

    public void register() {

        // bstats with pdm (without relocate)
        System.setProperty("bstats.relocatecheck", "false");

        new Metrics(plugin, PLUGIN_ID);
        plugin.getLogger().info("Métrica de uso habilitada com sucesso.");

    }

}
