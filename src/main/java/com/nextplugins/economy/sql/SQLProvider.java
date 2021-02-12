package com.nextplugins.economy.sql;

import com.henryfabio.sqlprovider.connector.SQLConnector;
import com.henryfabio.sqlprovider.connector.type.SQLDatabaseType;
import com.henryfabio.sqlprovider.connector.type.impl.MySQLDatabaseType;
import com.henryfabio.sqlprovider.connector.type.impl.SQLiteDatabaseType;
import lombok.Data;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

@Data(staticConstructor = "of")
public final class SQLProvider {

    private final JavaPlugin javaPlugin;

    public SQLConnector setup() {

        FileConfiguration configuration = javaPlugin.getConfig();
        ConfigurationSection databaseConfiguration = configuration.getConfigurationSection("database");

        String sqlType = databaseConfiguration.getString("type");

        Logger logger = javaPlugin.getLogger();

        SQLConnector sqlConnector;

        if (sqlType.equalsIgnoreCase("mysql")) {

            ConfigurationSection mysqlSection = databaseConfiguration.getConfigurationSection("mysql");
            sqlConnector = mysqlDatabaseType(mysqlSection).connect();
            logger.info("Conexão com o banco de dados (MySQL) realizada com sucesso.");

        } else if (sqlType.equalsIgnoreCase("sqlite")) {

            ConfigurationSection sqliteSection = databaseConfiguration.getConfigurationSection("sqlite");
            sqlConnector = sqliteDatabaseType(sqliteSection).connect();
            logger.info("Conexão com o banco de dados (SQLite) realizada com sucesso.");

        } else {

            logger.severe("O tipo de database selecionado não é válido.");
            return null;

        }

        return sqlConnector;

    }

    private SQLDatabaseType sqliteDatabaseType(ConfigurationSection section) {
        return SQLiteDatabaseType.builder()
                .file(new File(javaPlugin.getDataFolder(), section.getString("file")))
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
