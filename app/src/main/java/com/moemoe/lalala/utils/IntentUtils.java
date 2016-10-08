package com.moemoe.lalala.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.text.TextUtils;
import android.view.View;

import com.app.annotation.ContentView;
import com.app.common.Callback;
import com.app.http.request.UriRequest;
import com.moemoe.lalala.BaseActivity;
import com.moemoe.lalala.EventActivity;
import com.moemoe.lalala.MapActivity;
import com.moemoe.lalala.R;
import com.moemoe.lalala.WebViewActivity;
import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.data.DustBean;
import com.moemoe.lalala.network.CallbackFactory;
import com.moemoe.lalala.network.OnNetWorkCallback;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.view.dialog.NetaAlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
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
        sSupportSchame.add(context.getResources().getString(R.string.label_gacha_action));
        sSupportSchame.add(context.getResources().getString(R.string.label_cal_column_action));
        sSupportSchame.add(context.getResources().getString(R.string.label_plot_action));
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

    private static void showDialog(final Context context){
        if(PreferenceManager.getInstance(context).isLogin(context)){
            final String token1 = PreferenceManager.getInstance(context).getToken();
            Otaku.getCommonV2().dustState(token1).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
                @Override
                public void success(String token, String s) {
                    String state = s;
                    if (state.equals(IConstants.CLOSE)) {
                        ToastUtil.showCenterToast(context, R.string.msg_trash_close);
                    } else {
                        ((MapActivity)context).getMap().setIsDialogCause(true);
                        final NetaAlertDialog dialog = new NetaAlertDialog(context);
                        dialog.setAnimationEnable(true)
                                .setPositiveListener(new NetaAlertDialog.OnPositiveListener() {
                                    @Override
                                    public void onClick(NetaAlertDialog dialog, int state) {
                                        if (state == NetaAlertDialog.STATE_GET_FIRST_DUST) {
                                            getDust(token1,dialog,context);
                                        }else if(state == NetaAlertDialog.STATE_COPY_DUST){

                                        }else if(state == NetaAlertDialog.STATE_SEND_DUST) {
                                        }
                                    }
                                })
                                .setNegativeListener(new NetaAlertDialog.OnNegativeListener() {
                                    @Override
                                    public void onClick(NetaAlertDialog dialog, int state) {
                                        if(state == NetaAlertDialog.STATE_GET_NEXT_DUST){
                                            getDust(token1,dialog,context);
                                        }else if(state == NetaAlertDialog.STATE_SEND_SOME_DUST){

                                        }else if(state == NetaAlertDialog.STATE_CANCEL_SEND_DUST){
                                            dialog.dismiss();
                                        }
                                    }
                                })
                                .show();
                    }
                }

                @Override
                public void failure(String e) {

                }
            }));
        }else {

        }
    }

    private static void getDust(String token,final NetaAlertDialog dialog,final Context context){
        Otaku.getCommonV2().getDust(token).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
            @Override
            public void success(String token, String s) {
                final DustBean dustBean = DustBean.readFromJsonContent(s);
                if (dustBean.serverStatus.equals(IConstants.CLOSE)) {
                    ToastUtil.showCenterToast(context, R.string.msg_trash_close);
                    dialog.stopAnim();
                }else {
                    if (dustBean.item.overTimes) {
                        ToastUtil.showCenterToast(context, R.string.msg_trash_used_up);
                        dialog.stopAnim();
                    }else {
                        dialog.showDust();
                        dialog.setDialogTitle(dustBean.item.title)
                                .setContentText(dustBean.item.content)
                                .setContentLongClickListener(new NetaAlertDialog.OnLongClickListener() {
                                    @Override
                                    public void onLongClick() {
                                        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                                        ClipData mClipData = ClipData.newPlainText("绅士内容", dustBean.item.content);
                                        cmb.setPrimaryClip(mClipData);
                                        ToastUtil.showCenterToast(context, R.string.msg_trash_get);
                                    }
                                });
                    }
                }
            }

            @Override
            public void failure(String e) {
                ToastUtil.showCenterToast(context, R.string.msg_trash_used_up);
                dialog.stopAnim();
            }
        }));
    }

    private static void showEvent(final Context context){
        if(StringUtils.isKillEvent()){
//            Calendar today = Calendar.getInstance();
//            Calendar last = Calendar.getInstance();
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
        }
    }
}
