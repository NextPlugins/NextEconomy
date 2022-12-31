package com.nextplugins.economy;

import com.github.eikefab.libs.pluginupdater.BukkitUpdater;
import com.github.eikefab.libs.pluginupdater.api.Release;
import com.google.common.base.Stopwatch;
import com.henryfabio.minecraft.inventoryapi.manager.InventoryManager;
import com.henryfabio.sqlprovider.connector.SQLConnector;
import com.henryfabio.sqlprovider.executor.SQLExecutor;
import com.nextplugins.economy.backup.BackupManager;
import com.nextplugins.economy.convertor.ConvertorManager;
import com.nextplugins.economy.convertor.Convertors;
import com.nextplugins.economy.group.GroupWrapperManager;
import com.nextplugins.economy.placeholder.Placeholders;
import com.nextplugins.economy.ranking.RankingRunnable;
import com.nextplugins.economy.util.title.InternalAPIMapping;
import com.nextplugins.economy.util.title.InternalTitleAPI;
import com.nextplugins.economy.command.CommandRegistry;
import com.nextplugins.economy.configuration.FeatureValue;
import com.nextplugins.economy.configuration.registry.ConfigurationRegistry;
import com.nextplugins.economy.dao.SQLProvider;
import com.nextplugins.economy.dao.repository.AccountRepository;
import com.nextplugins.economy.listener.ListenerRegistry;
import com.nextplugins.economy.model.account.storage.AccountStorage;
import com.nextplugins.economy.model.interactions.registry.InteractionRegistry;
import com.nextplugins.economy.ranking.RankingStorage;
import com.nextplugins.economy.ranking.RankingChatBody;
import lombok.Getter;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
public final class NextEconomy extends JavaPlugin {

    private final BukkitUpdater updater;
    private final InternalTitleAPI internalTitleAPI;
    private final EconomyMetrics economyMetrics;

    private final SQLConnector sqlConnector;
    private final SQLExecutor sqlExecutor;

    private final File configFile;
    private final File convertorsFile;

    private final FileConfiguration configuration;
    private final FileConfiguration convertorsConfig;

    private final AccountRepository accountRepository;

    private final AccountStorage accountStorage;
    private final RankingStorage rankingStorage;

    private final BackupManager backupManager;
    private final ConvertorManager convertorManager;
    private final GroupWrapperManager groupWrapperManager;

    private final InteractionRegistry interactionRegistry;

    private final RankingRunnable rankingRunnable;
    private final RankingChatBody rankingChatBody;

    public NextEconomy() {
        final File dataFolder = getDataFolder();

        dataFolder.mkdir();

        saveResource("configuration.yml", false);
        saveResource("convertors.yml", false);

        this.updater = new BukkitUpdater(this, "NextPlugins/NextEconomy");
        this.internalTitleAPI = InternalAPIMapping.create();
        this.economyMetrics = new EconomyMetrics(this);

        this.configFile = new File(dataFolder, "configuration.yml");
        this.convertorsFile = new File(dataFolder, "convertors.yml");

        this.configuration = YamlConfiguration.loadConfiguration(configFile);
        this.convertorsConfig = YamlConfiguration.loadConfiguration(convertorsFile);

        this.sqlConnector = SQLProvider.of(this).setup(null);
        this.sqlExecutor = new SQLExecutor(sqlConnector);

        this.accountRepository = new AccountRepository(sqlExecutor);
        this.accountStorage = new AccountStorage(accountRepository);

        this.convertorManager = new ConvertorManager(accountRepository);
        this.rankingStorage = new RankingStorage();
        this.backupManager = new BackupManager();
        this.groupWrapperManager = new GroupWrapperManager();

        this.interactionRegistry = new InteractionRegistry();
        this.rankingChatBody = new RankingChatBody();

        this.rankingRunnable = new RankingRunnable(this);
    }

    @Override
    public void onLoad() {
        updater.query();
    }

    @Override
    public void onEnable() {
        getLogger().info("Iniciando carregamento do plugin...");

        final Stopwatch loadTime = Stopwatch.createStarted();

        checkUpdates();

        accountStorage.init(getConfig().getBoolean("plugin.configuration.nick-save-method", true));
        interactionRegistry.init();
        economyMetrics.init();

        InventoryManager.enable(this);

        EconomyServiceInjector.inject();

        ConfigurationRegistry.of(this).register();
        CommandRegistry.of(this).register();
        Convertors.of(this).register();

        Bukkit.getScheduler().runTaskLater(this, () -> {
            Placeholders.register();
            ListenerRegistry.of(this).register();

            groupWrapperManager.init();
            rankingRunnable.register();
            purgeBackups();
        }, 150L);

        loadTime.stop();
        getLogger().log(Level.INFO, "Plugin inicializado com sucesso. ({0})", loadTime);
    }

    @Override
    public void onDisable() {
        accountStorage.flushData();
        rankingRunnable.destroy();

        if (updater.isUpdateAvailable()) updater.download().update();

        createBackup();
    }

    private void purgeBackups() {
        val path = new File("plugins/NextEconomy/backups");
        if (!path.exists()) return;

        val list = path.listFiles();
        if (list == null) return;

        for (File file : list) {
            try {
                val basicFileAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);

                val fileTime = basicFileAttributes.creationTime();
                if (fileTime.toMillis() > System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7)) continue;

                if (file.delete()) {
                    getLogger().info("O backup " + file.getName() + " foi apagado por ser muito antigo.");
                } else {
                    getLogger().warning("Não foi possível apagar o backup " + file.getName() + ".");
                }
            } catch (Exception exception) {
                getLogger().warning("Ocorreu um erro ao tentar apagar o backup " + file.getName() + ".");
            }
        }
    }

    private void checkUpdates() {
        if (!updater.isUpdateAvailable()) return;

        final Release release = updater.getReleases().get(0);
        final Logger logger = getLogger();

        final String[] message = new String[] {
                "Nova atualização disponível!",
                "Sua versão: " + getDescription().getVersion(),
                "Nova versão: " + release.getVersion(),
                "Mudanças: " + release.getBody(),
                "A nova versão será aplicada assim que o servidor for reiniciado.",
        };

        for (String line : message) logger.info(line);
    }

    private void createBackup() {
        if (FeatureValue.get(FeatureValue::autoBackup)) {
            CompletableFuture.completedFuture(
                    backupManager.createBackup(
                            null,
                            null,
                            accountRepository,
                            false,
                            false
                    )
            ).join(); // freeze thread
        }
    }

    @Override
    public void saveConfig() {
        try {
            getConfig().save(configFile);
        } catch (IOException ex) {
            getLogger().log(Level.SEVERE, "Não foi possível salvar o arquivo " + configFile, ex);
        }
    }

    @Override
    public @NotNull FileConfiguration getConfig() {
        return configuration;
    }

    public static NextEconomy getInstance() {
        return getPlugin(NextEconomy.class);
    }

}
