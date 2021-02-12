package com.nextplugins.economy;

import com.henryfabio.sqlprovider.connector.SQLConnector;
import com.henryfabio.sqlprovider.executor.SQLExecutor;
import com.nextplugins.economy.command.registry.CommandRegistry;
import com.nextplugins.economy.configuration.registry.ConfigurationRegistry;
import com.nextplugins.economy.dao.AccountDAO;
import com.nextplugins.economy.listener.registry.ListenerRegistry;
import com.nextplugins.economy.metric.MetricProvider;
import com.nextplugins.economy.sql.SQLProvider;
import com.nextplugins.economy.storage.AccountStorage;
import com.nextplugins.economy.storage.RankingStorage;
import com.nextplugins.economy.task.registry.TaskRegistry;
import lombok.Getter;
import me.bristermitten.pdm.PluginDependencyManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class NextEconomy extends JavaPlugin {

    private SQLConnector sqlConnector;
    private SQLExecutor sqlExecutor;

    private AccountDAO accountDAO;
    private AccountStorage accountStorage;
    private RankingStorage rankingStorage;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        PluginDependencyManager.of(this).loadAllDependencies().thenRun(() -> {
            try {
                sqlConnector = SQLProvider.of(this).setup();
                sqlExecutor = new SQLExecutor(sqlConnector);

                accountDAO = new AccountDAO(sqlExecutor);
                accountStorage = new AccountStorage(accountDAO);
                rankingStorage = new RankingStorage();

                accountStorage.init();

                ConfigurationRegistry.of(this).register();
                ListenerRegistry.of(this).register();
                CommandRegistry.of(this).register();
                TaskRegistry.of(this).register();
                MetricProvider.of(this).setup();

                getLogger().info("Plugin inicializado com sucesso!");
            } catch (Throwable t) {
                t.printStackTrace();
                getLogger().severe("Ocorreu um erro durante a inicialização do plugin!");
                Bukkit.getPluginManager().disablePlugin(this);
            }
        });
    }

    public static NextEconomy getInstance() {
        return getPlugin(NextEconomy.class);
    }

}
