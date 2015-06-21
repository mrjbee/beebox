package org.monroe.team.corebox.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    public static String msAsString(){
        return Long.toString(System.currentTimeMillis());
    }

    public static Date now(){
        return new Date();
    }

    public static Date dateOnly(Date date){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        try {
           return formatter.parse(formatter.format(date));
        } catch (ParseException e) {
            throw new RuntimeException();
        }
    }

    public static Date monthOnly(Date now) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/yyyy");
        try {
            return formatter.parse(formatter.format(now));
        } catch (ParseException e) {
            throw new RuntimeException();
        }
    }


    public static long timeOnly(Date date) {
        return date.getTime() - dateOnly(date).getTime();
    }

    public static Date mathDays(Date date, int daysCount) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, daysCount);
        return cal.getTime();
    }

    public static Date mathMinutes(Date date, int minutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, minutes);
        return cal.getTime();
    }

    public static Date mathMonth(Date date, int month) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, month);
        return cal.getTime();
    }

    public static Date mathWeek(Date date, int weeks) {
        return mathDays(date, weeks*7);
    }

    public static long[] splitPeriod(Date endDate, Date startDate) {
        long rest = endDate.getTime() - startDate.getTime();
        return splitperiod(rest);
    }

    public static long[] splitperiod(long periodMs) {
        final long days = periodMs / (24*60*60*1000);
        periodMs = periodMs % (24*60*60*1000);
        long hours = periodMs / (60*60*1000);
        periodMs = periodMs % (60*60*1000);
        long minutes = periodMs / (msSeconds(60));
        periodMs = periodMs % (msSeconds(60));
        long seconds = periodMs / 1000;
        periodMs = periodMs % (1000);
        return new long[]{days,hours,minutes,seconds, periodMs};
    }


    public static long asHours(long ms) {
        return ms/(60*60*1000);
    }
    public static long asMinutes(long ms) {
        return ms/(msSeconds(60));
    }
    public static long asSeconds(long ms) {
        return ms/1000;
    }
    public static long asDays(long ms, boolean fullDays) {
        double count = ((double)ms) / (24d*60d*60d*1000d);
        if (fullDays) return (long) count;
        else return Math.round(count);
    }

    public static Date today() {
        return dateOnly(now());
    }

    public static boolean isToday(Date probeDate) {
        return 0 == today().compareTo(dateOnly(probeDate));
    }

    public static long msHour(long hourCount) {
        return hourCount * msMinutes(60);
    }

    public static long msMinutes(long minutesCount) {
        return minutesCount * msSeconds(60);
    }

    public static long msSeconds(long secondsCount) {
        return secondsCount * 1000;
    }
}
