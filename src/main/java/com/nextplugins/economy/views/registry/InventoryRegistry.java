package com.nextplugins.economy.views.registry;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.views.BankView;
import com.nextplugins.economy.views.HistoricBankView;
import com.nextplugins.economy.views.RankingView;
import lombok.Getter;
import lombok.Setter;

@Getter
public final class InventoryRegistry {

    @Getter private static final InventoryRegistry instance = new InventoryRegistry();

    private BankView bankView;
    private RankingView rankingView;
    private HistoricBankView historicBankView;
    @Setter private NextEconomy plugin;

    public static InventoryRegistry of(NextEconomy plugin) {

        instance.setPlugin(plugin);
        return instance;

    }

    public void register() {

        this.historicBankView = new HistoricBankView(plugin.getAccountStorage()).init();
        this.bankView = new BankView(plugin.getAccountStorage()).init();
        this.rankingView = new RankingView().init();

    }

}
