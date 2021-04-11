package com.nextplugins.economy.api.model.account.old.adapter;

import com.henryfabio.sqlprovider.executor.adapter.SQLResultAdapter;
import com.henryfabio.sqlprovider.executor.result.SimpleResultSet;
import com.nextplugins.economy.api.model.account.old.OldAccount;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class OldAccountAdapter implements SQLResultAdapter<OldAccount> {

    @Override
    public OldAccount adaptResult(SimpleResultSet resultSet) {

        String owner = resultSet.get("owner");
        if (!owner.contains("-")) return null;

        UUID uuid = UUID.fromString(owner);

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (offlinePlayer == null) return null;

        return OldAccount.builder()
                .uuid(uuid)
                .name(offlinePlayer.getName())
                .balance(resultSet.get("balance"))
                .build();

    }

}
