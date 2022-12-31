package com.nextplugins.economy.convertor;

import com.henryfabio.sqlprovider.connector.SQLConnector;
import com.henryfabio.sqlprovider.connector.type.impl.MySQLDatabaseType;
import com.henryfabio.sqlprovider.connector.type.impl.SQLiteDatabaseType;
import com.henryfabio.sqlprovider.executor.SQLExecutor;
import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.convertor.impl.atlas.AtlasEconomyConvertor;
import com.nextplugins.economy.convertor.impl.essentials.EssentialsXConvertor;
import com.nextplugins.economy.convertor.impl.iconomy.IConomyConvertor;
import com.nextplugins.economy.convertor.impl.jheyson.JHEconomyConvertor;
import com.nextplugins.economy.convertor.impl.soekd.SOEconomyConvertor;
import com.nextplugins.economy.convertor.impl.solary.SolaryEconomyConvertor;
import com.nextplugins.economy.convertor.impl.storm.StormEconomyConvertor;
import com.nextplugins.economy.convertor.impl.tusk.TuskEconomyConvertor;
import com.nextplugins.economy.convertor.impl.ystore.YEconomyConvertor;
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
public class Convertors {

    private final NextEconomy plugin;
    private final Map<String, Convertor> values = new HashMap<String, Convertor>() {{
        put("AtlasEconomy", new AtlasEconomyConvertor());
        put("JH_Economy", new JHEconomyConvertor());
        put("yEconomy", new YEconomyConvertor());
        put("TuskEconomy", new TuskEconomyConvertor());
        put("SOEconomy", new SOEconomyConvertor());
        put("SolaryEconomy", new SolaryEconomyConvertor());
        put("StormEconomy", new StormEconomyConvertor());
        put("Essentials", new EssentialsXConvertor());
        put("iConomy", new IConomyConvertor());
    }};

    public void register() {
        val config = plugin.getConversorsConfig();
        val convertorManager = plugin.getConvertorManager();

        for (val type : values.entrySet()) {
            val convertorName = type.getKey();

            val value = type.getValue();
            value.setConversorName(convertorName);

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
            plugin.getLogger().info("[Converter] Registrado o conversor " + type.getKey());
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
