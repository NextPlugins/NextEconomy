package com.nextplugins.economy;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.henryfabio.minecraft.inventoryapi.manager.InventoryManager;
import com.henryfabio.sqlprovider.connector.SQLConnector;
import com.henryfabio.sqlprovider.executor.SQLExecutor;
import com.nextplugins.economy.api.PurseAPI;
import com.nextplugins.economy.command.registry.CommandRegistry;
import com.nextplugins.economy.configuration.registry.ConfigurationRegistry;
import com.nextplugins.economy.configuration.RankingValue;
import com.nextplugins.economy.dao.repository.AccountRepository;
import com.nextplugins.economy.listener.ListenerRegistry;
import com.nextplugins.economy.api.conversor.ConversorManager;
import com.nextplugins.economy.api.metric.MetricProvider;
import com.nextplugins.economy.placeholder.registry.PlaceholderRegistry;
import com.nextplugins.economy.ranking.CustomRankingRegistry;
import com.nextplugins.economy.ranking.manager.LocationManager;
import com.nextplugins.economy.ranking.runnable.ArmorStandRunnable;
import com.nextplugins.economy.ranking.runnable.NPCRunnable;
import com.nextplugins.economy.listener.events.interactions.registry.InteractionRegistry;
import com.nextplugins.economy.views.registry.InventoryRegistry;
import com.nextplugins.economy.dao.SQLProvider;
import com.nextplugins.economy.api.model.account.storage.AccountStorage;
import com.nextplugins.economy.ranking.storage.RankingStorage;
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

    private AccountRepository accountRepository;
    private AccountStorage accountStorage;
    private RankingStorage rankingStorage;
    private ConversorManager conversorManager;
    private LocationManager locationManager;
    private InteractionRegistry interactionRegistry;

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

                accountRepository = new AccountRepository(sqlExecutor);
                accountStorage = new AccountStorage(accountRepository);
                conversorManager = new ConversorManager(accountRepository);
                rankingStorage = new RankingStorage();
                locationManager = new LocationManager();
                interactionRegistry = new InteractionRegistry();

                accountStorage.init();
                interactionRegistry.init();

                InventoryManager.enable(this);

                ConfigurationRegistry.of(this).register();
                ListenerRegistry.of(this).register();
                CommandRegistry.of(this).register();
                TaskRegistry.of(this).register();
                VaultHookRegistry.of(this).register();
                MetricProvider.of(this).register();
                InventoryRegistry.of(this).register();

                Bukkit.getScheduler().runTaskLater(this, () -> {
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

        AccountSaveTask accountSaveTask = new AccountSaveTask(accountStorage, accountRepository);
        accountSaveTask.run();
    }

    public static NextEconomy getInstance() {
        return getPlugin(NextEconomy.class);
    }

}
