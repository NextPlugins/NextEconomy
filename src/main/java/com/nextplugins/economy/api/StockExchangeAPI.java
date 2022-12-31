package com.nextplugins.economy.api;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.event.operations.StockExchangeUpdateEvent;
import com.nextplugins.economy.configuration.MessageValue;
import com.nextplugins.economy.configuration.RankingValue;
import com.nextplugins.economy.configuration.StockExchangeValue;
import org.bukkit.Bukkit;

import java.util.Random;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

public class StockExchangeAPI {

    /**
     * Can be null if the user desactivate system
     */
    private static final StockExchangeAPI instance = new StockExchangeAPI();

    private final Random random;
    private final boolean enabled;
    private final int maxValue;
    private final int minValue;
    private final int average;

    private double multiplier;
    private int previousValue;
    private int value;

    private StockExchangeAPI() {
        this.random = new Random();
        this.enabled = StockExchangeValue.get(StockExchangeValue::enable);

        this.maxValue = StockExchangeValue.get(StockExchangeValue::maxValue);
        this.minValue = StockExchangeValue.get(StockExchangeValue::minValue);
        this.average = StockExchangeValue.get(StockExchangeValue::average);
    }

    public Random getRandom() {
        return random;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void update() {
        if (!isEnabled()) return;

        final int updatedValue = random.ints(minValue, (maxValue + 1)).iterator().nextInt();

        setPreviousValue(getValue());
        setValue(updatedValue);
        setMultiplier(updatedValue / 100);

        Bukkit.getPluginManager().callEvent(new StockExchangeUpdateEvent(
                updatedValue,
                getPreviousValue()
        ));
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public String getValueAsPercentage() {
        return value + "%";
    }

    public String getValueFormatted() {
        String icon;

        if (value >= average) {
            icon = MessageValue.get(MessageValue::valuedIcon);
        } else {
            icon = MessageValue.get(MessageValue::devaluedIcon);
        }

        return value + "% " + icon;
    }

    public void setPreviousValue(int previousValue) {
        this.previousValue = previousValue;
    }

    public int getPreviousValue() {
        return previousValue;
    }

    public static StockExchangeAPI getInstance() {
        return instance;
    }

}
