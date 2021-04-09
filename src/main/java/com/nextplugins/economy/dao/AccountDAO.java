package com.nextplugins.economy.dao;

import com.henryfabio.sqlprovider.executor.SQLExecutor;
import com.nextplugins.economy.api.model.account.Account;
import com.nextplugins.economy.api.model.account.old.OldAccount;
import com.nextplugins.economy.api.model.account.old.adapter.OldAccountAdapter;
import com.nextplugins.economy.dao.adapter.AccountAdapter;
import lombok.RequiredArgsConstructor;

import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
public final class AccountDAO {

    private final String TABLE = "nexteconomy_data";

    private final SQLExecutor sqlExecutor;

    public void createTable() {
        sqlExecutor.updateQuery("CREATE TABLE IF NOT EXISTS " + TABLE + "(" +
                "owner CHAR(16) NOT NULL PRIMARY KEY," +
                "balance DOUBLE NOT NULL" +
                ");"
        );
    }

    public Account selectOne(String owner) {
        return sqlExecutor.resultOneQuery(
                "SELECT * FROM " + TABLE + " WHERE owner = ?",
                statement -> statement.set(1, owner),
                AccountAdapter.class
        );
    }

    public Set<OldAccount> selectAllOld() {
        return sqlExecutor.resultManyQuery(
                "SELECT * FROM economy_data",
                k -> {
                },
                OldAccountAdapter.class
        );
    }

    public Set<Account> selectAll() {
        return selectAll("");
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
                String.format("REPLACE INTO %s VALUES(?,?)", TABLE),
                statement -> {

                    statement.set(1, account.getUserName());
                    statement.set(2, account.getBalance());

                }
        );

    }

    public void deleteOldByUUID(UUID uuid) {
        sqlExecutor.updateQuery(
                "DELETE FROM economy_data WHERE owner = '" + uuid.toString() + "'"
        );
    }

    public void deleteOne(Account account) {
        sqlExecutor.updateQuery(
                "DELETE FROM " + TABLE + " WHERE owner = '" + account.getUserName() + "'"
        );
    }

}
