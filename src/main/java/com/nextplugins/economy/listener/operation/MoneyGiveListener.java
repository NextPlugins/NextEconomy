package com.nextplugins.economy.listener.operation;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.event.operations.MoneyGiveEvent;
import com.nextplugins.economy.api.model.Account;
import com.nextplugins.economy.configuration.values.MessageValue;
import com.nextplugins.economy.storage.AccountStorage;
import com.nextplugins.economy.util.NumberFormat;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class MoneyGiveListener implements Listener {

    protected final AccountStorage accountStorage = NextEconomy.getInstance().getAccountStorage();

    @EventHandler
    public void onDeposit(MoneyGiveEvent event) {
        CommandSender sender = event.getSender();
        Player target = event.getTarget();
        double amount = event.getAmount();

        Account targetAccount = accountStorage.getAccount(target.getUniqueId());

        targetAccount.depositAmount(amount);

        sender.sendMessage(MessageValue.get(MessageValue::addAmount)
                .replace("$player", Bukkit.getOfflinePlayer(targetAccount.getOwner()).getName())
                .replace("$amount", NumberFormat.format(amount))
        );
    }

}
