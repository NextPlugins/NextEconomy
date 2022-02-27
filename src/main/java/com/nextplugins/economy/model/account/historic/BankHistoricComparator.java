package com.nextplugins.economy.model.account.historic;

import java.util.Comparator;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class BankHistoricComparator implements Comparator<AccountBankHistoric> {

    @Override
    public int compare(AccountBankHistoric o1, AccountBankHistoric o2) {
        return Long.compare(o2.getMilli(), o1.getMilli());
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }
}
