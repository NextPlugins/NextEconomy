package com.nextplugins.economy.api.conversor.impl.soekd;

import com.google.gson.Gson;
import com.henryfabio.sqlprovider.executor.adapter.SQLResultAdapter;
import com.henryfabio.sqlprovider.executor.result.SimpleResultSet;
import com.nextplugins.economy.api.model.account.Account;
import lombok.val;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class SOEconomyAdapter implements SQLResultAdapter<Account> {

    private final Gson accountParser = new Gson();

    @Override
    public Account adaptResult(SimpleResultSet resultSet) {
        String json = resultSet.get("json");
        val user = accountParser.fromJson(json, SOEconomyUser.class);

        return Account.generate()
                .username(user.getPlayerName())
                .balance(user.getMoney())
                .receiveCoins(user.isTogglePayment())
                .result();
    }

}
