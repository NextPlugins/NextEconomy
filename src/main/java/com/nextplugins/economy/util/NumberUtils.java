package com.nextplugins.economy.util;

import com.nextplugins.economy.configuration.MessageValue;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NumberUtils {

    private static final Pattern PATTERN = Pattern.compile("^(\\d+\\.?\\d*)(\\D+)");

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,###.#");
    private static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("#.##");

    public static String format(double value) {
        if (isInvalid(value)) return "0";

        if (MessageValue.get(MessageValue::formatType).equalsIgnoreCase("DECIMAL")) {
            return DECIMAL_FORMAT.format(value);
        }

        int index = 0;
        val format = MessageValue.get(MessageValue::currencyFormat);

        double tmp;
        while ((tmp = value / 1000) >= 1) {
            if (index + 1 == format.size()) break;
            value = tmp;
            ++index;
        }

        return NUMBER_FORMAT.format(value) + format.get(index);
    }

    public static double parse(String string) {
        try {

            val value = Double.parseDouble(string);
            return isInvalid(value) ? 0 : value;

        } catch (Exception ignored) {
        }

        if (MessageValue.get(MessageValue::formatType).equalsIgnoreCase("DECIMAL")) return 0;

        Matcher matcher = PATTERN.matcher(string);
        if (!matcher.find()) return 0;

        double amount = Double.parseDouble(matcher.group(1));
        String suffix = matcher.group(2);
        String fixedSuffix = suffix.equalsIgnoreCase("k") ? suffix.toLowerCase() : suffix.toUpperCase();

        int index = MessageValue.get(MessageValue::currencyFormat).indexOf(fixedSuffix);

        val value = amount * Math.pow(1000, index);
        return isInvalid(value) ? 0 : value;
    }

    public static boolean isInvalid(double value) {
        return value < 0 || Double.isNaN(value) || Double.isInfinite(value);
    }

}
