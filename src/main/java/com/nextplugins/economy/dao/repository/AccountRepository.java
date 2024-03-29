package com.nextplugins.economy.dao.repository;

import com.henryfabio.sqlprovider.executor.SQLExecutor;
import com.nextplugins.economy.dao.repository.adapter.AccountAdapter;
import com.nextplugins.economy.dao.repository.adapter.SimpleAccountAdapter;
import com.nextplugins.economy.model.account.Account;
import com.nextplugins.economy.model.account.SimpleAccount;
import com.nextplugins.economy.util.BankHistoricParserUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@RequiredArgsConstructor
public final class AccountRepository {

    private static final String TABLE = "nexteconomy_data";

    @Getter
    private final SQLExecutor sqlExecutor;

    public void createTable() {
        sqlExecutor.updateQuery("CREATE TABLE IF NOT EXISTS " + TABLE + "(" +
                "owner CHAR(36) NOT NULL PRIMARY KEY," +
                "balance DOUBLE NOT NULL DEFAULT 0," +
                "movimentedBalance DOUBLE NOT NULL DEFAULT 0," +
                "transactionsQuantity INTEGER NOT NULL DEFAULT 0," +
                "transactions LONGTEXT NOT NULL," +
                "receiveCoins INTEGER NOT NULL DEFAULT 1" +
                ");"
        );
    }

    public void recreateTable() {
        sqlExecutor.updateQuery("DELETE FROM " + TABLE);
        createTable();
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

    public Set<Account> selectAll(String query) {
        return sqlExecutor.resultManyQuery(
                "SELECT * FROM " + TABLE + " " + query,
                k -> {
                },
                AccountAdapter.class
        );
    }

    public Set<SimpleAccount> selectSimpleAll(String query) {
        return sqlExecutor.resultManyQuery(
                "SELECT * FROM " + TABLE + " " + query,
                k -> {
                },
                SimpleAccountAdapter.class
        );
    }

    public void saveOne(Account account) {
        this.sqlExecutor.updateQuery(
                String.format("REPLACE INTO %s VALUES(?,?,?,?,?,?)", TABLE),
                statement -> {
                    statement.set(1, account.getIdentifier());
                    statement.set(2, account.getBalance());
                    statement.set(3, account.getMovimentedBalance());
                    statement.set(4, account.getTransactionsQuantity());
                    statement.set(5, BankHistoricParserUtil.parse(account.getTransactions()));
                    statement.set(6, account.isReceiveCoins() ? 1 : 0);
                }
        );
    }

    public void updateOne(String accountIdentifier, double balance) {
        this.sqlExecutor.updateQuery(
                String.format("UPDATE %s SET balance=? WHERE owner=?", TABLE),
                statement -> {
                    statement.set(1, balance);
                    statement.set(2, accountIdentifier);
                });
    }

}
