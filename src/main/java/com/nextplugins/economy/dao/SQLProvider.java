package com.nextplugins.economy.dao;

import com.henryfabio.sqlprovider.connector.SQLConnector;
import com.henryfabio.sqlprovider.connector.type.SQLDatabaseType;
import com.henryfabio.sqlprovider.connector.type.impl.MySQLDatabaseType;
import com.henryfabio.sqlprovider.connector.type.impl.SQLiteDatabaseType;
import lombok.Data;
import lombok.val;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import java.io.File;

@Data(staticConstructor = "of")
public final class SQLProvider {

    private final Plugin plugin;

    public SQLConnector setup(String forceType) {
        val configuration = plugin.getConfig();
        val databaseConfiguration = configuration.getConfigurationSection("database");

        val sqlType = forceType != null ? forceType : databaseConfiguration.getString("type");
        val logger = plugin.getLogger();

        SQLConnector sqlConnector;
        if (sqlType.equalsIgnoreCase("mysql")) {
            ConfigurationSection mysqlSection = databaseConfiguration.getConfigurationSection("mysql");
            sqlConnector = mysqlDatabaseType(mysqlSection).connect();

            logger.info("Conexão com o banco de dados (MySQL) realizada com sucesso.");
        } else if (sqlType.equalsIgnoreCase("sqlite")) {
            ConfigurationSection sqliteSection = databaseConfiguration.getConfigurationSection("sqlite");
            sqlConnector = sqliteDatabaseType(sqliteSection).connect();

            logger.info("Conexão com o banco de dados (SQLite) realizada com sucesso.");
            logger.warning("Recomendamos o uso do banco de dados MySQL.");
        } else {
            logger.severe("O tipo de database selecionado não é válido.");
            return null;
        }

        return sqlConnector;
    }

    private SQLDatabaseType sqliteDatabaseType(ConfigurationSection section) {
        return SQLiteDatabaseType.builder()
                .file(new File(plugin.getDataFolder(), section.getString("file")))
                .build();
    }

    private SQLDatabaseType mysqlDatabaseType(ConfigurationSection section) {
        return MySQLDatabaseType.builder()
                .address(section.getString("address"))
                .username(section.getString("username"))
                .password(section.getString("password"))
                .database(section.getString("database"))
                .build();
    }

}
