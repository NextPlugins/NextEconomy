package com.nextplugins.economy.api.model.account.historic;

import com.nextplugins.economy.api.model.account.transaction.TransactionType;
import lombok.Builder;
import lombok.Data;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
@Data
@Builder
public final class AccountBankHistoric {

    private final String target;
    private final double amount;
    private final TransactionType type;

    @Builder.Default private final long milli = System.currentTimeMillis();

}
