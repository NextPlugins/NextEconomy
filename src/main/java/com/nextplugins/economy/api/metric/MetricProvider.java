package com.nextplugins.economy.api.metric;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.configuration.PurseValue;
import lombok.Data;
import lombok.val;

@Data(staticConstructor = "of")
public final class MetricProvider {

    private final NextEconomy plugin;
    private MetricsConnector metricsConnector;

    public void register() {
        System.setProperty("bstats.relocatecheck", "false");

        val accountStorage = plugin.getAccountStorage();

        metricsConnector = new MetricsConnector(plugin, 10041);
        metricsConnector.addCustomChart(new MetricsConnector.SimplePie("purse", () -> String.valueOf(PurseValue.get(PurseValue::enable))));
        metricsConnector.addCustomChart(new MetricsConnector.SingleLineChart("deposit_transaction", accountStorage::getDepositCount));
        metricsConnector.addCustomChart(new MetricsConnector.SingleLineChart("withdraw_transaction", accountStorage::getWithdrawCount));
        metricsConnector.addCustomChart(new MetricsConnector.SingleLineChart("transactions", () -> accountStorage.getDepositCount() + accountStorage.getWithdrawCount()));

        plugin.getLogger().info("Métrica de uso habilitada com sucesso.");
    }

}
