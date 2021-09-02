package com.nextplugins.economy.api.group.impl;

import com.nextplugins.economy.api.group.Group;
import com.nextplugins.economy.api.group.GroupWrapper;
import lombok.val;
import ru.tehkode.permissions.bukkit.PermissionsEx;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class PermissionsExGroupWrapper implements GroupWrapper {

    @Override
    public Group getGroup(String player) {
        val user = PermissionsEx.getUser(player);
        if (user == null) return new Group();

        val groups = user.getGroups();
        if (groups.length < 1) return new Group();

        return new Group(groups[0].getPrefix(), groups[0].getSuffix());
    }

    @Override
    public void setup() {

    }
}
