package com.nextplugins.economy;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Holograms.CMIHologram;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.google.common.base.Stopwatch;
import com.henryfabio.minecraft.inventoryapi.manager.InventoryManager;
import com.henryfabio.sqlprovider.connector.SQLConnector;
import com.henryfabio.sqlprovider.executor.SQLExecutor;
import com.nextplugins.economy.api.PurseAPI;
import com.nextplugins.economy.api.backup.BackupManager;
import com.nextplugins.economy.api.conversor.ConversorManager;
import com.nextplugins.economy.api.conversor.ConversorRegistry;
import com.nextplugins.economy.api.group.GroupWrapperManager;
import com.nextplugins.economy.api.metric.MetricProvider;
import com.nextplugins.economy.api.title.InternalAPIMapping;
import com.nextplugins.economy.api.title.InternalTitleAPI;
import com.nextplugins.economy.command.bukkit.registry.CommandRegistry;
import com.nextplugins.economy.command.discord.registry.DiscordCommandRegistry;
import com.nextplugins.economy.configuration.DiscordValue;
import com.nextplugins.economy.configuration.FeatureValue;
import com.nextplugins.economy.configuration.RankingValue;
import com.nextplugins.economy.configuration.registry.ConfigurationRegistry;
import com.nextplugins.economy.dao.SQLProvider;
import com.nextplugins.economy.dao.repository.AccountRepository;
import com.nextplugins.economy.listener.ListenerRegistry;
import com.nextplugins.economy.model.account.storage.AccountStorage;
import com.nextplugins.economy.model.discord.manager.PayActionDiscordManager;
import com.nextplugins.economy.model.interactions.registry.InteractionRegistry;
import com.nextplugins.economy.placeholder.registry.PlaceholderRegistry;
import com.nextplugins.economy.ranking.CustomRankingRegistry;
import com.nextplugins.economy.ranking.manager.LocationManager;
import com.nextplugins.economy.ranking.storage.RankingStorage;
import com.nextplugins.economy.ranking.types.ArmorStandRunnable;
import com.nextplugins.economy.ranking.types.NPCRunnable;
import com.nextplugins.economy.ranking.util.RankingChatBody;
import com.nextplugins.economy.vault.registry.VaultHookRegistry;
import com.nextplugins.economy.views.registry.InventoryRegistry;
import com.yuhtin.updatechecker.UpdateChecker;
import lombok.Getter;
import lombok.val;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

@Getter
public final class NextEconomy extends JavaPlugin {

    private InternalTitleAPI internalTitleAPI;

    private SQLConnector sqlConnector;
    private SQLExecutor sqlExecutor;

    private AccountRepository accountRepository;

    private AccountStorage accountStorage;
    private RankingStorage rankingStorage;

    private BackupManager backupManager;
    private LocationManager locationManager;
    private ConversorManager conversorManager;
    private GroupWrapperManager groupWrapperManager;
    private PayActionDiscordManager payActionDiscordManager;

    private InteractionRegistry interactionRegistry;
    private DiscordCommandRegistry discordCommandRegistry;

    private UpdateChecker updateChecker;
    private RankingChatBody rankingChatBody;

    private File npcFile;
    private File conversorsFile;
    private File configFile;

    private FileConfiguration npcConfig;
    private FileConfiguration conversorsConfig;
    private FileConfiguration config;

    @Override
    public void onLoad() {
        updateChecker = new UpdateChecker(this, "NextPlugins");
        updateChecker.check();

        npcFile = new File(getDataFolder(), "npcs.yml");
        if (!npcFile.exists()) saveResource("npcs.yml", false);

        npcConfig = YamlConfiguration.loadConfiguration(npcFile);

        configFile = new File(getDataFolder(), "configuration.yml");
        if (!configFile.exists()) saveResource("configuration.yml", false);

        config = YamlConfiguration.loadConfiguration(configFile);

        conversorsFile = new File(getDataFolder(), "conversors.yml");
        if (!conversorsFile.exists()) saveResource("conversors.yml", false);

        conversorsConfig = YamlConfiguration.loadConfiguration(conversorsFile);
    }

