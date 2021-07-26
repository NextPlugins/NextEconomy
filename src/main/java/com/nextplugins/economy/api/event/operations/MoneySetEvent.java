package com.nextplugins.economy.api.event.operations;

import com.nextplugins.economy.api.event.EconomyEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;

@Getter
@Setter
public final class MoneySetEvent extends EconomyEvent implements Cancellable {

    private final CommandSender sender;
    private final OfflinePlayer target;
    private final double amount;
    private boolean cancelled;

    public MoneySetEvent(CommandSender sender, OfflinePlayer target, double amount) {
        super(false);
        this.sender = sender;
        this.target = target;
        this.amount = amount;
    }
}
