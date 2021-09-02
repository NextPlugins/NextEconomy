package com.nextplugins.economy.api.group.impl;

import com.nextplugins.economy.api.group.GroupWrapper;
import com.nextplugins.economy.util.ColorUtil;
import lombok.val;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.query.QueryOptions;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class LuckPermsGroupWrapper implements GroupWrapper {

    private static final LuckPerms LUCK_PERMS = LuckPermsProvider.get();

    @Override
    public Group getGroup(String player) {
        val uuid = LUCK_PERMS.getUserManager().lookupUniqueId(player).join();
        if (uuid == null) return new Group();

        val user = LUCK_PERMS.getUserManager().loadUser(uuid, player).join();
        if (user == null) return new Group();

        val data = user.getCachedData().getMetaData(QueryOptions.defaultContextualOptions());
        return new Group(data.getPrefix(), data.getSuffix());
    }

    @Override
    public void setup() {

    }
}
