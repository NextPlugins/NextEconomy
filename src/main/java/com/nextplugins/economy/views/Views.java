package com.nextplugins.economy.views;

import com.nextplugins.economy.NextEconomy;
import lombok.Getter;

@Getter
public final class Views {

    @Getter private static final Views instance = new Views();

    private final BankView bankView;
    private final RankingView rankingView;
    private final HistoricBankView historicBankView;

    private Views() {
        final NextEconomy plugin = NextEconomy.getInstance();

        this.historicBankView = new HistoricBankView(plugin.getAccountStorage()).init();
        this.bankView = new BankView(plugin.getAccountStorage()).init();
        this.rankingView = new RankingView().init();
    }

}
