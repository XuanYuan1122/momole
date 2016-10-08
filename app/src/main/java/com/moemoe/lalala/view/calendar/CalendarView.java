package com.moemoe.lalala.view.calendar;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.data.CalendarEvent;
import com.moemoe.lalala.view.calendar.adapter.CalendarAdapter;
import com.moemoe.lalala.view.calendar.adapter.TopViewPagerAdapter;
import com.moemoe.lalala.view.calendar.util.DateBean;
import com.moemoe.lalala.view.calendar.util.OtherUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * 日历View
 * Created by Haru on 2016/4/26 0026.
 */
public class CalendarView extends GridView {

    private CalendarAdapter calendarAdapter;

    public CalendarView(Context context, int jumpMonth, int year, int month) {
        super(context);
        initCalendarValues();
        setCalendarValues(context, jumpMonth, year, month);
    }

    private void setCalendarValues(Context context, int jumpMonth, int year, int month) {
//        queryDateList(year, month, jumpMonth);
        calendarAdapter = new CalendarAdapter(context, jumpMonth, year, month);
        setAdapter(calendarAdapter);
        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 点击任何一个item，得到这个item的日期(排除点击的是非当前月日期(点击不响应))
                DateBean dateBean = (DateBean) calendarAdapter.getItem(position);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dateBean.getDate());
                int i = calendar.get(Calendar.DAY_OF_MONTH);
                int m = calendar.get(Calendar.MONTH);
                ViewPager viewPager = (ViewPager) getParent();
                if (dateBean.getMonthType() == DateBean.CURRENT_MONTH) {
                    calendarAdapter.setColorDataPosition(position);
                    ((CalendarLayout) getParent().getParent()).setRowNum(position / 7);
                    if (onCalendarClickListener != null) {
                        onCalendarClickListener.onCalendarClick(position, dateBean);
                    }
                    ((TopViewPagerAdapter) viewPager.getAdapter()).setDay(position);
                    ((TopViewPagerAdapter) viewPager.getAdapter()).setSelectMonth(m);
                } else if (dateBean.getMonthType() == DateBean.LAST_MONTH) {
                    int n = i / 7;
                    position = n * 7 + position;
                    int h = position - i + 1;
                    if (h < 0) {
                        position = 7 + position;
                    }
                    if (onCalendarClickListener != null) {
                        onCalendarClickListener.onCalendarClick(position, dateBean);
                    }
                    ((TopViewPagerAdapter) viewPager.getAdapter()).setDay(position);
                    ((TopViewPagerAdapter) viewPager.getAdapter()).setSelectMonth(m);
                    viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
                } else if (dateBean.getMonthType() == DateBean.NEXT_MONTH) {
                    int firstDayPosition = position - i + 1;
                    int dayPosition = firstDayPosition % 7 + i - 1;
                    if (onCalendarClickListener != null) {
                        onCalendarClickListener.onCalendarClick(dayPosition, dateBean);
                    }
                    ((TopViewPagerAdapter) viewPager.getAdapter()).setDay(dayPosition);
                    ((TopViewPagerAdapter) viewPager.getAdapter()).setSelectMonth(m);
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                }
            }
        });
    }

    public void refreshView(Context context, int jumpMonth, int year, int month) {
        setCalendarValues(context, jumpMonth, year, month);
        setEventDays(eventDays);
    }

    public String getCurrentDay() {
        DateBean firstDateBean = calendarAdapter.getFirstDateBean();
        if (firstDateBean != null) {
            return OtherUtils.formatMonth(firstDateBean.getDate());
        }
        return null;
    }

    public int getCurrentMonth(){
        DateBean firstDateBean = calendarAdapter.getFirstDateBean();
        if(firstDateBean != null){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(firstDateBean.getDate());
            return  calendar.get(Calendar.MONTH);
        }
        return -1;
    }

    public int getColorDataPosition() {
        return calendarAdapter.getColorDataPosition();
    }

    private OnCalendarClickListener onCalendarClickListener;


    public void setOnCalendarClickListener(OnCalendarClickListener onCalendarClickListener) {
        this.onCalendarClickListener = onCalendarClickListener;
    }

    public interface OnCalendarClickListener {
        public void onCalendarClick(int position, DateBean dateBean);
    }

    HashMap<String,CalendarEvent> eventDays = new HashMap<>();
    /**
     * 设置含有事件的日期
     * @param eventDays
     */
    public void setEventDays(HashMap<String,CalendarEvent> eventDays) {
        this.eventDays = eventDays;
        calendarAdapter.setDateList(eventDays);
    }

    public void initFirstDayPosition(int position) {
        if (calendarAdapter != null) {
            if (position != 0) {
                calendarAdapter.setColorDataPosition(position);
            } else {
                calendarAdapter.setColorDataPosition(calendarAdapter.getFirstDatePosition());
            }
        }
    }

    public CalendarAdapter getCurAdapter(){
        return calendarAdapter;
    }

    private void initCalendarValues() {
        setCacheColorHint(getResources().getColor(android.R.color.transparent));
        setHorizontalSpacing(0);
        setVerticalSpacing(0);
        setNumColumns(7);
        setStretchMode(STRETCH_COLUMN_WIDTH);
        setSelector(R.color.transparent);
    }

    public CalendarView(Context context) {
        super(context);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
