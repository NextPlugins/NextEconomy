package com.nextplugins.economy.task;

import com.nextplugins.economy.api.PurseAPI;
import com.nextplugins.economy.configuration.PurseValue;
import lombok.val;

import java.util.Random;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

public final class AsyncPurseUpdateTask implements Runnable {

    private final Random RANDOM = new Random();
    private final PurseAPI API = PurseAPI.getInstance();

    @Override
    public void run() {

        val maxValue = PurseValue.get(PurseValue::maxValue);
        val minValue = PurseValue.get(PurseValue::minValue);

        val randomValue = RANDOM.ints(minValue, maxValue + 1).iterator().nextInt();
        API.updatePurse(randomValue);

    }
}
