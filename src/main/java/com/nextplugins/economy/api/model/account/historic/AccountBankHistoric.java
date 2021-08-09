package com.nextplugins.economy.api.model.account.historic;

import com.nextplugins.economy.api.model.account.transaction.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.util.Comparator;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
@Data
@Builder
public final class AccountBankHistoric implements Comparator<AccountBankHistoric> {

    private final String target;
    private final double amount;
    private final TransactionType type;

    @Builder.Default private final long milli = System.currentTimeMillis();

    @Override
    public int compare(AccountBankHistoric o1, AccountBankHistoric o2) {
        return Long.compare(o1.milli, o2.milli);
    }
}
