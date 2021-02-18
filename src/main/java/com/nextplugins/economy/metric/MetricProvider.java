package com.nextplugins.economy.metric;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.configuration.values.FeatureValue;
import lombok.Data;
import org.bstats.bukkit.Metrics;

@Data(staticConstructor = "of")
public final class MetricProvider {

    private final NextEconomy plugin;

    private final int PLUGIN_ID = 10041;

    public void setup() {
        new Metrics(plugin, PLUGIN_ID);
    }

}
