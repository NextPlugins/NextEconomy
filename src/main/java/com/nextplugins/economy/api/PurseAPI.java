package com.nextplugins.economy.api;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.event.operations.AsyncPurseUpdateEvent;
import com.nextplugins.economy.configuration.values.MessageValue;
import com.nextplugins.economy.configuration.values.PurseValue;
import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PurseAPI {

    /**
     * Can be null if the user desactivate system
     */
    @Getter @Nullable private static PurseAPI instance = new PurseAPI();

    private int purse;
    private double purseMultiplier;
    @Setter private long nextUpdate;

    public static boolean isAvaliable() {

        if (PurseValue.get(PurseValue::enable)) return true;

        instance = null;
        return false;

    }

    public void setPurse(int purse) {
        this.purse = purse;
        this.purseMultiplier = purse / 100.0;
    }

    public String getPurseFormated() {
        return purse + "%";
    }

    public String getPurseFormatedWithIcon() {
        return purse + "% " + isHigh();
    }

    public String isHigh() {

        val media = PurseValue.get(PurseValue::media);
        if (purse >= media) {
            return MessageValue.get(MessageValue::valuedIcon) + " " + MessageValue.get(MessageValue::purseHigh);
        } else {
            return MessageValue.get(MessageValue::devaluedIcon) + " " + MessageValue.get(MessageValue::purseDown);
        }

    }

    public void updatePurse(int newValue) {

        Bukkit.getScheduler().runTaskAsynchronously(NextEconomy.getInstance(), () -> {

            val duration = PurseValue.get(PurseValue::nextUpdate);
            val nextUpdate = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(duration);

            AsyncPurseUpdateEvent asyncPurseUpdateEvent = new AsyncPurseUpdateEvent(
                    newValue,
                    purse,
                    Instant.now(),
                    nextUpdate
            );

            PluginManager pluginManager = Bukkit.getPluginManager();
            pluginManager.callEvent(asyncPurseUpdateEvent);

        });

    }

}
