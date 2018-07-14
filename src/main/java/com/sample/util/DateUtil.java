package com.sample.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    public static String formatDateTOStr(Date date, String pattern){
        String str = " ";
        try{
            SimpleDateFormat spdf = new SimpleDateFormat(pattern);
            str  = spdf.format(date);
        }catch (Exception e){
            str = " ";
            e.printStackTrace();
        }
        return str;
    }
    public static String getCurrentTime () {
        Calendar calendar =Calendar.getInstance();
        return calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DATE) + " " +
                calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND);
    }
}
