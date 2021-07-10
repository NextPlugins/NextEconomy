package com.nextplugins.economy.api.model.account.historic;

import com.nextplugins.economy.api.model.account.transaction.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
@Data
@Builder
public final class AccountBankHistoric implements Serializable {

    private final String target;
    private final double amount;
    private final TransactionType type;

    @Builder.Default private final long milli = System.currentTimeMillis();

}
