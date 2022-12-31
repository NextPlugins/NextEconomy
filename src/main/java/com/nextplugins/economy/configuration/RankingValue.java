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

    @ConfigField("npc.enable") private boolean npcEnabled;
    @ConfigField("npc.format") private List<String> hologramFormat;
    @ConfigField("npc.extra") private List<String> extraFormat;

    // tycoon

    @ConfigField("tycoon.top.tag") private String tycoonTagValue;
    @ConfigField("tycoon.top.discordRoleId") private long tycoonRoleId;

    @ConfigField("tycoon.rich.tag") private String tycoonRichTagValue;
    @ConfigField("tycoon.rich.discordRoleId") private long tycoonRichRoleId;

    @ConfigField("tycoon.commands") private List<String> tycoonCommands;

    public static <T> T get(Function<RankingValue, T> function) {
        return function.apply(instance);
    }

}
