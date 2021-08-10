package com.nextplugins.economy.api.event.operations;

import com.nextplugins.economy.api.event.EconomyEvent;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;

@Getter
@Setter
public final class MoneyGiveEvent extends EconomyEvent implements Cancellable {

    private final CommandSender sender;
    private final OfflinePlayer target;
    private final double amount;
    private boolean cancelled;

    public MoneyGiveEvent(CommandSender sender, OfflinePlayer target, double amount) {
        super(false);
        this.sender = sender;
        this.target = target;
        this.amount = amount;
    }
}
