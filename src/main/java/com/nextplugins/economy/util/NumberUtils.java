package com.nextplugins.economy.util;

import com.nextplugins.economy.configuration.FeatureValue;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NumberUtils {

    private static final Pattern PATTERN = Pattern.compile("^(\\d+\\.?\\d*)(\\D+)");

    private final static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");
    private static final List<String> CHARS = Arrays.asList("", "K", "M", "B", "T", "Q", "QQ", "S", "SS", "O", "N", "D",
            "UN", "DD", "TR", "QT", "QN", "SD", "SPD", "OD", "ND", "VG", "UVG", "DVG", "TVG", "QTV");

    public static String format(double number) {
        return isLetterFormat() ? letterFormat(number) : decimalFormat(number);
    }

    private static String decimalFormat(double number) {
        return DECIMAL_FORMAT.format(number);
    }

    private static boolean isLetterFormat() {
        return FeatureValue.get(FeatureValue::formatType).equalsIgnoreCase("letter");
    }

    private static String letterFormat(double value) {

        int index = 0;

        double tmp;
        while ((tmp = value / 1000) >= 1) {
            value = tmp;
            ++index;
        }

        return DECIMAL_FORMAT.format(value) + CHARS.get(index);

    }

    public static double parse(String string) {
        try {
            return Double.parseDouble(string);
        } catch (Exception ignored) {}

        Matcher matcher = PATTERN.matcher(string);
        if (!matcher.find()) return -1;

        double amount = Double.parseDouble(matcher.group(1));
        String suffix = matcher.group(2);

        int index = CHARS.indexOf(suffix.toUpperCase());

        return amount * Math.pow(1000, index);
    }

}
