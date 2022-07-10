package com.nextplugins.economy.api.conversor.impl.essentials;

import com.nextplugins.economy.api.conversor.Conversor;
import com.nextplugins.economy.model.account.Account;
import com.nextplugins.economy.util.NumberUtils;
import lombok.val;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class EssentialsXConversor extends Conversor {

    @Override
    public Set<Account> lookupPlayers() {
        Set<Account> accounts = new HashSet<>();

        val file = new File("plugins/Essentials/userdata");
        val files = file.listFiles();
        if (files == null) return accounts;

        for (File playerArchive : files) {
            val accountBuilder = Account.generate();
            try (val bufferedReader = new BufferedReader(new FileReader(playerArchive))) {
                while (bufferedReader.ready()) {
                    val line = bufferedReader.readLine();
                    if (line.startsWith("money")) {
                        accountBuilder.balance(NumberUtils.parse(line.split(" ")[1].replace("'", "")));
                    }

                    if (line.startsWith("last-account-name")) {
                        accountBuilder.username(line.split(" ")[1]);
                    }

                    if (line.startsWith("accepting-pay")) {
                        accountBuilder.receiveCoins(line.split(" ")[1].equals("true"));
                    }
                }
            } catch (Exception ignored) {}

            accounts.add(accountBuilder.result());
        }

        return accounts;
    }

}
