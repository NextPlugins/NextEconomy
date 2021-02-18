package com.nextplugins.economy.metric;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.configuration.GeneralConfiguration;
import lombok.Data;
import org.bstats.bukkit.Metrics;

@Data(staticConstructor = "of")
public final class MetricProvider {

    private final NextEconomy plugin;

    private final int PLUGIN_ID = 10041;

    public void setup() {
        if (GeneralConfiguration.get(GeneralConfiguration::useBStats)) {
            new Metrics(plugin, PLUGIN_ID);
            plugin.getLogger().info("Integração com o bStats configurada com sucesso.");
        } else {
            plugin.getLogger().info("Você desabilitou o uso do bStats, portanto, não utilizaremos dele.");
        }
    }

}
