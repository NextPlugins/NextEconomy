package com.nextplugins.economy.api.group;

import com.nextplugins.economy.util.ColorUtil;
import lombok.Getter;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
@Getter
public class Group {

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
