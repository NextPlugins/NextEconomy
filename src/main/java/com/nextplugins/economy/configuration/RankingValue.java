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
public final class RankingValue implements ConfigurationInjectable {

    @Getter private static final RankingValue instance = new RankingValue();

    // ranking

    @ConfigField("update-delay") private int updateDelay;
    @ConfigField("limit") private int rankingLimit;

    // model

    @ConfigField("model.type") private String rankingType;

    // inventory model

    @ConfigField("model.inventory.title") private String inventoryModelTitle;
    @ConfigField("model.inventory.head-display-name-top") private String inventoryModelHeadDisplayNameTop;
    @ConfigField("model.inventory.head-display-name") private String inventoryModelHeadDisplayName;
    @ConfigField("model.inventory.head-lore") private List<String> inventoryModelHeadLore;

    // npc

    @ConfigField("npc.type") private String npcType;
    @ConfigField("npc.height") private String hologramHeight;
    @ConfigField("npc.armor.chestplate") private String chestplateRGB;
    @ConfigField("npc.armor.leggings") private String leggingsRGB;
    @ConfigField("npc.armor.boots") private String bootsRGB;
    @ConfigField("npc.messages.hologram.limit") private int hologramDefaultLimit;
    @ConfigField("npc.messages.hologram.line") private String hologramDefaultLine;
    @ConfigField("npc.messages.hologram.modal") private List<String> hologramDefaultLines;
    @ConfigField("npc.messages.default.modal") private List<String> hologramArmorStandLines;

    // tycoon

    @ConfigField("tycoon.tag") private String tycoonTagValue;
    @ConfigField("tycoon.rich") private String tycoonRichTagValue;

    public static <T> T get(Function<RankingValue, T> function) {
        return function.apply(instance);
    }

}
