package com.nextplugins.economy.listener.events.operation;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.event.operations.MoneyGiveEvent;
import com.nextplugins.economy.configuration.MessageValue;
import com.nextplugins.economy.model.account.storage.AccountStorage;
import com.nextplugins.economy.model.account.transaction.Transaction;
import com.nextplugins.economy.model.account.transaction.TransactionType;
import com.nextplugins.economy.util.NumberUtils;
import lombok.val;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public final class MoneyGiveListener implements Listener {

    private final AccountStorage accountStorage = NextEconomy.getInstance().getAccountStorage();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeposit(MoneyGiveEvent event) {
        if (event.isCancelled()) return;

        val sender = event.getSender();
        val target = event.getTarget();
        val amount = event.getAmountBeforePurse();

        val targetAccount = accountStorage.findAccount(target);
        if (targetAccount == null) {
            sender.sendMessage(MessageValue.get(MessageValue::invalidTarget));
            return;
        }

        val response = targetAccount.createTransaction(
                Transaction.builder()
                        .player(target.isOnline() ? target.getPlayer() : null)
                        .amount(event.getAmount())
                        .amountWithoutPurse(event.getAmountBeforePurse())
                        .transactionType(TransactionType.DEPOSIT)
                        .build()
        );

        if (!response.transactionSuccess()) {
            sender.sendMessage(MessageValue.get(MessageValue::invalidMoney));
            return;
        }

        sender.sendMessage(MessageValue.get(MessageValue::addAmount)
                .replace("$player", target.getName())
                .replace("$amount", NumberUtils.format(amount))
        );
    }

}
