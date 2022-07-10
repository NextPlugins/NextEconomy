package com.nextplugins.economy.api.group.impl;

import com.nextplugins.economy.api.group.Group;
import com.nextplugins.economy.api.group.GroupWrapper;
import com.nextplugins.testserver.core.NextTestServer;
import com.nextplugins.testserver.core.api.model.player.storage.UserStorage;
import lombok.val;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class NextTestServerGroupWrapper implements GroupWrapper {

    private UserStorage userStorage;

    @Override
    public Group getGroup(String player) {
        val user = userStorage.findUserByName(player);
        if (user == null) return new Group();

        val group = user.getGroup();
        return new Group(group.getPrefix(), "");
    }

    @Override
    public void setup() {
        userStorage = NextTestServer.getInstance().getUserStorage();
    }
}
