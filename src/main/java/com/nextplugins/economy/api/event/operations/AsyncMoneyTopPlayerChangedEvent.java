package com.nextplugins.economy.api.event.operations;

import com.nextplugins.economy.api.event.EconomyEvent;
import com.nextplugins.economy.api.model.account.SimpleAccount;
import lombok.*;
import org.bukkit.event.Cancellable;

import java.time.Instant;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Getter
@Setter
public final class AsyncMoneyTopPlayerChangedEvent extends EconomyEvent implements Cancellable {

    private final SimpleAccount lastMoneyTop;
    private final SimpleAccount moneyTop;

    private final Instant updateInstant = Instant.now();
    private boolean cancelled;

    public AsyncMoneyTopPlayerChangedEvent(SimpleAccount lastMoneyTop, SimpleAccount moneyTop) {
        super(true);
        this.lastMoneyTop = lastMoneyTop;
        this.moneyTop = moneyTop;
    }
}
