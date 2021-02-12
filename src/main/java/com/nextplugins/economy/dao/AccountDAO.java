package com.nextplugins.economy.dao;

import com.henryfabio.sqlprovider.executor.SQLExecutor;
import com.nextplugins.economy.api.model.Account;
import com.nextplugins.economy.dao.adapter.AccountAdapter;
import lombok.RequiredArgsConstructor;

import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
public final class AccountDAO {

    private final String TABLE = "cash_accounts";

    private final SQLExecutor sqlExecutor;

    public void createTable() {
        sqlExecutor.updateQuery("CREATE TABLE IF NOT EXISTS " + TABLE + "(" +
                "owner CHAR(36) NOT NULL PRIMARY KEY," +
                "balance DOUBLE NOT NULL" +
                ");"
        );
    }

    public Account selectOne(UUID owner) {
        return sqlExecutor.resultOneQuery(
                "SELECT * FROM " + TABLE + " WHERE owner = ?",
                statement -> statement.set(1, owner.toString()),
                AccountAdapter.class
        );
    }

    public Set<Account> selectAll() {
        return sqlExecutor.resultManyQuery(
                "SELECT * FROM " + TABLE,
                k -> {
                },
                AccountAdapter.class
        );
    }

    public Set<Account> selectAll(String query) {
        return sqlExecutor.resultManyQuery(
                "SELECT * FROM " + TABLE + " " + query,
                k -> {
                },
                AccountAdapter.class
        );
    }

    public void insertOne(Account account) {
        sqlExecutor.updateQuery(
                "INSERT INTO " + TABLE + " VALUES(?,?);",
                statement -> {
                    statement.set(1, account.getOwner().toString());
                    statement.set(2, account.getBalance());
                }
        );
    }

    public void saveOne(Account account) {
        sqlExecutor.updateQuery(
                "UPDATE " + TABLE + " SET balance = ? WHERE owner = ?",
                statement -> {
                    statement.set(1, account.getBalance());
                    statement.set(2, account.getOwner().toString());
                }
        );
    }

    public void deleteOne(Account account) {
        sqlExecutor.updateQuery(
                "DELETE FROM " + TABLE + " WHERE owner = '" + account.getOwner().toString() + "'"
        );
    }

}
