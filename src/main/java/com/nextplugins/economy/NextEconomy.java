package com.nextplugins.economy;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.henryfabio.minecraft.inventoryapi.manager.InventoryManager;
import com.henryfabio.sqlprovider.connector.SQLConnector;
import com.henryfabio.sqlprovider.executor.SQLExecutor;
import com.nextplugins.economy.api.PurseAPI;
import com.nextplugins.economy.api.backup.BackupManager;
import com.nextplugins.economy.command.bukkit.registry.CommandRegistry;
import com.nextplugins.economy.configuration.DiscordValue;
import com.nextplugins.economy.configuration.FeatureValue;
import com.nextplugins.economy.configuration.registry.ConfigurationRegistry;
import com.nextplugins.economy.configuration.RankingValue;
import com.nextplugins.economy.dao.repository.AccountRepository;
import com.nextplugins.economy.listener.ListenerRegistry;
import com.nextplugins.economy.api.conversor.ConversorManager;
import com.nextplugins.economy.api.metric.MetricProvider;
import com.nextplugins.economy.command.discord.registry.DiscordCommandRegistry;
import com.nextplugins.economy.placeholder.registry.PlaceholderRegistry;
import com.nextplugins.economy.ranking.CustomRankingRegistry;
import com.nextplugins.economy.ranking.manager.LocationManager;
import com.nextplugins.economy.ranking.runnable.ArmorStandRunnable;
import com.nextplugins.economy.ranking.runnable.NPCRunnable;
import com.nextplugins.economy.api.model.interactions.registry.InteractionRegistry;
import com.nextplugins.economy.views.registry.InventoryRegistry;
import com.nextplugins.economy.dao.SQLProvider;
import com.nextplugins.economy.api.model.account.storage.AccountStorage;
import com.nextplugins.economy.ranking.storage.RankingStorage;
import com.nextplugins.economy.vault.registry.VaultHookRegistry;
import github.scarsz.discordsrv.DiscordSRV;
import lombok.Getter;
import lombok.val;
import me.bristermitten.pdm.PluginDependencyManager;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

@Getter
public final class NextEconomy extends JavaPlugin {

    private SQLConnector sqlConnector;
    private SQLExecutor sqlExecutor;

    private AccountRepository accountRepository;

    private AccountStorage accountStorage;
    private RankingStorage rankingStorage;

    private BackupManager backupManager;
    private LocationManager locationManager;
    private ConversorManager conversorManager;

    private InteractionRegistry interactionRegistry;
    private DiscordCommandRegistry discordCommandRegistry;

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

        getLogger().info("Baixando e carregando dependências necessárias...");

        val downloadTime = Stopwatch.createStarted();

        PluginDependencyManager.of(this)
                .loadAllDependencies()
                .exceptionally(throwable -> {

                    throwable.printStackTrace();

                    getLogger().severe("Ocorreu um erro durante a inicialização do plugin!");
                    Bukkit.getPluginManager().disablePlugin(this);

                    return null;

                })
                .join();

        downloadTime.stop();

        getLogger().log(Level.INFO, "Dependências carregadas com sucesso! ({0})", downloadTime);
        getLogger().info("Iniciando carregamento do plugin.");

        val loadTime = Stopwatch.createStarted();

        sqlConnector = SQLProvider.of(this).setup(null);
        sqlExecutor = new SQLExecutor(sqlConnector);

        accountRepository = new AccountRepository(sqlExecutor);
        accountStorage = new AccountStorage(accountRepository);
        conversorManager = new ConversorManager(accountRepository);
        rankingStorage = new RankingStorage();
        backupManager = new BackupManager();
        locationManager = new LocationManager();
        interactionRegistry = new InteractionRegistry();
        discordCommandRegistry = new DiscordCommandRegistry();

        accountStorage.init();
        interactionRegistry.init();

        InventoryManager.enable(this);

        ConfigurationRegistry.of(this).register();
        CommandRegistry.of(this).register();
        VaultHookRegistry.of(this).register();
        MetricProvider.of(this).register();
        InventoryRegistry.of(this).register();

        Bukkit.getScheduler().runTaskLater(this, () -> {

            PlaceholderRegistry.of(this).register();
            CustomRankingRegistry.of(this).register();
            ListenerRegistry.of(this).register();

            // bump money top one time and add, if enabled, stands/npcs
            rankingStorage.updateRanking();
            discordCommandRegistry.init();

        }, 150L);

        if (!PurseAPI.init()) getLogger().info("Sistema de bolsa de valores desativado.");
        else PurseAPI.getInstance().forceUpdate();

        loadTime.stop();
        getLogger().log(Level.INFO, "Plugin inicializado com sucesso. ({0})", loadTime);

    }

    @Override
    public void onDisable() {

        if (DiscordValue.get(DiscordValue::enable)) {

            val discordAPI = DiscordSRV.api;

            val commandHandler = discordCommandRegistry.getCommandHandler();
            if (commandHandler != null) discordAPI.unsubscribe(commandHandler);

        }

        accountStorage.getCache().synchronous().invalidateAll();

        if (FeatureValue.get(FeatureValue::autoBackup)) {

            val accounts = accountRepository.selectAll("");
            CompletableFuture.completedFuture(
                    backupManager.createBackup(null, null, Lists.newArrayList(accounts), false, false)
            ).join(); // freeze thread

        }

        if (CustomRankingRegistry.getInstance().isEnabled()) {
            String type = RankingValue.get(RankingValue::npcType);
            if (type.equalsIgnoreCase("npc")) {

                for (NPC npc : NPCRunnable.NPCS) {
                    npc.destroy();
                }

                for (Hologram hologram : NPCRunnable.HOLOGRAM) {
                    hologram.delete();
                }

            }

            if (type.equalsIgnoreCase("armorstand")) {

                for (ArmorStand stand : ArmorStandRunnable.STANDS) {
                    stand.remove();
                }

                for (Hologram hologram : ArmorStandRunnable.HOLOGRAM) {
                    hologram.delete();
                }

            }
        }

    }

    public static NextEconomy getInstance() {
        return getPlugin(NextEconomy.class);
    }

}
