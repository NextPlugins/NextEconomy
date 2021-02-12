package com.nextplugins.economy;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.henryfabio.minecraft.inventoryapi.manager.InventoryManager;
import com.henryfabio.sqlprovider.connector.SQLConnector;
import com.henryfabio.sqlprovider.executor.SQLExecutor;
import com.nextplugins.economy.command.registry.CommandRegistry;
import com.nextplugins.economy.configuration.registry.ConfigurationRegistry;
import com.nextplugins.economy.dao.AccountDAO;
import com.nextplugins.economy.listener.registry.ListenerRegistry;
import com.nextplugins.economy.metric.MetricProvider;
import com.nextplugins.economy.placeholder.registry.PlaceholderRegistry;
import com.nextplugins.economy.ranking.NPCRankingRegistry;
import com.nextplugins.economy.ranking.manager.LocationManager;
import com.nextplugins.economy.ranking.runnable.NPCRunnable;
import com.nextplugins.economy.sql.SQLProvider;
import com.nextplugins.economy.storage.AccountStorage;
import com.nextplugins.economy.storage.RankingStorage;
import com.nextplugins.economy.task.registry.TaskRegistry;
import lombok.Getter;
import me.bristermitten.pdm.PluginDependencyManager;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

@Getter
public final class NextEconomy extends JavaPlugin {

    private SQLConnector sqlConnector;
    private SQLExecutor sqlExecutor;

    private AccountDAO accountDAO;
    private AccountStorage accountStorage;
    private RankingStorage rankingStorage;

    private LocationManager locationManager;

    private File npcFile;
    private FileConfiguration npcConfiguration;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        npcFile = new File(getDataFolder(), "npcs.yml");
        if (!npcFile.exists()) {
            saveResource("npcs.yml", false);
        }
        npcConfiguration = YamlConfiguration.loadConfiguration(npcFile);

        PluginDependencyManager.of(this).loadAllDependencies().thenRun(() -> {
            try {
                sqlConnector = SQLProvider.of(this).setup();
                sqlExecutor = new SQLExecutor(sqlConnector);

                accountDAO = new AccountDAO(sqlExecutor);
                accountStorage = new AccountStorage(accountDAO);
                rankingStorage = new RankingStorage();

                locationManager = new LocationManager();

                accountStorage.init();
                InventoryManager.enable(this);

                ConfigurationRegistry.of(this).register();
                ListenerRegistry.of(this).register();
                CommandRegistry.of(this).register();
                TaskRegistry.of(this).register();
                PlaceholderRegistry.of(this).register();
                NPCRankingRegistry.of(this).register();

                MetricProvider.of(this).setup();

                getLogger().info("Plugin inicializado com sucesso!");
            } catch (Throwable t) {
                t.printStackTrace();
                getLogger().severe("Ocorreu um erro durante a inicialização do plugin!");
                Bukkit.getPluginManager().disablePlugin(this);
            }
        });
    }

    @Override
    public void onDisable() {
        for (NPC npc : NPCRunnable.NPC) {
            npc.destroy();
        }
        for (Hologram hologram : NPCRunnable.HOLOGRAM) {
            hologram.delete();
        }
    }

    public static NextEconomy getInstance() {
        return getPlugin(NextEconomy.class);
    }

}
