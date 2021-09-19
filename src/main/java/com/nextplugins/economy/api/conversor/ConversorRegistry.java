package com.nextplugins.economy.api.conversor;

import com.henryfabio.sqlprovider.connector.SQLConnector;
import com.henryfabio.sqlprovider.connector.type.impl.MySQLDatabaseType;
import com.henryfabio.sqlprovider.connector.type.impl.SQLiteDatabaseType;
import com.henryfabio.sqlprovider.executor.SQLExecutor;
import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.conversor.impl.atlas.AtlasEconomyConversor;
import com.nextplugins.economy.api.conversor.impl.essentials.EssentialsXConversor;
import com.nextplugins.economy.api.conversor.impl.iconomy.IConomyConversor;
import com.nextplugins.economy.api.conversor.impl.jheyson.JHEconomyConversor;
import com.nextplugins.economy.api.conversor.impl.soekd.SOEconomyConversor;
import com.nextplugins.economy.api.conversor.impl.solary.SolaryEconomyConversor;
import com.nextplugins.economy.api.conversor.impl.tusk.TuskEconomyConversor;
import com.nextplugins.economy.api.conversor.impl.ystore.YEconomyConversor;
import lombok.Data;
import lombok.val;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Data(staticConstructor = "of")
public class ConversorRegistry {

    private final NextEconomy plugin;
    private final Map<String, Conversor> values = new HashMap<String, Conversor>() {{
        put("AtlasEconomy", new AtlasEconomyConversor());
        put("JH_Economy", new JHEconomyConversor());
        put("yEconomy", new YEconomyConversor());
        put("TuskEconomy", new TuskEconomyConversor());
        put("SOEconomy", new SOEconomyConversor());
        put("SolaryEconomy", new SolaryEconomyConversor());
        put("Essentials", new EssentialsXConversor());
        put("iConomy", new IConomyConversor());
    }};

    public void register() {
        val config = plugin.getConversorsConfig();
        val conversorManager = plugin.getConversorManager();

        for (val type : values.entrySet()) {
            val conversorName = type.getKey();

            val value = type.getValue();
            value.setConversorName(conversorName);

            if (!config.contains(conversorName)) {
                conversorManager.registerConversor(type.getValue());
                continue;
            }

            val configurationSection = config.getConfigurationSection(conversorName);
            if (configurationSection == null || !configurationSection.getBoolean("use")) continue;

            val connector = new SQLExecutor(configureSqlProvider(configurationSection));
            val tableName = configurationSection.getString("connection.table");
            value.setExecutor(connector);
            value.setTable(tableName);

            conversorManager.registerConversor(value);
            plugin.getLogger().info("[Converter] Registrado o conversor " + type);
        }

    }

    private SQLConnector configureSqlProvider(ConfigurationSection section) {
        SQLConnector connector;
        if (section.getBoolean("connection.mysql.enable")) {
            val mysqlSection = section.getConfigurationSection("connection.mysql");
            connector = MySQLDatabaseType.builder()
                    .address(mysqlSection.getString("address"))
                    .username(mysqlSection.getString("username"))
                    .password(mysqlSection.getString("password"))
                    .database(mysqlSection.getString("database"))
                    .build()
                    .connect();
        } else {
            if (!section.contains("connection.sqlite")) return null;

            val sqliteSection = section.getConfigurationSection("connection.sqlite");
            connector = SQLiteDatabaseType.builder()
                    .file(new File(sqliteSection.getString("file")))
                    .build()
                    .connect();
        }

        return connector;
    }

}
