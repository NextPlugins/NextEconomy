package com.nextplugins.economy;

import com.nextplugins.economy.configuration.StockExchangeValue;
import com.nextplugins.economy.util.MetricsConnector;
import com.nextplugins.economy.util.MetricsConnector.SimplePie;
import com.nextplugins.economy.util.MetricsConnector.SingleLineChart;
import com.nextplugins.economy.model.account.storage.AccountStorage;


public final class EconomyMetrics {

    private static final int PLUGIN_ID = 10041;

    private final NextEconomy plugin;
    private final MetricsConnector metricsConnector;
    private final AccountStorage accountStorage;

    public EconomyMetrics(NextEconomy plugin) {
        this.plugin = plugin;
        this.metricsConnector = new MetricsConnector(plugin, PLUGIN_ID);
        this.accountStorage = plugin.getAccountStorage();
    }

    public void init() {
        System.setProperty("bstats.relocatecheck", "false");

        final SimplePie stockExchangePie = new SimplePie(
                "stock_exchange",
                () -> String.valueOf(StockExchangeValue.get(StockExchangeValue::enable))
        );

        metricsConnector.addCustomChart(stockExchangePie);

        final int depositCount = accountStorage.getDepositCount();
        final int withdrawCount = accountStorage.getWithdrawCount();

        final SingleLineChart depositChart = new SingleLineChart("deposits", () -> depositCount);
        final SingleLineChart withdrawChat = new SingleLineChart("withdrawals", () -> withdrawCount);
        final SingleLineChart transactions = new SingleLineChart(
                "transactions",
                () -> (depositCount + withdrawCount)
        );

        metricsConnector.addCustomChart(depositChart);
        metricsConnector.addCustomChart(withdrawChat);
        metricsConnector.addCustomChart(transactions);

        plugin.getLogger().info("Métrica de uso habilitada com sucesso.");
    }

}
