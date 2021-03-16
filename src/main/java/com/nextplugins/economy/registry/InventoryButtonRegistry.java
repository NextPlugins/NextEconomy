package com.nextplugins.economy.registry;

import com.google.common.collect.Maps;
import com.nextplugins.economy.configuration.values.InventoryValue;
import com.nextplugins.economy.inventory.button.InventoryButton;
import com.nextplugins.economy.parser.InventoryButtonParser;
import lombok.Getter;

import java.util.Map;

public final class InventoryButtonRegistry {

    @Getter private static final InventoryButtonRegistry instance = new InventoryButtonRegistry().init();

    private final Map<String, InventoryButton> inventoryButtonMap = Maps.newHashMap();
    private final InventoryButtonParser inventoryButtonParser = new InventoryButtonParser();

    public InventoryButtonRegistry init() {

        register("main.yourMoney", inventoryButtonParser.parse(
                InventoryValue.get(InventoryValue::mainInventoryMoneyButton)
        ));

        return this;

    }

    public void register(String id, InventoryButton inventoryButton) {
        inventoryButtonMap.put(id, inventoryButton);
    }

    public InventoryButton get(String id) {
        return inventoryButtonMap.get(id);
    }

}
