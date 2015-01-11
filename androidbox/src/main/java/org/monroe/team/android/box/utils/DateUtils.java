package org.monroe.team.android.box.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.SimpleFormatter;

public class DateUtils {

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
        final long days = rest / (24*60*60*1000);
        rest = rest % (24*60*60*1000);
        long hours = rest / (60*60*1000);
        rest = rest % (60*60*1000);
        long minutes = rest / (60*1000);
        rest = rest % (60*1000);
        long seconds = rest / 1000;
        return new long[]{days,hours,minutes,seconds};
    }


    public static long asMinutes(long ms) {
        return ms/(60*1000);
    }


}
