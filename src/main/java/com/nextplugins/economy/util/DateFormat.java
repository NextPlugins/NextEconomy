package com.nextplugins.economy.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateFormat {

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public static String of(long milli) {

        Date date = new Date(milli);
        return simpleDateFormat.format(date);

    }

}
