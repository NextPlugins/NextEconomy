package com.nextplugins.economy.api.event.operations;

import com.nextplugins.economy.api.event.EconomyEvent;
import com.nextplugins.economy.api.model.account.Account;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.event.Cancellable;

import java.time.Instant;
import java.util.List;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
@Data
@EqualsAndHashCode(callSuper = true)
public final class AsyncRankingUpdateEvent extends EconomyEvent implements Cancellable {

    private final List<Account> oldTopAccounts;
    private final List<Account> topAccounts;

    private final Instant instant;
    private final long nextUpdate;
    private boolean cancelled;

    private final boolean async = true;

}
