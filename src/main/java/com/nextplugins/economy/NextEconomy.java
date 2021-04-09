package com.nextplugins.economy;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.henryfabio.minecraft.inventoryapi.manager.InventoryManager;
import com.henryfabio.sqlprovider.connector.SQLConnector;
import com.henryfabio.sqlprovider.executor.SQLExecutor;
import com.nextplugins.economy.api.PurseAPI;
import com.nextplugins.economy.command.registry.CommandRegistry;
import com.nextplugins.economy.configuration.registry.ConfigurationRegistry;
import com.nextplugins.economy.configuration.values.RankingValue;
import com.nextplugins.economy.dao.AccountDAO;
import com.nextplugins.economy.listener.ListenerRegistry;
import com.nextplugins.economy.manager.ConversorManager;
import com.nextplugins.economy.metric.MetricProvider;
import com.nextplugins.economy.placeholder.registry.PlaceholderRegistry;
import com.nextplugins.economy.ranking.CustomRankingRegistry;
import com.nextplugins.economy.ranking.manager.LocationManager;
import com.nextplugins.economy.ranking.runnable.ArmorStandRunnable;
import com.nextplugins.economy.ranking.runnable.NPCRunnable;
import com.nextplugins.economy.registry.InventoryRegistry;
import com.nextplugins.economy.sql.SQLProvider;
import com.nextplugins.economy.storage.AccountStorage;
import com.nextplugins.economy.storage.RankingStorage;
import com.nextplugins.economy.task.AccountSaveTask;
import com.nextplugins.economy.task.registry.TaskRegistry;
import com.nextplugins.economy.vault.registry.VaultHookRegistry;
import lombok.Getter;
import me.bristermitten.pdm.PluginDependencyManager;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

@Getter
public final class NextEconomy extends JavaPlugin {

    private SQLConnector sqlConnector;
    private SQLExecutor sqlExecutor;

    private AccountDAO accountDAO;
    private AccountStorage accountStorage;
    private RankingStorage rankingStorage;
    private ConversorManager conversorManager;

    private LocationManager locationManager;

    private File npcFile;
    private FileConfiguration npcConfig;

    @Override
    public void onLoad() {

        saveDefaultConfig();

        npcFile = new File(getDataFolder(), "npcs.yml");
        if (!npcFile.exists()) saveResource("npcs.yml", false);

        npcConfig = YamlConfiguration.loadConfiguration(npcFile);


    }

    @Override
    public void onEnable() {

        PluginDependencyManager.of(this).loadAllDependencies().thenRun(() -> {
            try {

                sqlConnector = SQLProvider.of(this).setup();
                sqlExecutor = new SQLExecutor(sqlConnector);

                accountDAO = new AccountDAO(sqlExecutor);
                accountStorage = new AccountStorage(accountDAO);
                conversorManager = new ConversorManager(accountDAO);
                rankingStorage = new RankingStorage();
                locationManager = new LocationManager();

                accountStorage.init();

                InventoryManager.enable(this);

                VaultHookRegistry.of(this).register();
                ConfigurationRegistry.of(this).register();
                ListenerRegistry.of(this).register();
                CommandRegistry.of(this).register();
                TaskRegistry.of(this).register();

                InventoryRegistry.getInstance().init(this);

                MetricProvider.of(this).setup();

                Bukkit.getScheduler().runTaskLaterAsynchronously(this, () -> {
                    PlaceholderRegistry.of(this).register();
                    CustomRankingRegistry.of(this).register();
                }, 5 * 20);

                if (!PurseAPI.isAvaliable()) getLogger().info("Sistema de bolsa de valores desativado.");

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

        if (CustomRankingRegistry.isEnabled()) {
            String type = RankingValue.get(RankingValue::npcType);
            if (type.equalsIgnoreCase("npc")) {

                NPCRunnable.NPCS.forEach(NPC::destroy);
                NPCRunnable.HOLOGRAM.forEach(Hologram::delete);

            }

            if (type.equalsIgnoreCase("armorstand")) {

                ArmorStandRunnable.STANDS.forEach(ArmorStand::remove);
                ArmorStandRunnable.HOLOGRAM.forEach(Hologram::delete);

            }
        }

        AccountSaveTask accountSaveTask = new AccountSaveTask(accountStorage, accountDAO);
        accountSaveTask.run();
    }

    public static NextEconomy getInstance() {
        return getPlugin(NextEconomy.class);
    }

}
