package com.nextplugins.economy.listener.events.transaction;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.event.transaction.TransactionRequestEvent;
import com.nextplugins.economy.configuration.FeatureValue;
import com.nextplugins.economy.configuration.MessageValue;
import com.nextplugins.economy.model.account.storage.AccountStorage;
import com.nextplugins.economy.model.account.transaction.Transaction;
import com.nextplugins.economy.model.account.transaction.TransactionType;
import com.nextplugins.economy.util.NumberUtils;
import lombok.val;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public final class TransactionRequestListener implements Listener {

    private final AccountStorage accountStorage = NextEconomy.getInstance().getAccountStorage();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onRequest(TransactionRequestEvent event) {
        if (event.isCancelled()) return;

        val player = event.getPlayer();
        val target = event.getTarget();
        val targetAccount = event.getAccount();
        val amount = event.getAmount();

        if (target.getUniqueId().compareTo(player.getUniqueId()) == 0) {
            player.sendMessage(MessageValue.get(MessageValue::isYourself));

            event.setCancelled(true);

            return;
        }

        val account = accountStorage.findAccount(player);
        if (NumberUtils.isInvalid(amount)) {
            player.sendMessage(MessageValue.get(MessageValue::invalidMoney));

            event.setCancelled(true);

            return;
        }

        val minValue = FeatureValue.get(FeatureValue::minTransactionValue);
        if (amount < minValue) {
            player.sendMessage(MessageValue.get(MessageValue::minValueNecessary)
                    .replace("$amount", NumberUtils.format(minValue))
            );

            event.setCancelled(true);

            return;
        }

        if (!account.hasAmount(amount)) {
            player.sendMessage(MessageValue.get(MessageValue::insufficientAmount));

            event.setCancelled(true);

            return;
        }

        targetAccount.createTransaction(
                Transaction.builder()
                        .player(target.isOnline() ? target.getPlayer() : null)
                        .owner(player.getName())
                        .amount(event.getAmount())
                        .transactionType(TransactionType.DEPOSIT)
                        .build()
        );

        account.createTransaction(
                Transaction.builder()
                        .player(player)
                        .owner(target.getName())
                        .amount(event.getAmount())
                        .transactionType(TransactionType.WITHDRAW)
                        .build()
        );

        player.sendMessage(
                MessageValue.get(MessageValue::paid).replace("$player", target.getName())
                        .replace("$amount", NumberUtils.format(amount))
        );

        if (target.isOnline()) {
            target.getPlayer().sendMessage(
                    MessageValue.get(MessageValue::received).replace("$player", player.getName())
                            .replace("$amount", NumberUtils.format(amount))
            );
        }
    }

}
