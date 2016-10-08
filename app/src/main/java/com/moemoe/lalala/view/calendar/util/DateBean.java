package com.moemoe.lalala.view.calendar.util;

import com.moemoe.lalala.data.CalendarEvent;

import java.util.Date;

/**
 * Created by Haru on 2016/4/26 0026.
 */
public class DateBean {
    public final static int LAST_MONTH = -1;
    public final static int CURRENT_MONTH = 0;
    public final static int NEXT_MONTH = 1;

    private Date date;

    /**
     * 是否当前天
     */
    private boolean isCurrentDay;
    /**
     * 是否当前显示的月份
     * -1   上一个月
     * 0   当前显示月
     * 1   下一个月
     */
    private int monthType = 0;
    private CalendarEvent tag;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isCurrentDay() {
        return isCurrentDay;
    }

    public void setIsCurrentDay(boolean isCurrentDay) {
        this.isCurrentDay = isCurrentDay;
    }

    public int getMonthType() {
        return monthType;
    }

    public void setMonthType(int monthType) {
        this.monthType = monthType;
    }

    public CalendarEvent getTag() {
        return tag;
    }

    public void setTag(CalendarEvent tag) {
        this.tag = tag;
    }
}
