package com.nextplugins.economy.api.conversor.impl.jheyson;

import com.google.common.collect.Sets;
import com.nextplugins.economy.api.conversor.Conversor;
import com.nextplugins.economy.api.model.account.Account;
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
public class JHEconomyConversor extends Conversor {

    @Override
    public Set<Account> lookupPlayers() {
        if (getExecutor() == null) {
            val file = new File("/plugins/JH_Economy/contas.save");
            if (!file.exists()) return Sets.newHashSet();

            try (val bufferedReader = new BufferedReader(new FileReader(file))) {
                val accounts = new HashSet<Account>();
                while (bufferedReader.ready()) {
                    val line = bufferedReader.readLine();

                    val split = line.split(";");
                    val username = split[1];

                    val balance = NumberUtils.parse(split[0].split(" ")[1]);
                    if (NumberUtils.isInvalid(balance)) continue;

                    accounts.add(Account.generate()
                            .username(username)
                            .balance(balance)
                            .result()
                    );

                }

                return accounts;
            } catch (Exception exception) {
                return Sets.newHashSet();
            }
        }

        return getExecutor().resultManyQuery(
                "SELECT * FROM " + getTable(),
                statement -> {},
                JHEconomyAdapter.class
        );
    }
}
