package com.nextplugins.economy.api.event.operations;

import com.nextplugins.economy.api.event.EconomyEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;

@Data
@EqualsAndHashCode(callSuper = true)
public final class MoneyWithdrawEvent extends EconomyEvent implements Cancellable {

    private final CommandSender sender;
    private final OfflinePlayer target;
    private final double amount;
    private boolean cancelled;

}
