package com.nextplugins.economy.api.event.operations;

import com.nextplugins.economy.api.event.EconomyEvent;
import lombok.*;
import org.bukkit.event.Cancellable;

import java.time.Instant;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
@Getter
@Setter
public final class AsyncRankingUpdateEvent extends EconomyEvent implements Cancellable {

    private final long nextUpdate;

    private final Instant instant = Instant.now();
    private boolean cancelled;

    public AsyncRankingUpdateEvent(long nextUpdate) {
        super(true);
        this.nextUpdate = nextUpdate;
    }

}
