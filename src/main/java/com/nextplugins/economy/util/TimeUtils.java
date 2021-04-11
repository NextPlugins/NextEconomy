package com.nextplugins.economy.util;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum TimeUtils {

    WEEK(604800000, "weeks", "week", "w", "semana", "semanas"),
    DAY(86400000, "days", "day", "d", "dia", "dias"),
    HOUR(3600000, "hours", "hour", "h", "hora", "horas"),
    MINUTE(60000, "minutes", "minute", "m", "minuto", "minutos"),
    SECOND(1000, "seconds", "second", "s", "segundo", "segundos");

    private static final Pattern PATTERN = Pattern.compile("(\\d+)([a-zA-Z]+)");
    private final long millis;
    private final String[] formats;

    TimeUtils(long millis, String... formats) {
        this.millis = millis;
        this.formats = formats;
    }

    public static String format(long time) {
        if (time == 0) return "0s";

        long days = TimeUnit.MILLISECONDS.toDays(time);
        long hours = TimeUnit.MILLISECONDS.toHours(time) - (days * 24);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(time) - (TimeUnit.MILLISECONDS.toHours(time) * 60);
        long second = TimeUnit.MILLISECONDS.toSeconds(time) - (TimeUnit.MILLISECONDS.toMinutes(time) * 60);

        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(plural(days, "dia"));
        if (hours > 0) sb.append(plural(hours, "hora"));
        if (minutes > 0) sb.append(plural(minutes, "minuto"));
        if (second > 0) sb.append(plural(second, "segundo"));

        String s = sb.toString();
        return s.isEmpty() ? "alguns instantes" : s;
    }

    public static Long getTime(String string) {
        Matcher matcher = PATTERN.matcher(string);
        long time = 0;

        while (matcher.find()) {
            try {
                int value = Integer.parseInt(matcher.group(1));
                TimeUtils type = fromFormats(matcher.group(2));
                if (type != null) {
                    time += (value * type.getMillis());
                }
            } catch (Exception ignored) {
            }
        }

        return time;
    }

    public static String formatTime(long time) {
        if (time == 0) return "alguns instantes";

        long days = TimeUnit.MILLISECONDS.toDays(time);
        long hours = TimeUnit.MILLISECONDS.toHours(time) - (days * 24);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(time) - (TimeUnit.MILLISECONDS.toHours(time) * 60);
        long second = TimeUnit.MILLISECONDS.toSeconds(time) - (TimeUnit.MILLISECONDS.toMinutes(time) * 60);

        StringBuilder sb = new StringBuilder();

        boolean hasDays = days > 0;
        boolean hasHours = hours > 0;
        boolean hasMinutes = minutes > 0;
        boolean hasSeconds = second > 0;

        if (hasDays) {

            sb.append(plural(days, "dia"));
            if (hasHours) sb.append(hasMinutes ? ", " : " e ");

        }

        if (hasHours) {

            sb.append(plural(hours, "hora"));
            if (hasMinutes) sb.append(hasSeconds ? ", " : " e ");

        }

        if (hasMinutes) {

            sb.append(plural(minutes, "minuto"));
            if (hasSeconds) sb.append(" e ");

        }

        if (hasSeconds) sb.append(plural(second, "segundo"));

        String s = sb.toString();
        return s.isEmpty() ? "alguns instantes" : s;
    }

    public static String formatOne(long time) {

        if (time == 0) return "0s";

        long days = TimeUnit.MILLISECONDS.toDays(time);
        long hours = TimeUnit.MILLISECONDS.toHours(time) - (days * 24);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(time) - (TimeUnit.MILLISECONDS.toHours(time) * 60);
        long second = TimeUnit.MILLISECONDS.toSeconds(time) - (TimeUnit.MILLISECONDS.toMinutes(time) * 60);

        if (days > 0) return plural(days, "dia");
        if (hours > 0) return plural(hours, "hora");
        if (minutes > 0) return plural(minutes, "minuto");
        if (second > 0) return plural(second, "segundo");
        return "0s";

    }

    public static TimeUtils fromFormats(String format) {
        return Arrays.stream(values())
                .filter(type -> Arrays.asList(type.getFormats()).contains(format.toLowerCase()))
                .findFirst()
                .orElse(null);
    }

    public static String plural(long quantity, String message) {
        return quantity + " " + message + (quantity == 1 ? "" : "s");
    }

    public long getMillis() {
        return millis;
    }

    public String[] getFormats() {
        return formats;
    }
}
