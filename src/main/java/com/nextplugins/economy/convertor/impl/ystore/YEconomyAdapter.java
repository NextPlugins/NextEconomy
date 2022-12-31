package com.nextplugins.economy.convertor.impl.ystore;

import com.google.gson.Gson;
import com.henryfabio.sqlprovider.executor.adapter.SQLResultAdapter;
import com.henryfabio.sqlprovider.executor.result.SimpleResultSet;
import com.nextplugins.economy.model.account.Account;
import lombok.val;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class YEconomyAdapter implements SQLResultAdapter<Account> {

    private final Gson userParser = new Gson();

    @Override
    public Account adaptResult(SimpleResultSet resultSet) {
        String json = resultSet.get("json");
        val user = userParser.fromJson(json, YEconomyUser.class);

        return Account.generate()
                .username(user.getNome())
                .balance(user.getMoney())
                .receiveCoins(user.isRecebimento())
                .result();
    }
}
