package com.nextplugins.economy.util;

import com.nextplugins.economy.api.model.account.historic.AccountBankHistoric;
import com.nextplugins.economy.api.model.account.transaction.TransactionType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class BankHistoricParserUtil {

    public static String parse(List<AccountBankHistoric> historic) {
        if (historic.isEmpty()) return "";

        val stringBuilder = new StringBuilder();
        for (val accountBankHistoric : historic) {
            stringBuilder.append(accountBankHistoric.getTarget())
                    .append("-")
                    .append(accountBankHistoric.getAmount())
                    .append("-")
                    .append(accountBankHistoric.getType())
                    .append("-")
                    .append(accountBankHistoric.getMilli())
                    .append("@");
        }

        return stringBuilder.toString();
    }

    public static LinkedList<AccountBankHistoric> unparse(String data) {
        if (!data.contains("@")) return new LinkedList<>();

        val accountBankHistorics = new LinkedList<AccountBankHistoric>();
        for (val parts : data.split("@")) {

            if (parts.equals("")) break;
            val values = parts.split("-");

            accountBankHistorics.add(
                    AccountBankHistoric.builder()
                            .target(values[0])
                            .amount(NumberUtils.parse(values[1]))
                            .type(TransactionType.valueOf(values[2]))
                            .milli(Long.parseLong(values[3]))
                            .build()
            );
        }

        return accountBankHistorics;
    }

}
