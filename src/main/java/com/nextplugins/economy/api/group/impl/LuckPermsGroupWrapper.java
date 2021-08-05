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
    public String getPrefix(String player) {

        val user = LUCK_PERMS.getUserManager().getUser(player);
        if (user == null) return "";

        val prefix = user.getCachedData().getMetaData(QueryOptions.defaultContextualOptions()).getPrefix();
        if (prefix == null) return "";

        return ColorUtil.colored(prefix);
    }

    @Override
    public void setup() {

    }
}