    @Override
    public void onEnable() {
        getLogger().info("Iniciando carregamento do plugin.");

        val loadTime = Stopwatch.createStarted();
        if (updateChecker.canUpdate()) {
            val lastRelease = updateChecker.getLastRelease();

            getLogger().info("");
            getLogger().info("[NextUpdate] ATENÇÃO!");
            getLogger().info("[NextUpdate] Você está usando uma versão antiga do NextEconomy!");
            getLogger().info("[NextUpdate] Nova versão: " + lastRelease.getVersion());
            getLogger().info("[NextUpdate] Baixe aqui: " + lastRelease.getDownloadURL());
            getLogger().info("");
        } else {
            getLogger().info("[NextUpdate] Olá! Vim aqui revisar se a versão do NextEconomy está atualizada, e pelo visto sim! Obrigado por usar nossos plugins!");
        }

        sqlConnector = SQLProvider.of(this).setup(null);
        sqlExecutor = new SQLExecutor(sqlConnector);

        accountRepository = new AccountRepository(sqlExecutor);
        accountStorage = new AccountStorage(accountRepository);

        conversorManager = new ConversorManager(accountRepository);
        rankingStorage = new RankingStorage();
        backupManager = new BackupManager();
        locationManager = new LocationManager();
        groupWrapperManager = new GroupWrapperManager();
        interactionRegistry = new InteractionRegistry();
        discordCommandRegistry = new DiscordCommandRegistry();
        rankingChatBody = new RankingChatBody();

        internalTitleAPI = InternalAPIMapping.create();

        val nickValue = getConfig().getString("plugin.configuration.save-method", "NICK");
        val nickMode = nickValue.equalsIgnoreCase("NICK");

        accountStorage.init(nickMode);
        interactionRegistry.init();

        InventoryManager.enable(this);

        ConfigurationRegistry.of(this).register();
        CommandRegistry.of(this).register();
        VaultHookRegistry.of(this).register();
        MetricProvider.of(this).register();
        InventoryRegistry.of(this).register();
        ConversorRegistry.of(this).register();

        Bukkit.getScheduler().runTaskLater(this, () -> {
            PlaceholderRegistry.of(this).register();
            CustomRankingRegistry.of(this).register();
            ListenerRegistry.of(this).register();

            if (!PurseAPI.init()) getLogger().info("Sistema de bolsa de valores desativado.");
            else PurseAPI.getInstance(); // force purse update

            groupWrapperManager.init();

            // bump money top one time and add, if enabled, stands/npcs
            rankingStorage.updateRanking(true);

            registerPayDiscordManager();
            discordCommandRegistry.init();

            purgeBackups();
        }, 150L);

        loadTime.stop();
        getLogger().log(Level.INFO, "Plugin inicializado com sucesso. ({0})", loadTime);
    }

    @Override
    public void onDisable() {
        accountStorage.flushData();
        unloadRanking();

        if (FeatureValue.get(FeatureValue::autoBackup)) {
            CompletableFuture.completedFuture(
                    backupManager.createBackup(null, null, accountRepository, false, false)
            ).join(); // freeze thread
        }
    }

    private void unloadRanking() {
        if (CustomRankingRegistry.getInstance().isEnabled()) {
            if (CustomRankingRegistry.getInstance().isHolographicDisplays()) {
                HologramsAPI.getHolograms(this).forEach(Hologram::delete);
            } else {
                // jump concurrentmodificationexception
                val holograms = new ArrayList<CMIHologram>();
                val hologramManager = CMI.getInstance().getHologramManager();
                for (val entry : hologramManager.getHolograms().entrySet()) {
                    if (entry.getKey().startsWith("NextEconomy")) holograms.add(entry.getValue());
                }

                holograms.forEach(hologramManager::removeHolo);
            }

            String type = RankingValue.get(RankingValue::npcType);
            if (type.equalsIgnoreCase("npc")) {

                for (val id : NPCRunnable.NPCS) {
                    val npc = CitizensAPI.getNPCRegistry().getById(id);
                    if (npc == null) continue;

                    CitizensAPI.getNPCRegistry().deregister(npc);
                }

            }

            if (type.equalsIgnoreCase("armorstand")) {
                for (val stand : ArmorStandRunnable.STANDS) {
                    stand.remove();
                }
            }

            getLogger().info("Sistema de ranking visual descarregado com sucesso");
        }
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

    private void registerPayDiscordManager() {
        if (!DiscordValue.get(DiscordValue::enable) || !Bukkit.getPluginManager().isPluginEnabled("DiscordSRV")) return;
        payActionDiscordManager = new PayActionDiscordManager(accountStorage);
    }

    @Override
    public void saveConfig() {
        try {
            getConfig().save(configFile);
        } catch (IOException ex) {
            getLogger().log(Level.SEVERE, "Não foi possível salvar o arquivo " + configFile, ex);
        }
    }

    public @NotNull FileConfiguration getConfig() {
        return config;
    }

    public static NextEconomy getInstance() {
        return getPlugin(NextEconomy.class);
    }

}
