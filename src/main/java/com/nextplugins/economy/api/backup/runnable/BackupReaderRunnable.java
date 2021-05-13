package com.nextplugins.economy.api.backup.runnable;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.backup.BackupManager;
import com.nextplugins.economy.api.conversor.ConversorManager;
import com.nextplugins.economy.api.model.account.Account;
import com.nextplugins.economy.util.LinkedListHelper;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutionException;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@RequiredArgsConstructor
public final class BackupReaderRunnable implements Runnable {

    private static final LinkedListHelper<Account> PARSER = new LinkedListHelper<>();

    private final CommandSender commandSender;
    private final ConversorManager conversorManager;
    private final BackupManager backupManager;
    private final File file;

    @Override
    public void run() {

        File restaurationFile = null;

        val logger = NextEconomy.getInstance().getLogger();
        try {

            val accountRepository = NextEconomy.getInstance().getAccountRepository();

            logger.info("Criando um ponto de restauração para caso ocorra um erro.");

            restaurationFile = backupManager.createBackup(
                    null,
                    Lists.newArrayList(accountRepository.selectAll("")),
                    true, true
            ).get();

            logger.info("O ponto '" + restaurationFile.getName() + "' de restauração foi criado com sucesso");

            accountRepository.truncateTable();
            logger.warning("Tabela com as contas do servidor foi apagada!");

            val stopwatch = Stopwatch.createStarted();
            val accounts = PARSER.fromJson(new FileReader(this.file));

            conversorManager.startConversion(
                    commandSender,
                    Sets.newHashSet(accounts),
                    "Lendo backup",
                    stopwatch
            );

            logger.info("A leitura do backup '" + this.file.getName() + "' foi finalizada e os valores da tabela alterados. (" + stopwatch + ")");
            backupManager.setBackuping(false);

        } catch (InterruptedException | ExecutionException | IOException exception) {

            Thread.currentThread().interrupt();

            exception.printStackTrace();
            logger.severe("Não foi possível escrever os dados no arquivo.");

            if (restaurationFile != null) {

                logger.severe("Tentando utilizar o backup de restauração!");
                backupManager.loadBackup(commandSender, restaurationFile, true);

            }


        }

    }
}
