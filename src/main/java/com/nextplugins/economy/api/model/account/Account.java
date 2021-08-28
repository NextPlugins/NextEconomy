package com.nextplugins.economy.api.model.account;

import com.google.common.collect.Lists;
import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.event.operations.MoneyChangeEvent;
import com.nextplugins.economy.api.model.account.historic.AccountBankHistoric;
import com.nextplugins.economy.api.model.account.transaction.TransactionType;
import com.nextplugins.economy.configuration.FeatureValue;
import com.nextplugins.economy.configuration.MessageValue;
import com.nextplugins.economy.util.DiscordSyncUtil;
import com.nextplugins.economy.util.ListSerializerHelper;
import com.nextplugins.economy.util.NumberUtils;
import lombok.*;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;

@Data
@Builder(builderMethodName = "generate", buildMethodName = "result")
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {

    private static final ListSerializerHelper<AccountBankHistoric> PARSER = new ListSerializerHelper<>();

    private final String username;
    private String discordName;

    private double balance;
    private double movimentedBalance;

    private int transactionsQuantity;
    @Builder.Default private transient LinkedList<AccountBankHistoric> transactions = Lists.newLinkedList();

    @Builder.Default private boolean receiveCoins = true;

    public static Account createDefault(String name) {

        return Account.generate()
                .username(name)
                .balance(FeatureValue.get(FeatureValue::initialBalance))
                .result();

    }

    /**
     * Create account
     *
     * @param name                 of player
     * @param balance              start balance
     * @param movimentedBalance    balance used
     * @param transactionsQuantity performed
     * @param transactions         info
     * @return a new {@link Account}
     * @deprecated Since 2.0.0
     */
    @Deprecated
    public static Account create(String name,
                                 double balance,
                                 double movimentedBalance,
                                 int transactionsQuantity,
                                 LinkedList<AccountBankHistoric> transactions) {

        return new Account(
                name,
                "Nenhum configurado",
                balance,
                movimentedBalance,
                transactionsQuantity,
                transactions,
                true
        );

    }

    public String getDiscordName() {
        if (discordName == null) {
            discordName = DiscordSyncUtil.getUserTag(Bukkit.getOfflinePlayer(username));
        }

        return discordName;
    }

    public synchronized double getBalance() {
        return this.balance;
    }

    public synchronized String getBalanceFormated() {
        return NumberUtils.format(getBalance());
    }

    public synchronized void setBalance(double quantity) {

        if (NumberUtils.isInvalid(quantity)) return;
        this.balance = quantity;

    }

    public synchronized void deposit(double quantity) {

        if (NumberUtils.isInvalid(quantity)) return;
        this.balance += quantity;

    }

    public synchronized EconomyResponse createTransaction(@Nullable Player player,
                                                          @Nullable String owner,
                                                          double quantity,
                                                          double valueWithoutPurse,
                                                          @NotNull TransactionType transactionType) {

        if (NumberUtils.isInvalid(quantity)) {
            return new EconomyResponse(
                    quantity, balance,
                    EconomyResponse.ResponseType.FAILURE,
                    "O valor inserido é inválido."
            );
        }

        if (transactionType == TransactionType.WITHDRAW) {

            if (!hasAmount(quantity)) {

                return new EconomyResponse(
                        quantity, balance, EconomyResponse.ResponseType.FAILURE,
                        "Não foi possível terminar esta operação. " +
                                "(A conta requisitada não possui quantia suficiente para completar esta transação)."
                );

            }

            movimentedBalance += quantity;
            this.balance -= quantity;

        } else this.balance += quantity;
        if (this.balance < 0) this.balance = 0;

        if (owner != null) {

            ++transactionsQuantity;

            val historic = AccountBankHistoric.builder()
                    .target(owner)
                    .amount(quantity)
                    .type(transactionType)
                    .build();

            if (transactions.size() >= 56) transactions.remove(0);
            transactions.add(historic);

        }

        if (player != null) {

            val moneyChangeEvent = new MoneyChangeEvent(
                    player,
                    this,
                    balance,
                    NumberUtils.format(balance)
            );

            Bukkit.getScheduler().runTask(NextEconomy.getInstance(), () -> Bukkit.getPluginManager().callEvent(moneyChangeEvent));

            if (valueWithoutPurse > 0 && quantity != valueWithoutPurse) {

                String message;

                if (transactionType == TransactionType.WITHDRAW) {
                    if (quantity > valueWithoutPurse) message = MessageValue.get(MessageValue::purseSpendMore);
                    else message = MessageValue.get(MessageValue::purseSpendLess);
                } else {
                    if (quantity > valueWithoutPurse) message = MessageValue.get(MessageValue::purseReceiveMore);
                    else message = MessageValue.get(MessageValue::purseReceiveLess);
                }

                val value = quantity > valueWithoutPurse ? quantity - valueWithoutPurse : valueWithoutPurse - quantity;
                player.sendMessage(message.replace("$value", NumberUtils.format(value)));

            }

        }

        return new EconomyResponse(quantity, balance, EconomyResponse.ResponseType.SUCCESS, "Operação realizada com sucesso.");

    }

    public synchronized boolean hasAmount(double amount) {
        if (NumberUtils.isInvalid(amount)) return false;
        return this.balance >= amount;
    }

}
