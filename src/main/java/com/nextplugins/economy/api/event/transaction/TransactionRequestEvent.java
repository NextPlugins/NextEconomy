package com.nextplugins.economy.api.event.transaction;

import com.nextplugins.economy.api.event.EconomyEvent;
import com.nextplugins.economy.api.model.account.Account;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

@Data
@EqualsAndHashCode(callSuper = true)
public final class TransactionRequestEvent extends EconomyEvent implements Cancellable {

    private final Player player;
    private final OfflinePlayer target;
    private final Account account;
    private final double amount;
    private boolean cancelled;

}
