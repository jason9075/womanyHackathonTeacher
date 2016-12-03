package com.jason9075.womanyhackathonteacher.Utils;

import android.support.annotation.NonNull;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by jason9075 on 2016/12/4.
 */

public enum DateFormatCached {
    INSTANCE;

    private Map<String, SimpleDateFormat> simpleDateCached = new HashMap<>();
    private Map<String, DateTimeFormatter> dateTimeFormatterCached = new HashMap<>();

    public SimpleDateFormat getFormat(@NonNull String formatString) {
        if (simpleDateCached.get(formatString) == null) {
            SimpleDateFormat formatter = new SimpleDateFormat(formatString, Locale.getDefault());
            simpleDateCached.put(formatString, formatter);
            return formatter;
        }

        return simpleDateCached.get(formatString);
    }

    public DateTimeFormatter getDateTimeByString(@NonNull String formatString) {
        if (dateTimeFormatterCached.get(formatString) == null) {
            DateTimeFormatter formatter = DateTimeFormat.forPattern(formatString);
            dateTimeFormatterCached.put(formatString, formatter);
            return formatter;
        }

        return dateTimeFormatterCached.get(formatString);
    }
}
