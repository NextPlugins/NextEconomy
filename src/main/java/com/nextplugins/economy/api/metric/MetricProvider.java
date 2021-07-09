package com.nextplugins.economy.api.metric;

import com.nextplugins.economy.NextEconomy;
import lombok.Data;

@Data(staticConstructor = "of")
public final class MetricProvider {

    private final NextEconomy plugin;

    public void register() {

        System.setProperty("bstats.relocatecheck", "false");

        new MetricsConnector(plugin, 10041);
        plugin.getLogger().info("Métrica de uso habilitada com sucesso.");

    }

}
