package com.nextplugins.economy.api.event.transaction;

import com.nextplugins.economy.api.event.EconomyEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@Data
@EqualsAndHashCode(callSuper = true)
public final class TransactionCompletedEvent extends EconomyEvent {

    private final Player player;
    private final OfflinePlayer target;
    private final double amount;

}
