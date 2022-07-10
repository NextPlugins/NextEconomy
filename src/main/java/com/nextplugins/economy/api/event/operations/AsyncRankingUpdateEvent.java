package com.nextplugins.economy.api.event.operations;

import com.nextplugins.economy.api.event.EconomyEvent;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;

import java.time.Instant;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
@Getter
@Setter
public final class AsyncRankingUpdateEvent extends EconomyEvent implements Cancellable {

    private final Instant instant = Instant.now();
    private boolean cancelled;

    public AsyncRankingUpdateEvent() {
        super(true);
    }

}
