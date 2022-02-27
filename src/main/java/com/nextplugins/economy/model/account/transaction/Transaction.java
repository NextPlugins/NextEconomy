package com.nextplugins.economy.model.account.transaction;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Builder
@Data
@Accessors(fluent = true)
public class Transaction {

    @Nullable private final Player player;
    @Nullable private final String owner;
    private final double amount;
    private final double amountWithoutPurse;
    @NotNull private final TransactionType transactionType;

}
