package com.nextplugins.economy.registry;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.views.BankView;
import com.nextplugins.economy.views.RankingView;
import lombok.Getter;

@Getter
public final class InventoryRegistry {

    @Getter private static final InventoryRegistry instance = new InventoryRegistry();

    private BankView bankView;
    private RankingView rankingView;

    public void init(NextEconomy plugin) {

        this.bankView = new BankView(plugin.getAccountStorage()).init();
        this.rankingView = new RankingView().init();

    }

}
