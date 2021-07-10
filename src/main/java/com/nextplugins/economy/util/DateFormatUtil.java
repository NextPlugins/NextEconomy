package com.nextplugins.economy.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateFormatUtil {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public static String of(long milli) {

        Date date = new Date(milli);
        return DATE_FORMAT.format(date).replace(" ", " às ");

    }

}
