package com.nextplugins.economy.api.backup.runnable;

import com.google.common.base.Stopwatch;
import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.backup.BackupManager;
import com.nextplugins.economy.api.model.account.Account;
import com.nextplugins.economy.util.LinkedListHelper;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@RequiredArgsConstructor
public final class BackupCreatorRunnable implements Runnable {

    private static final LinkedListHelper<Account> PARSER = new LinkedListHelper<>();

    private final BackupManager backupManager;
    private final File file;
    private final List<Account> accounts;

    @Override
    public void run() {

        val logger = NextEconomy.getInstance().getLogger();
        try {

            logger.info("Iniciando criação do backup '" + file.getName() + "'.");

            val stopwatch = Stopwatch.createStarted();
            PARSER.toJson(accounts, new FileWriter(file));

            stopwatch.stop();

            logger.info("O backup '" + file.getName() + "' foi finalizado. (" + stopwatch + ")");
            backupManager.setBackuping(false);

        } catch (IOException exception) {

            exception.printStackTrace();
            logger.severe("Não foi possível escrever os dados no arquivo.");

        }

    }
}
