package com.nextplugins.economy.api;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.event.operations.AsyncPurseUpdateEvent;
import com.nextplugins.economy.configuration.MessageValue;
import com.nextplugins.economy.configuration.PurseValue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
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
    @Nullable private static PurseAPI instance;

    private static final Random RANDOM = new Random();

    private int purse;
    private double purseMultiplier;
    @Setter private long nextUpdate;

    public static boolean init() {

        if (!PurseValue.get(PurseValue::enable)) return false;

        instance = new PurseAPI();
        return true;

    }

    public static boolean isAvaliable() {
        return instance != null;
    }

    public static PurseAPI getInstance() {

        if (instance != null && instance.getNextUpdate() < System.currentTimeMillis()) instance.forceUpdate();
        return instance;

    }

    public void forceUpdate() {

        val maxValue = PurseValue.get(PurseValue::maxValue);
        val minValue = PurseValue.get(PurseValue::minValue);

        val randomValue = RANDOM.ints(minValue, maxValue + 1).iterator().nextInt();
        updatePurse(randomValue);

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

        val lastNextUpdate = nextUpdate;

        val duration = PurseValue.get(PurseValue::nextUpdate);
        setNextUpdate(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(duration));

        Bukkit.getScheduler().runTaskAsynchronously(NextEconomy.getInstance(), () -> {

            AsyncPurseUpdateEvent asyncPurseUpdateEvent = new AsyncPurseUpdateEvent(
                    newValue,
                    purse,
                    lastNextUpdate
            );

            PluginManager pluginManager = Bukkit.getPluginManager();
            pluginManager.callEvent(asyncPurseUpdateEvent);

        });

    }

}
