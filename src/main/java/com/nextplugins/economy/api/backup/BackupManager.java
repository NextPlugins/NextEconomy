package com.nextplugins.economy.api.backup;

import com.henryfabio.sqlprovider.connector.utils.FileUtils;
import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.backup.runnable.BackupCreatorRunnable;
import com.nextplugins.economy.api.backup.runnable.BackupReaderRunnable;
import com.nextplugins.economy.api.model.account.Account;
import com.nextplugins.economy.util.DateFormatUtil;
import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Data
@RequiredArgsConstructor
public final class BackupManager {

    private boolean backuping;

    /**
     * Create a bakcup
     *
     * @param name of backup (if null, use the current time)
     * @param accounts to backup
     * @param async operation mode
     * @return {@link File} created
     */
    @Nullable
    public CompletableFuture<File> createBackup(@Nullable String name,
                                          List<Account> accounts,
                                          boolean restaurationPoint,
                                          boolean async) {

        if (backuping) return null;

        backuping = true;

        val plugin = NextEconomy.getInstance();
        val scheduler = Bukkit.getScheduler();

        var fileName = name == null ? getTimeAsString() : name;
        if (fileName.contains(".")) fileName = fileName.split("\\.")[0];

        if (restaurationPoint) fileName = "restauration/" + fileName;

        val file = new File(plugin.getDataFolder(), "backups/" + fileName + ".json");
        plugin.getLogger().info("Criando backup para o local '" + file.getPath() + "'.");

        if (file.exists()) {

            plugin.getLogger().info("O nome inserido no arquivo de backup já existe.");
            return null;

        }

        FileUtils.createFileIfNotExists(file);

        val runnable = new BackupCreatorRunnable(this, file, accounts);

        if (async) scheduler.runTaskAsynchronously(plugin, runnable);
        else scheduler.runTask(plugin, runnable);

        return CompletableFuture.completedFuture(file);

    }

    /**
     * Load backup
     * Warning: All users will be deleted and replaced by backup
     *
     * @param sender executing command (can be null)
     * @param file backup to read
     * @param async operation mode
     */
    public void loadBackup(@Nullable CommandSender sender,
                           File file,
                           boolean restauration,
                           boolean async) {

        if (backuping) return;

        val conversorManager = NextEconomy.getInstance().getConversorManager();
        conversorManager.checkConversorAvaility(sender);

        conversorManager.setConverting(true);
        val runnable = new BackupReaderRunnable(
                sender,
                conversorManager,
                this,
                restauration,
                file
        );

        val plugin = NextEconomy.getInstance();
        val scheduler = Bukkit.getScheduler();

        if (async) scheduler.runTaskAsynchronously(plugin, runnable);
        else scheduler.runTask(plugin, runnable);

    }

    private String getTimeAsString() {
        return DateFormatUtil.of(System.currentTimeMillis())
                .replace("/", "-")
                .replace(" ", "-")
                .replace(":", "-");
    }

}
