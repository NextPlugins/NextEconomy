package com.nextplugins.economy.listener.events.operation;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.event.operations.MoneyWithdrawEvent;
import com.nextplugins.economy.configuration.MessageValue;
import com.nextplugins.economy.model.account.storage.AccountStorage;
import com.nextplugins.economy.model.account.transaction.Transaction;
import com.nextplugins.economy.model.account.transaction.TransactionType;
import com.nextplugins.economy.util.NumberUtils;
import lombok.val;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public final class MoneyWithdrawListener implements Listener {

    private final AccountStorage accountStorage = NextEconomy.getInstance().getAccountStorage();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWithdraw(MoneyWithdrawEvent event) {
        if (event.isCancelled()) return;

        val sender = event.getSender();
        val target = event.getTarget();
        val amount = event.getAmount();

        val targetAccount = accountStorage.findAccount(target);
        if (targetAccount == null) {
            sender.sendMessage(MessageValue.get(MessageValue::invalidTarget));
            return;
        }

        val response = targetAccount.createTransaction(
                Transaction.builder()
                        .player(target.isOnline() ? target.getPlayer() : null)
                        .amount(amount)
                        .transactionType(TransactionType.WITHDRAW)
                        .build()
        );

        if (!response.transactionSuccess()) {
            sender.sendMessage(MessageValue.get(MessageValue::invalidMoney));
            return;
        }

        sender.sendMessage(MessageValue.get(MessageValue::removeAmount)
                .replace("$player", target.getName())
                .replace("$amount", NumberUtils.format(amount))
        );
    }

}
