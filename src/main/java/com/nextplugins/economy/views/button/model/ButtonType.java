package com.nextplugins.economy.views.button.model;

import com.henryfabio.minecraft.inventoryapi.event.impl.CustomInventoryClickEvent;
import com.nextplugins.economy.NextEconomy;
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
    YOUR_MONEY(callback -> {

        val historicBankView = InventoryRegistry.getInstance().getHistoricBankView();
        historicBankView.openInventory(callback.getPlayer());

    }),

    SEND_MONEY(callback -> {

        val player = callback.getPlayer();
        player.closeInventory();

        val interaction = NextEconomy.getInstance().getInteractionRegistry().getSendMoneyInteractionManager();

        MessageValue.get(MessageValue::interactionInputPlayer).forEach(player::sendMessage);
        interaction.sendRequisition(player, false);

    }),

    VIEW_MONEY(callback -> {

        val player = callback.getPlayer();
        player.closeInventory();

        val interactionRegistry = NextEconomy.getInstance().getInteractionRegistry();
        if (interactionRegistry.getSendMoneyInteractionManager().isUsing(player)) player.chat("cancelar");

        val interaction = interactionRegistry.getViewPlayerInteractionManager();

        MessageValue.get(MessageValue::interactionInputPlayer).forEach(player::sendMessage);
        interaction.sendRequisition(player);

    }),

    TOP_MONEY(callback -> {

        val rankingInventory = InventoryRegistry.getInstance().getRankingView();
        rankingInventory.openInventory(callback.getPlayer());

    });

    @Getter private final Consumer<CustomInventoryClickEvent> action;

}
