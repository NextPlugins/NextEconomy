package com.nextplugins.economy.api.backup.runnable;

import com.google.common.base.Stopwatch;
import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.backup.BackupManager;
import com.nextplugins.economy.api.model.account.Account;
import com.nextplugins.economy.util.ColorUtil;
import com.nextplugins.economy.util.SetSerializerHelper;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@RequiredArgsConstructor
public final class BackupCreatorRunnable implements Runnable {

    private static final SetSerializerHelper<Account> PARSER = new SetSerializerHelper<>();

    private final CommandSender sender;
    private final BackupManager backupManager;
    private final File file;
    private final Set<Account> accounts;

    @Override
    public void run() {

        val logger = NextEconomy.getInstance().getLogger();
        try {

            logger.info("Iniciando criação do backup '" + file.getName() + "'.");

            val stopwatch = Stopwatch.createStarted();

            val writer = new FileWriter(file);
            PARSER.toJson(accounts, writer);

            writer.flush();
            stopwatch.stop();

            logger.info("O backup '" + file.getName() + "' foi finalizado. (" + stopwatch + ")");
            backupManager.setBackuping(false);

            if (sender != null) sender.sendMessage(ColorUtil.colored(
                    "&aBackup '" + file.getName() + "' criado com sucesso!"
            ));

        } catch (IOException exception) {

            exception.printStackTrace();
            logger.severe("Não foi possível escrever os dados no arquivo.");

        }

    }
}
