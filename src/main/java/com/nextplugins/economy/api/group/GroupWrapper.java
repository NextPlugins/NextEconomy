package com.nextplugins.economy.api.group;

import com.nextplugins.economy.util.ColorUtil;
import lombok.Getter;

public interface GroupWrapper {

    Group getGroup(String player);
    void setup();

}
