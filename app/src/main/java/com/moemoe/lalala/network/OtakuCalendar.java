//package com.moemoe.lalala.network;
//
//import android.content.Context;
//
//import com.app.Utils;
//import com.app.common.Callback;
//import com.moemoe.lalala.callback.BaseCommonCallback;
//
///**
// * Created by Haru on 2016/4/27 0027.
// */
//public enum  OtakuCalendar {
//    INSTANCE;
//
//    private IOtakuCalendar service = Utils.http().create(IOtakuCalendar.class);
//
//    public void requestCalendarOneDay(String token,String id,Callback.InterceptCallback<String> callback,Context context){
//        service.requestCalendarOneDay(token,id,new BaseCommonCallback(callback,context));
//    }
//
//    public void requestRss(String token,String day,int index,int total,Callback.InterceptCallback<String> callback,Context context){
//        service.requestRss(token, day, index,total, new BaseCommonCallback(callback,context));
//    }
//
//    public void refreshUi(String token,String day,String id,int index,int len,Callback.InterceptCallback<String> callback,Context context){
//        service.refreshUi(token, day, id, index,len, new BaseCommonCallback(callback,context));
//    }
//
//    public void requestFeatured(String token,String dayRange,Callback.InterceptCallback<String> callback,Context context){
//        service.requestFeatured(token,dayRange,new BaseCommonCallback(callback,context));
//    }
//
//
//}
