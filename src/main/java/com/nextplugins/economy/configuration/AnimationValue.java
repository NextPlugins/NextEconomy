package com.nextplugins.economy.configuration;

import com.henryfabio.minecraft.configinjector.common.annotations.ConfigFile;
import com.henryfabio.minecraft.configinjector.common.annotations.ConfigSection;
import com.henryfabio.minecraft.configinjector.common.annotations.TranslateColors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@TranslateColors
@Accessors(fluent = true)
@ConfigSection("ranking.visual.animations")
@ConfigFile("ranking.yml")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnimationValue {

    private boolean enable;

}
