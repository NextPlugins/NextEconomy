package com.nextplugins.economy.api.backup;

import com.henryfabio.sqlprovider.connector.utils.FileUtils;
import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.backup.response.BackupResponse;
import com.nextplugins.economy.api.backup.response.ResponseType;
import com.nextplugins.economy.api.backup.runnable.BackupCreatorRunnable;
import com.nextplugins.economy.api.backup.runnable.BackupReaderRunnable;
import com.nextplugins.economy.api.model.account.Account;
import com.nextplugins.economy.dao.repository.AccountRepository;
import com.nextplugins.economy.util.DateFormatUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.var;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
     * @param name              of backup (if null, use the current time)
     * @param accountRepository to backup
     * @return {@link File} created
     */
    @NotNull
    public synchronized BackupResponse createBackup(@Nullable CommandSender sender,
                                                    @Nullable String name,
                                                    AccountRepository accountRepository,
                                                    boolean restaurationPoint,
                                                    boolean async) {

        if (backuping) return new BackupResponse(null, ResponseType.BACKUP_IN_PROGRESS);

        setBackuping(true);

        val plugin = NextEconomy.getInstance();
        val scheduler = Bukkit.getScheduler();

        var fileName = name == null ? getTimeAsString() : name;
        if (fileName.contains(".")) fileName = fileName.split("\\.")[0];

        fileName = restaurationPoint ? "restauration/" + fileName : "backups/" + fileName;

        val file = new File(plugin.getDataFolder(), fileName + ".json");
        plugin.getLogger().info("Criando backup para o local '" + file.getPath() + "'.");

        if (file.exists()) {

            plugin.getLogger().info("Já existe um backup com este nome.");
            return new BackupResponse(null, ResponseType.NAME_IN_USE);

        }

        FileUtils.createFileIfNotExists(file);

        val accountStorage = NextEconomy.getInstance().getAccountStorage();
        accountStorage.flushData();

        if (async) {
            scheduler.runTaskAsynchronously(plugin, () -> {
                Set<Account> accounts = new HashSet<>();
                accountRepository.selectAll("").forEach(account -> {
                    account.saveTransactions();
                    accounts.add(account);
                });

                val runnable = new BackupCreatorRunnable(sender, this, file, accounts);
                scheduler.runTaskAsynchronously(plugin, runnable);
            });
        } else {
            Set<Account> accounts = new HashSet<>();
            accountRepository.selectAll("").forEach(account -> {
                account.saveTransactions();
                accounts.add(account);
            });

            val runnable = new BackupCreatorRunnable(sender, this, file, accounts);
            runnable.run();
        }

        return new BackupResponse(file, ResponseType.SUCCESS);

    }

    /**
     * Load backup
     * Warning: All users will be deleted and replaced by backup
     *
     * @param sender executing command (can be null)
     * @param file   backup to read
     * @param async  operation mode
     */
    public synchronized void loadBackup(@Nullable CommandSender sender,
                                        File file,
                                        boolean restauration,
                                        boolean async) {

        if (backuping) return;

        val conversorManager = NextEconomy.getInstance().getConversorManager();
        if (!conversorManager.checkConversorAvailability(sender)) return;

        setBackuping(true);

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
        else runnable.run();

    }

    private String getTimeAsString() {
        return DateFormatUtil.of(System.currentTimeMillis())
                .replace("/", "-")
                .replace(" às ", "-")
                .replace(":", "-");
    }

}
