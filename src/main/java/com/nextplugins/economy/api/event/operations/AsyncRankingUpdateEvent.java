package com.nextplugins.economy.api.event.operations;

import com.nextplugins.economy.api.event.EconomyEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Cancellable;

import java.time.Instant;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class AsyncRankingUpdateEvent extends EconomyEvent implements Cancellable {

    private final long nextUpdate;

    private final Instant instant = Instant.now();
    private boolean cancelled = false;

    private final boolean async = true;

}
