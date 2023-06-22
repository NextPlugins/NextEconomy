package com.nextplugins.economy.model.ranking;

import com.nextplugins.economy.model.ranking.impl.CMIHolder;
import com.nextplugins.economy.model.ranking.impl.DecentHologramsHolder;
import com.nextplugins.economy.model.ranking.impl.HolographicDisplaysHolder;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author <a href="https://github.com/Yuhtin">Yuhtin</a>
 */
@Getter
@AllArgsConstructor
public enum HologramSupportType {

    HOLOGRAPHIC_DISPLAYS("HolographicDisplays", new HolographicDisplaysHolder(), true),
    DECENT_HOLOGRAMS("DecentHolograms", new DecentHologramsHolder(), false),
    CMI("CMI", new CMIHolder(), true),
    NONE("", null, false);

    private final String pluginName;
    private final IHologramHolder holder;
    private final boolean shutdownCompatible;

}
