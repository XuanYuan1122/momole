package com.moemoe.lalala.view.calendar.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Haru on 2016/4/26 0026.
 */
public class OtherUtils {
    /**
     * 格式(yyyy-MM-dd)
     */
    public static final String DATE_PATTERN_1 = "yyyy-MM-dd";
    /**
     * 格式(yyyy-MM)
     */
    public static final String DATE_PATTERN_2 = "yyyy-MM";

    public static StackTraceElement getCallerStackTraceElement() {
        return Thread.currentThread().getStackTrace()[4];
    }

    /**
     * 根据指定的格式对日期进行格式化处理
     *
     * @param date 日期
     * @return 日期
     */
    public static String formatMonth(Date date) {
        if (null == date) {
            return "";
        }
        SimpleDateFormat df = (SimpleDateFormat) DateFormat.getDateInstance();
        df.applyPattern("yyyy-MM");
        return df.format(date);
    }

    /**
     * 根据指定的格式对日期进行格式化处理
     *
     * @param date 日期
     * @return 日期
     */
    public static String formatDate(Date date) {
        if (null == date) {
            return "";
        }
        SimpleDateFormat df = (SimpleDateFormat) DateFormat.getDateInstance();
        df.applyPattern("yyyy-MM-dd");
        return df.format(date);
    }
    /**
     * 根据指定的格式对日期进行格式化处理
     *
     * @param date   日期
     * @param format 格式
     * @return 日期
     */
    public static String formatDate(Date date, String format) {
        if (null == date) {
            return null;
        }
        SimpleDateFormat df = (SimpleDateFormat) DateFormat.getDateInstance();
        df.applyPattern(format);
        return df.format(date);
    }

}
