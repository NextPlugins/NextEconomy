package com.nextplugins.economy.api.group.impl;

import com.nextplugins.economy.api.group.GroupWrapper;
import com.nextplugins.economy.util.ColorUtil;
import lombok.val;
import ru.tehkode.permissions.bukkit.PermissionsEx;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class PermissionsExGroupWrapper implements GroupWrapper {

    @Override
    public String getPrefix(String player) {
        val user = PermissionsEx.getUser(player);
        if (user == null) return "";

        val groups = user.getGroups();
        if (groups.length < 1) return "";

        return ColorUtil.colored(groups[0].getPrefix());
    }

    @Override
    public void setup() {

    }
}
