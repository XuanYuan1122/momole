package com.moemoe.lalala.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.text.TextUtils;
import android.view.View;

import com.moemoe.lalala.BaseActivity;
import com.moemoe.lalala.EventActivity;
import com.moemoe.lalala.R;
import com.moemoe.lalala.WebViewActivity;
import com.moemoe.lalala.app.AppSetting;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Haru on 2016/5/6 0006.
 */
public class IntentUtils {

    public static ArrayList<String> sSupportSchame  = new ArrayList<>();

    public static void init(Context context){
        sSupportSchame.add(context.getResources().getString(R.string.label_doc_action));
        sSupportSchame.add(context.getResources().getString(R.string.label_doc_action_old));
        sSupportSchame.add(context.getResources().getString(R.string.label_tag_action));
        sSupportSchame.add(context.getResources().getString(R.string.label_room_action));
        sSupportSchame.add(context.getResources().getString(R.string.label_url_action));
        sSupportSchame.add(context.getResources().getString(R.string.label_out_url_action));
        sSupportSchame.add(context.getResources().getString(R.string.label_department_action));
        sSupportSchame.add(context.getResources().getString(R.string.label_trash_action));
        sSupportSchame.add(context.getResources().getString(R.string.label_img_trash_action));
        sSupportSchame.add(context.getResources().getString(R.string.label_gacha_action));
        sSupportSchame.add(context.getResources().getString(R.string.label_cal_column_action));
        sSupportSchame.add(context.getResources().getString(R.string.label_plot_action));
        sSupportSchame.add(context.getResources().getString(R.string.label_qiu_action));
        sSupportSchame.add(context.getResources().getString(R.string.label_donation_action));
        sSupportSchame.add("event_1.0");
    }

    public static void haveShareWeb(Context context,Uri uri,View v){
        try{
            Intent i = new Intent();
            String path = uri.getPath();
            if(path.startsWith("/")){
                path = path.substring(1);
            }
            if(sSupportSchame.contains(path)){
                if(path.equals(context.getResources().getString(R.string.label_url_action))){
                    i.setPackage(uri.getHost());
                    i.setData(uri);
                    i.setAction(path);
                    i.putExtra(BaseActivity.EXTRA_KEY_UUID, uri.getQuery());
                    i.putExtra(WebViewActivity.EXTRA_KEY_SHARE,true);
                    context.startActivity(i);
                }
            }
        }catch (Exception e){

        }
    }

    public static void toActivityFromUri(Context context,Uri uri,View v){
        try{
            Intent i = new Intent();
            String path = uri.getPath();
            if(path.startsWith("/")){
                path = path.substring(1);
            }
            if(sSupportSchame.contains(path)){
                if(path.equals("event_1.0")){
                    showEvent(context);
                } else if(path.equals(context.getResources().getString(R.string.label_out_url_action))){
                    Uri uri1 = Uri.parse(uri.getQuery());
                    i.setData(uri1);
                    i.setAction("android.intent.action.VIEW");
                    context.startActivity(i);
                }else{
                    i.setPackage(uri.getHost());
                    i.setData(uri);
                    i.setAction(path);
                    String query = uri.getQuery();
                    if(path.equals(context.getResources().getString(R.string.label_url_action))){
                        if(query.contains("netaopera/chap")){
                            i.putExtra(WebViewActivity.EXTRA_KEY_SHOW_TOOLBAR,false);
                        }
                        i.putExtra(BaseActivity.EXTRA_KEY_UUID, uri.getQuery());
                    }else {
                        if(!TextUtils.isEmpty(query)){
                            if(!query.contains("&")){
                                i.putExtra(BaseActivity.EXTRA_KEY_UUID, uri.getQuery());
                            }else {
                                Set<String> names = uri.getQueryParameterNames();
                                for(String name : names){
                                    i.putExtra(name,uri.getQueryParameter(name));
                                }
                            }
                        }
                    }
                    if(path.equals(context.getResources().getString(R.string.label_plot_action))){
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeScaleUpAnimation(v,0,0,0,0);
                        ActivityCompat.startActivity((Activity)context,i,options.toBundle());
                    }else {
                        context.startActivity(i);
                    }
                }
            }else {
                WebViewActivity.startActivity(context, IConstants.notFoundPage);
            }
        }catch (Exception e){
            WebViewActivity.startActivity(context, IConstants.notFoundPage);
        }
    }

    public static Intent getIntentFromUri(Context context,Uri uri){
        Intent i = new Intent();
        try{
            String path = uri.getPath();
            if(path.startsWith("/")){
                path = path.substring(1);
            }
            if(sSupportSchame.contains(path)){
                if(path.equals(context.getResources().getString(R.string.label_out_url_action))){
                    Uri uri1 = Uri.parse(uri.getQuery());
                    i.setData(uri1);
                    i.setAction("android.intent.action.VIEW");
                }else {
                    i.setPackage(uri.getHost());
                    i.setData(uri);
                    i.setAction(path);
                    String query = uri.getQuery();
                    if(path.equals(context.getResources().getString(R.string.label_url_action))){
                        i.putExtra(BaseActivity.EXTRA_KEY_UUID, uri.getQuery());
                    }else {
                        if(!query.contains("&")){
                            i.putExtra(BaseActivity.EXTRA_KEY_UUID, uri.getQuery());
                        }else {
                            Set<String> names = uri.getQueryParameterNames();
                            for(String name : names){
                                i.putExtra(name,uri.getQueryParameter(name));
                            }
                        }
                    }
                }
            }else {
                i.setClass(context, WebViewActivity.class);
                i.putExtra(WebViewActivity.EXTRA_KEY_URL, IConstants.notFoundPage);
            }
            return i;
        }catch (Exception e){
            i.setClass(context,WebViewActivity.class);
            i.putExtra(WebViewActivity.EXTRA_KEY_URL, IConstants.notFoundPage);
        }finally {
            return i;
        }
    }

    private static void showEvent(final Context context){
        int pass = PreferenceManager.getInstance(context).getPassEvent();
        if(StringUtils.isKillEvent() && pass < 3){
            if(!AppSetting.isEnterEventToday){
                final AlertDialogUtil alertDialogUtil = AlertDialogUtil.getInstance();
                alertDialogUtil.createNormalDialog(context,context.getString(R.string.label_enter_event));
                alertDialogUtil.setOnClickListener(new AlertDialogUtil.OnClickListener() {
                    @Override
                    public void CancelOnClick() {
                        alertDialogUtil.dismissDialog();
                    }

                    @Override
                    public void ConfirmOnClick() {
                        alertDialogUtil.dismissDialog();
                        Intent i = new Intent(context,EventActivity.class);
                        context.startActivity(i);
                    }
                });
                alertDialogUtil.showDialog();
            }
        }else if((StringUtils.isKillEvent() && pass == 3) || pass > 3){
            WebViewActivity.startActivity(context,false,"http://prize.moemoe.la:8000/netaopera/chap4/?pass=" + pass  + "&user_id=" + PreferenceManager.getInstance(context).getUUid());
        }
    }
}
