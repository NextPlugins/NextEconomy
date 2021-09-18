package com.nextplugins.economy.api.conversor.impl.iconomy;

import com.google.common.collect.Sets;
import com.henryfabio.sqlprovider.executor.SQLExecutor;
import com.nextplugins.economy.api.conversor.Conversor;
import com.nextplugins.economy.api.model.account.Account;
import com.nextplugins.economy.util.NumberUtils;
import lombok.val;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class IConomyConversor extends Conversor {

    private final String fileName;

    protected IConomyConversor(String fileName) {
        super("iConomy", null, null);
        this.fileName = fileName;
    }

    @Override
    public Set<Account> lookupPlayers() {
        val file = new File("/plugins/iConomy/" + fileName);
        if (!file.exists()) return Sets.newHashSet();

        try (val bufferedReader = new BufferedReader(new FileReader(file))) {
            val accounts = new HashSet<Account>();
            while (bufferedReader.ready()) {

                val line = bufferedReader.readLine();
                val split = line.split(" ");

                val user = Bukkit.getOfflinePlayer(split[0]);
                if (user == null || !user.hasPlayedBefore()) continue;

                val balance = NumberUtils.parse(split[1].split(":")[1]);
                if (NumberUtils.isInvalid(balance)) continue;

                accounts.add(Account.generate()
                        .username(user.getName())
                        .balance(balance)
                        .result()
                );

            }

            return accounts;
        } catch (Exception exception) {
            return Sets.newHashSet();
        }
    }
}
