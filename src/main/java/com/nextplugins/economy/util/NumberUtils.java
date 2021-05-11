package com.nextplugins.economy.util;

import com.nextplugins.economy.configuration.MessageValue;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NumberUtils {

    private static final Pattern PATTERN = Pattern.compile("^(\\d+\\.?\\d*)(\\D+)");

    private final static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

    public static String format(double value) {

        int index = 0;

        double tmp;
        while ((tmp = value / 1000) >= 1) {
            value = tmp;
            ++index;
        }

        return DECIMAL_FORMAT.format(value) + MessageValue.get(MessageValue::currencyFormat).get(index);

    }

    public static double parse(String string) {
        try {
            return Double.parseDouble(string);
        } catch (Exception ignored) {}

        Matcher matcher = PATTERN.matcher(string);
        if (!matcher.find()) return -1;

        double amount = Double.parseDouble(matcher.group(1));
        String suffix = matcher.group(2);

        int index = MessageValue.get(MessageValue::currencyFormat).indexOf(suffix.toUpperCase());

        return amount * Math.pow(1000, index);
    }

}
