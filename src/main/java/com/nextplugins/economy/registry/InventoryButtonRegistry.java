package com.nextplugins.economy.registry;

import com.nextplugins.economy.configuration.values.InventoryValue;
import com.nextplugins.economy.views.button.InventoryButton;
import com.nextplugins.economy.parser.InventoryButtonParser;
import com.nextplugins.economy.util.CaseInsensitiveLinkedMap;
import lombok.Getter;

import java.util.Collection;

public final class InventoryButtonRegistry {

    @Getter private static final InventoryButtonRegistry instance = new InventoryButtonRegistry().init();

    private final CaseInsensitiveLinkedMap<InventoryButton> inventoryButtonMap = CaseInsensitiveLinkedMap.newMap();
    private final InventoryButtonParser inventoryButtonParser = new InventoryButtonParser();

    public InventoryButtonRegistry init() {

        register("yourMoney", inventoryButtonParser.parse(
                InventoryValue.get(InventoryValue::yourMoneyButton)
        ));

        register("viewPlayerMoney", inventoryButtonParser.parse(
                InventoryValue.get(InventoryValue::viewPlayerMoneyButton)
        ));

        register("sendMoney", inventoryButtonParser.parse(
                InventoryValue.get(InventoryValue::sendMoneyButton)
        ));

        register("purse", inventoryButtonParser.parse(
                InventoryValue.get(InventoryValue::purseButton)
        ));

        register("topMoney", inventoryButtonParser.parse(
                InventoryValue.get(InventoryValue::topMoneyButton)
        ));

        return this;

    }

    public void register(String id, InventoryButton inventoryButton) {
        inventoryButtonMap.put(id, inventoryButton);
    }

    public Collection<InventoryButton> values() {
        return inventoryButtonMap.values();
    }

}
