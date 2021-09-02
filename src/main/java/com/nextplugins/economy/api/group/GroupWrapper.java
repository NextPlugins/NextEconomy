package com.nextplugins.economy.api.group;

import com.nextplugins.economy.util.ColorUtil;
import lombok.Getter;

public interface GroupWrapper {

    Group getGroup(String player);
    void setup();

    @Getter
    class Group {

        private final String prefix;
        private final String suffix;

        public Group() {
            prefix = "";
            suffix = "";
        }

        public Group(String prefix, String suffix) {
            this.prefix = ColorUtil.colored(prefix);
            this.suffix = ColorUtil.colored(suffix);
        }

    }
}
