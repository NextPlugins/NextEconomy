package com.nextplugins.economy.listener.events.check;

import com.nextplugins.economy.api.model.account.Account;
import com.nextplugins.economy.api.model.account.storage.AccountStorage;
import com.nextplugins.economy.api.model.account.transaction.TransactionType;
import com.nextplugins.economy.configuration.MessageValue;
import com.nextplugins.economy.util.NumberUtils;
import de.tr7zw.changeme.nbtapi.NBTItem;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public final class CheckInteractListener implements Listener {

    private final AccountStorage accountStorage;

    @EventHandler
    public void onCheckInteract(PlayerInteractEvent event) {

        val player = event.getPlayer();
        val item = event.getItem();

        if (item == null || item.getType() == Material.AIR) return;

        val nbtItem = new NBTItem(item);
        if (!nbtItem.hasKey("value")) return;

        val checkValue = nbtItem.getDouble("value");
        val totalValue = checkValue * item.getAmount();
        val account = accountStorage.findOnlineAccount(player);

        account.createTransaction(
                "Cheque",
                totalValue,
                TransactionType.DEPOSIT
        );

        player.setItemInHand(null);

        player.sendMessage(
                MessageValue.get(MessageValue::checkUsed)
                        .replace("$checkAmount", NumberUtils.format(item.getAmount()))
                        .replace("$checkValue", NumberUtils.format(checkValue))
                        .replace("$checkTotalValue", NumberUtils.format(totalValue))
        );
    }

}
