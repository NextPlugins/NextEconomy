package com.nextplugins.economy.configuration;

import com.henryfabio.minecraft.configinjector.common.annotations.ConfigField;
import com.henryfabio.minecraft.configinjector.common.annotations.ConfigFile;
import com.henryfabio.minecraft.configinjector.common.annotations.ConfigSection;
import com.henryfabio.minecraft.configinjector.common.annotations.TranslateColors;
import com.henryfabio.minecraft.configinjector.common.injector.ConfigurationInjectable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.function.Function;

@Getter
@TranslateColors
@Accessors(fluent = true)
@ConfigSection("ranking")
@ConfigFile("ranking.yml")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RankingConfiguration implements ConfigurationInjectable {

    @Getter private static final RankingConfiguration instance = new RankingConfiguration();

    // ranking

    @ConfigField("update-delay") private int updateDelay;
    @ConfigField("limit") private int rankingLimit;

    // model

    @ConfigField("model.type") private String rankingType;

    // chat model

    @ConfigField("model.chat.header") private List<String> chatModelHeader;
    @ConfigField("model.chat.body") private String chatModelBody;
    @ConfigField("model.chat.footer") private List<String> chatModelFooter;

    // inventory model

    @ConfigField("model.inventory.title") private String inventoryModelTitle;
    @ConfigField("model.inventory.head-display-name") private String inventoryModelHeadDisplayName;
    @ConfigField("model.inventory.head-lore") private List<String> inventoryModelHeadLore;

    // npc

    @ConfigField("npc.hologram.height") private int hologramHeight;
    @ConfigField("npc.hologram.lines") private List<String> hologramLines;
    @ConfigField("npc.locations") private List<String> npcLocations;

    public static <T> T get(Function<RankingConfiguration, T> function) {
        return function.apply(instance);
    }

}