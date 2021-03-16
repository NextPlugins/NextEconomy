package com.nextplugins.economy.registry;

import com.nextplugins.economy.inventory.MainInventory;
import com.nextplugins.economy.inventory.RankingInventory;
import lombok.Getter;

@Getter
public final class InventoryRegistry {

    @Getter private static final InventoryRegistry instance = new InventoryRegistry().init();

    private MainInventory mainInventory;
    private RankingInventory rankingInventory;

    public InventoryRegistry init() {

        this.mainInventory = new MainInventory().init();
        this.rankingInventory = new RankingInventory().init();

        return this;
    }

}
