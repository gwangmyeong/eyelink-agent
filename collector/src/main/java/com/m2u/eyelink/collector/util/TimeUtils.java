package com.m2u.eyelink.collector.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public final class TimeUtils {
    private TimeUtils() {
    }

    public static long reverseTimeMillis(long currentTimeMillis) {
        return Long.MAX_VALUE - currentTimeMillis;
    }

    public static long reverseCurrentTimeMillis() {
        return reverseTimeMillis(System.currentTimeMillis());
    }

    public static long recoveryTimeMillis(long reverseCurrentTimeMillis) {
        return Long.MAX_VALUE - reverseCurrentTimeMillis;
    }
    
    public static String convertEpochToDate(long epochTimeStamp) {
	    	Date date = new Date ();
	    	date.setTime((long)epochTimeStamp);
	    	DateFormat sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
	    	sf.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
	    	return sf.format(date).toString();
    }
}
