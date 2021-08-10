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

    // chat model

    @ConfigField("model.chat.header") private List<String> chatModelHeader;
    @ConfigField("model.chat.body") private String chatModelBody;
    @ConfigField("model.chat.footer") private List<String> chatModelFooter;

    // inventory model

    @ConfigField("model.inventory.title") private String inventoryModelTitle;
    @ConfigField("model.inventory.head-display-name-top") private String inventoryModelHeadDisplayNameTop;
    @ConfigField("model.inventory.head-display-name") private String inventoryModelHeadDisplayName;
    @ConfigField("model.inventory.head-lore") private List<String> inventoryModelHeadLore;

    // npc

    @ConfigField("visual.type") private String npcType;
    @ConfigField("visual.format") private String hologramFormat;
    @ConfigField("visual.armor.chestplate") private String chestplateRGB;
    @ConfigField("visual.armor.leggings") private String leggingsRGB;
    @ConfigField("visual.armor.boots") private String bootsRGB;
    @ConfigField("visual.messages.hologram.limit") private int hologramDefaultLimit;
    @ConfigField("visual.messages.hologram.line") private String hologramDefaultLine;
    @ConfigField("visual.messages.hologram.modal") private List<String> hologramDefaultLines;
    @ConfigField("visual.messages.default.modal") private List<String> hologramArmorStandLines;

    // tycoon

    @ConfigField("tycoon.tag") private String tycoonTagValue;
    @ConfigField("tycoon.rich") private String tycoonRichTagValue;

    public static <T> T get(Function<RankingValue, T> function) {
        return function.apply(instance);
    }

}
