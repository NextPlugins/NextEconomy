package com.nextplugins.economy.dao.repository;

import com.henryfabio.sqlprovider.executor.SQLExecutor;
import com.nextplugins.economy.api.model.account.Account;
import com.nextplugins.economy.api.model.account.historic.AccountBankHistoric;
import com.nextplugins.economy.dao.repository.adapter.AccountAdapter;
import com.nextplugins.economy.util.LinkedListHelper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.LinkedList;
import java.util.Set;

@RequiredArgsConstructor
public final class AccountRepository {

    private static final String TABLE = "nexteconomy_data";

    @Getter private final SQLExecutor sqlExecutor;

    public void createTable() {

        sqlExecutor.updateQuery("CREATE TABLE IF NOT EXISTS " + TABLE + "(" +
                "owner CHAR(16) NOT NULL PRIMARY KEY," +
                "balance DOUBLE NOT NULL," +
                "movimentedBalance DOUBLE NOT NULL," +
                "transactionsQuantity INTEGER NOT NULL," +
                "transactions LONGTEXT NOT NULL," +
                "discordId LONG NOT NULL" +
                ");"
        );

        try {
            sqlExecutor.updateQuery("ALTER TABLE " + TABLE + " ADD COLUMN discordId LONG NOT NULL");
        } catch (Exception ignored) {
        }

    }

    private Account selectOneQuery(String query) {
        return sqlExecutor.resultOneQuery(
                "SELECT * FROM " + TABLE + " " + query,
                statement -> {
                },
                AccountAdapter.class
        );
    }

    public Account selectOne(String owner) {
        return selectOneQuery("WHERE owner = '" + owner + "'");
    }

    public Account selectByDiscord(long discord) {
        return selectOneQuery("WHERE discordId = '" + discord + "'");
    }

    public Set<Account> selectAll(String query) {
        return sqlExecutor.resultManyQuery(
                "SELECT * FROM " + TABLE + " " + query,
                k -> {
                },
                AccountAdapter.class
        );
    }

    public void saveOne(Account account) {

        this.sqlExecutor.updateQuery(
                String.format("REPLACE INTO %s VALUES(?,?,?,?,?,?)", TABLE),
                statement -> {

                    statement.set(1, account.getUserName());
                    statement.set(2, account.getBalance());
                    statement.set(3, account.getMovimentedBalance());
                    statement.set(4, account.getTransactionsQuantity());
                    statement.set(5, LinkedListHelper.toJson((LinkedList<AccountBankHistoric>) account.getTransactions()));
                    statement.set(6, account.getDiscordId());

                }
        );

    }

}
