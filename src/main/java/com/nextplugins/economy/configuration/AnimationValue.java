package com.nextplugins.economy.configuration;

import com.henryfabio.minecraft.configinjector.common.annotations.ConfigField;
import com.henryfabio.minecraft.configinjector.common.annotations.ConfigFile;
import com.henryfabio.minecraft.configinjector.common.annotations.ConfigSection;
import com.henryfabio.minecraft.configinjector.common.annotations.TranslateColors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.function.Function;

@Getter
@TranslateColors
@Accessors(fluent = true)
@ConfigSection("ranking.visual.animations")
@ConfigFile("ranking.yml")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnimationValue {

    @Getter private static final AnimationValue instance = new AnimationValue();

    @ConfigField("enable") private boolean enable;

    @ConfigField("spawn") private String spawnEmote;
    @ConfigField("rage-dance") private String rageDance;

    @ConfigField("show-npc") private List<String> showNpcEmotes;
    @ConfigField("magnata-annimations-id-list") private List<String> magnataEmotes;

    public static <T> T get(Function<AnimationValue, T> function) {
        return function.apply(instance);
    }

}
