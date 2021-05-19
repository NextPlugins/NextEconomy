package com.nextplugins.economy.api.backup.runnable;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.backup.BackupManager;
import com.nextplugins.economy.api.backup.response.ResponseType;
import com.nextplugins.economy.api.conversor.ConversorManager;
import com.nextplugins.economy.api.model.account.Account;
import com.nextplugins.economy.util.ColorUtil;
import com.nextplugins.economy.util.LinkedListHelper;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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
    private final boolean restauration;
    private final File file;

    @Override
    public void run() {

        File restaurationFile = null;

        val logger = NextEconomy.getInstance().getLogger();
        try {

            val accountRepository = NextEconomy.getInstance().getAccountRepository();

            if (!restauration) {

                backupManager.setBackuping(false);

                logger.info("Criando um ponto de restauração para caso ocorra um erro.");

                val response = backupManager.createBackup(
                        null, null,
                        Lists.newArrayList(accountRepository.selectAll("")),
                        true, true
                );

                if (response.getResponseType() == ResponseType.SUCCESS) {

                    restaurationFile = response.getFile();
                    logger.info("O ponto '" + restaurationFile.getName() + "' de restauração foi criado com sucesso");

                } else {

                    logger.warning("O ponto de restauração não pode ser criado. (" + response.getResponseType() + ")");
                    logger.warning("Cancelando o carregamento do backup.");

                    commandSender.sendMessage(ColorUtil.colored("&cOcorreu um erro, observe o console!"));
                    return;

                }

            }

            backupManager.setBackuping(true);

            accountRepository.recreateTable();
            logger.warning("Tabela com as contas do servidor foi apagada!");

            String type = restauration ? "ponto de restauração" : "backup";

            logger.info("Iniciando leitura do " + type + ".");

            val stopwatch = Stopwatch.createStarted();
            val accounts = PARSER.fromJson(new FileReader(this.file));

            conversorManager.startConversion(
                    commandSender,
                    Sets.newHashSet(accounts),
                    "Lendo " + type,
                    stopwatch
            );

            logger.info("A leitura do " + type + " '" + this.file.getName() + "' foi finalizada e os valores da tabela alterados. (" + stopwatch + ")");
            backupManager.setBackuping(false);

        } catch (IOException exception) {

            backupManager.setBackuping(false);

            Thread.currentThread().interrupt();

            exception.printStackTrace();
            logger.severe("Não foi possível ler os dados do arquivo.");

            if (restaurationFile != null) {

                logger.severe("Tentando utilizar o backup de restauração!");
                backupManager.loadBackup(commandSender, restaurationFile, true, true);

            }


        }

    }
}
