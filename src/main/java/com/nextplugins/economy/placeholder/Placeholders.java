package com.nextplugins.economy.placeholder;

public final class Placeholders {

    private Placeholders() {}

    public static void register() {
        new EconomyPlaceholder().register();
        new StockExchangePlaceholder().register();
    }

}
