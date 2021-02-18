package com.nextplugins.economy.api.event.operations;

import com.nextplugins.economy.api.event.EconomyEvent;
import com.nextplugins.economy.api.model.Account;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public final class EconomyRankingUpdateEvent extends EconomyEvent {

    private final Set<Account> accountList;
    private final Instant updateInstant;

}
