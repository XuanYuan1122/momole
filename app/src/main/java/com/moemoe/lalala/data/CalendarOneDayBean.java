package com.moemoe.lalala.data;

import android.content.Context;

import com.app.annotation.Column;
import com.app.annotation.Table;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Haru on 2016/4/18 0018.
 */
@Table(name = "calendar_one_day")
public class CalendarOneDayBean {

    @Column(name = "day",isId = true,autoGen = false)
    public String day;
    @Column(name = "json")
    public String dbJson;
    public ArrayList<CalendarDayItem> items;
    private int mIndex;
    private CalendarDayItem mPreItem;

    public CalendarOneDayBean(){
        items = new ArrayList<>();
    }

    public ArrayList<CalendarDayItem> readFromJsonList(Context context,String jsonStr){
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            day = jsonObject.optString("day");
            JSONArray jsonArray = jsonObject.optJSONArray("items");
            if(jsonArray != null && jsonArray.length() > 0){
                for(int i = 0;i < jsonArray.length(); i++){
                    JSONObject json = jsonArray.getJSONObject(i);
                    CalendarDayItem bean = new CalendarDayItem();
                    bean.readFromJsonContent(context, json.toString());
                    if(bean.type.equals(CalendarDayType.BAR.value)){
                        if(mPreItem == null){
                            mPreItem = bean;
                            mIndex = i;
                        }else {
                            mIndex = i - mIndex - 1;
                            ((CalendarDayItem.CalendarDayBar)mPreItem.data).curIndex = mIndex;
                            ((CalendarDayItem.CalendarDayBar)mPreItem.data).refreshPosition = mIndex;
                            mPreItem = bean;
                            mIndex = i;
                        }
                    }
                    items.add(bean);
                }
                if(mPreItem != null){
                    if(((CalendarDayItem.CalendarDayBar)mPreItem.data).curIndex == -1){
                        ((CalendarDayItem.CalendarDayBar)mPreItem.data).curIndex = ((CalendarDayItem.CalendarDayBar)mPreItem.data).refreshPosition = items.size() - 1;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return items;
    }
}
