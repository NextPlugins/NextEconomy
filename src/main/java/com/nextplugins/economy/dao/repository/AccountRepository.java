package com.nextplugins.economy.dao.repository;

import com.henryfabio.sqlprovider.executor.SQLExecutor;
import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.model.account.Account;
import com.nextplugins.economy.api.model.account.historic.AccountBankHistoric;
import com.nextplugins.economy.dao.repository.adapter.AccountAdapter;
import com.nextplugins.economy.util.LinkedListHelper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.Set;

@RequiredArgsConstructor
public final class AccountRepository {

    private static final String TABLE = "nexteconomy_data";
    private static final LinkedListHelper<AccountBankHistoric> PARSER = new LinkedListHelper<>();

    @Getter private final SQLExecutor sqlExecutor;

    public void createTable() {

        sqlExecutor.updateQuery("CREATE TABLE IF NOT EXISTS " + TABLE + "(" +
                "owner CHAR(16) NOT NULL PRIMARY KEY," +
                "balance DOUBLE NOT NULL," +
                "movimentedBalance DOUBLE NOT NULL," +
                "transactionsQuantity INTEGER NOT NULL," +
                "transactions LONGTEXT NOT NULL" +
                ");"
        );

        val config = NextEconomy.getInstance().getConfig();
        val version = config.getString("database.version", "1.1.4");

        if (version.equalsIgnoreCase("1.1.4")) {

            try {
                sqlExecutor.updateQuery("ALTER TABLE " + TABLE + " ADD COLUMN discordName LONGTEXT NOT NULL DEFAULT Nenhum dado");
                sqlExecutor.updateQuery("ALTER TABLE " + TABLE + " ADD COLUMN receiveCoins INTEGER NOT NULL DEFAULT 1");
            } catch (Exception ignored) { }

            config.set("database.version", "2.0.0");

        }

        NextEconomy.getInstance().saveConfig();

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

    public void saveOne(Account account) {

        this.sqlExecutor.updateQuery(
                String.format("REPLACE INTO %s VALUES(?,?,?,?,?,?,?)", TABLE),
                statement -> {

                    statement.set(1, account.getUsername());
                    statement.set(2, account.getBalance());
                    statement.set(3, account.getMovimentedBalance());
                    statement.set(4, account.getTransactionsQuantity());
                    statement.set(5, PARSER.toJson(account.getTransactions()));
                    statement.set(6, account.getDiscordName());
                    statement.set(7, account.isReceiveCoins() ? 1 : 0);

                }
        );

    }

}
