package com.nextplugins.economy.views.button.model;

import com.henryfabio.minecraft.inventoryapi.event.impl.CustomInventoryClickEvent;
import com.nextplugins.economy.configuration.values.MessageValue;
import com.nextplugins.economy.registry.InteractionRegistry;
import com.nextplugins.economy.registry.InventoryRegistry;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.function.Consumer;

@RequiredArgsConstructor
public enum ButtonType {

    PURSE(callback -> {}),
    YOUR_MONEY(callback -> {}),

    SEND_MONEY(callback -> {

        val player = callback.getPlayer();
        val interaction = InteractionRegistry.getInstance().getSendMoneyInteractionManager();

        MessageValue.get(MessageValue::interactionInputPlayer).forEach(player::sendMessage);
        interaction.sendRequisition(player);

    }),

    VIEW_MONEY(callback -> {

        val player = callback.getPlayer();
        val interaction = InteractionRegistry.getInstance().getViewPlayerInteractionManager();

        MessageValue.get(MessageValue::interactionInputPlayer).forEach(player::sendMessage);
        interaction.sendRequisition(player);

    }),

    TOP_MONEY(callback -> {

        val rankingInventory = InventoryRegistry.getInstance().getRankingView();
        rankingInventory.openInventory(callback.getPlayer());

    });

    @Getter private final Consumer<CustomInventoryClickEvent> action;

}
