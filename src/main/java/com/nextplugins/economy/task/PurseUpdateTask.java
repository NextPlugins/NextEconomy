package com.nextplugins.economy.task;

import com.nextplugins.economy.api.PurseAPI;
import com.nextplugins.economy.configuration.values.PurseValue;
import lombok.val;

import java.util.Random;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

public final class PurseUpdateTask implements Runnable {

    private static final Random RANDOM = new Random();

    @Override
    public void run() {

        val api = PurseAPI.getInstance();
        if (api == null) return;

        val maxValue = PurseValue.get(PurseValue::maxValue);
        val minValue = PurseValue.get(PurseValue::minValue);

        val randomValue = RANDOM.ints(minValue, maxValue + 1).iterator().nextInt();
        api.updatePurse(randomValue);

    }
}
