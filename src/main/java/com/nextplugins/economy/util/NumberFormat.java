package com.nextplugins.economy.util;

import com.nextplugins.economy.configuration.values.FeatureValue;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public final class NumberFormat {

    private final static DecimalFormat numberFormat = new DecimalFormat("#,###.#");
    private final static List<String> chars = Arrays.asList("", "K", "M", "B", "T", "Q", "QQ", "S", "SS", "O", "N", "D",
            "UN", "DD", "TR", "QT", "QN", "SD", "SPD", "OD", "ND", "VG", "UVG", "DVG", "TVG", "QTV");
    private static final String FORMAT_TYPE = FeatureValue.get(FeatureValue::formatType);

    public static String letterFormat(double number) {
        int index = 0;
        while (number / 1000.0D >= 1.0D) {
            number /= 1000.0D;
            index++;
        }

        String character = index < chars.size() ? chars.get(index) : "";
        return numberFormat.format(number) + character;
    }

    public static String decimalFormat(double number) {
        return numberFormat.format(number);
    }

    public static String format(double number) {
        if (FORMAT_TYPE.equalsIgnoreCase("decimal")) {
            return decimalFormat(number);
        } else if (FORMAT_TYPE.equalsIgnoreCase("letter")) {
            return letterFormat(number);
        } else {
            throw new IllegalArgumentException("Opção de formatação inválida: " + FORMAT_TYPE);
        }
    }

}
