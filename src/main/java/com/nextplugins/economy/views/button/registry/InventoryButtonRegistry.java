package com.nextplugins.economy.views.button.registry;

import com.google.common.collect.Sets;
import com.nextplugins.economy.configuration.InventoryValue;
import com.nextplugins.economy.views.button.parser.InventoryButtonParser;
import com.nextplugins.economy.views.button.InventoryButton;
import lombok.Getter;

import java.util.Set;

public final class InventoryButtonRegistry {

    @Getter private static final InventoryButtonRegistry instance = new InventoryButtonRegistry().init();

    private final Set<InventoryButton> inventoryButtonMap = Sets.newHashSet();
    private final InventoryButtonParser inventoryButtonParser = new InventoryButtonParser();

    public InventoryButtonRegistry init() {

        register(inventoryButtonParser.parse(
                InventoryValue.get(InventoryValue::yourMoneyButton)
        ));

        register(inventoryButtonParser.parse(
                InventoryValue.get(InventoryValue::viewPlayerMoneyButton)
        ));

        register(inventoryButtonParser.parse(
                InventoryValue.get(InventoryValue::sendMoneyButton)
        ));

        register(inventoryButtonParser.parse(
                InventoryValue.get(InventoryValue::purseButton)
        ));

        register(inventoryButtonParser.parse(
                InventoryValue.get(InventoryValue::topMoneyButton)
        ));

        return this;

    }

    public void register(InventoryButton inventoryButton) {
        inventoryButtonMap.add(inventoryButton);
    }

    public Set<InventoryButton> values() {
        return inventoryButtonMap;
    }

}
