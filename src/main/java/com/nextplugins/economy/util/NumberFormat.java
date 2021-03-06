package com.nextplugins.economy.util;

import com.nextplugins.economy.configuration.values.FeatureValue;

import java.text.DecimalFormat;
import java.util.Arrays;

public final class NumberFormat {

    private final static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,###.#");
    private static final String[] CHARS = new String[]{"", "K", "M", "B", "T", "Q", "QQ", "S", "SS", "O", "N", "D",
            "UN", "DD", "TR", "QT", "QN", "SD", "SPD", "OD", "ND", "VG", "UVG", "DVG", "TVG", "QTV"};

    private static final String FORMAT_TYPE = FeatureValue.get(FeatureValue::formatType);

    public static String decimalFormat(double number) {
        return DECIMAL_FORMAT.format(number);
    }

    public static boolean isLetterFormat() {
        return FORMAT_TYPE.equalsIgnoreCase("letter");
    }

    public static String format(double number) {
        return isLetterFormat() ? letterFormat(number) : decimalFormat(number);
    }

    private static String letterFormat(double value) {

        String result = String.valueOf(value);

        int prefixIndex = (int) Math.log10(value) / 3;
        if (prefixIndex > CHARS.length) prefixIndex = CHARS.length;
        if (prefixIndex > 0)
            result = DECIMAL_FORMAT.format(value / Math.pow(10, prefixIndex * 3)) + CHARS[prefixIndex - 1];

        return result;

    }

    public static double parse(String string) {
        try {

            double result = Double.parseDouble(string.replaceAll("[^0-9.]", ""));

            int prefixIndex = Arrays.asList(CHARS).indexOf(string.replaceAll("[^a-zA-Z]","").toUpperCase()) + 1;
            if (prefixIndex > 0) result *= Math.pow(10, prefixIndex * 3);

            return result;

        } catch (Exception e) {
            return -1;
        }
    }

}
