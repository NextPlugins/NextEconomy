package com.nextplugins.economy.api.model.account;

import com.google.common.collect.Lists;
import com.nextplugins.economy.api.event.operations.MoneyChangeEvent;
import com.nextplugins.economy.api.model.account.historic.AccountBankHistoric;
import com.nextplugins.economy.api.model.account.transaction.TransactionType;
import com.nextplugins.economy.configuration.FeatureValue;
import com.nextplugins.economy.util.NumberUtils;
import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;

@Data
@Builder(builderMethodName = "generate", buildMethodName = "result")
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {

    private final String username;
    private String discordName;

    private double balance;
    private double movimentedBalance;

    private int transactionsQuantity;
    @Builder.Default private LinkedList<AccountBankHistoric> transactions = Lists.newLinkedList();

    @Builder.Default private long discordId = -1L;
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
     * @deprecated Since 2.0.0
     *
     * @param name of player
     * @param balance start balance
     * @param movimentedBalance balance used
     * @param transactionsQuantity performed
     * @param transactions info
     * @return a new {@link Account}
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
                0, true
        );

    }

    public synchronized double getBalance() {
        return this.balance;
    }

    public synchronized void setBalance(double quantity) {
        this.balance = quantity;
    }

    public synchronized void deposit(double quantity) {
        this.balance += quantity;
    }

    public synchronized void createTransaction(@Nullable Player player,
                                               @Nullable String owner,
                                               double quantity,
                                               @NotNull TransactionType transactionType) {

        var amount = quantity;
        if (amount < 1 || Double.isNaN(amount) || Double.isInfinite(amount)) return;

        if (transactionType == TransactionType.WITHDRAW) {

            movimentedBalance += amount;
            amount *= -1;

        }

        deposit(amount);
        if (this.balance < 0) this.balance = 0;

        if (owner != null) {

            ++transactionsQuantity;

            val historic = AccountBankHistoric.builder()
                    .target(owner)
                    .amount(amount < 0 ? amount * -1 : amount)
                    .type(transactionType)
                    .build();

            if (transactions.size() >= 56) transactions.remove(0);
            transactions.add(historic);

        }

        if (player == null) return;

        val moneyChangeEvent = new MoneyChangeEvent(
                player,
                this,
                balance,
                NumberUtils.format(balance)
        );

        Bukkit.getPluginManager().callEvent(moneyChangeEvent);

    }

    public synchronized boolean hasAmount(double amount) {
        return this.balance >= amount;
    }

}
