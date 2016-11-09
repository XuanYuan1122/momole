package com.moemoe.lalala;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.app.Utils;
import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.callback.MoeMoeCallback;
import com.moemoe.lalala.data.AppUpdateInfo;
import com.moemoe.lalala.data.AuthorInfo;
import com.moemoe.lalala.network.CallbackFactory;
import com.moemoe.lalala.network.OnNetWorkCallback;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.thirdopen.ThirdPartySDKManager;
import com.moemoe.lalala.utils.AlertDialogUtil;
import com.moemoe.lalala.utils.PhoneUtil;
import com.moemoe.lalala.utils.PreferenceManager;
import com.moemoe.lalala.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Date;

/**
 * Created by Haru on 2016/4/26 0026.
 */
public abstract class BaseActivity extends AppCompatActivity{
    public static final String EXTRA_KEY_UUID = "uuid";
    protected Intent mIntent;
    protected static PreferenceManager mPreferMng = null;
    protected static PreferenceManager.PreferenceInfo mPreferInfo = null;
    protected ProgressDialog mDialog = null;
    public ProgressBar mProgressBar;
    protected String updateApkName = null ;
    protected long mUpdateDownloadId = Integer.MIN_VALUE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.view().inject(this);
        mIntent = getIntent();
        initView();
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(mReceiver, filter);
        MoemoeApplication.getInstance().addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        MoemoeApplication.getInstance().removeActivity(this);
    }

    protected BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                long downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (mUpdateDownloadId == downId) {
                    String apDir = getExternalFilesDir("").getAbsolutePath();
                    Intent installIntent = new Intent();
                    installIntent.setAction(Intent.ACTION_VIEW);
                    File file = new File(apDir, updateApkName);
                    installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    installIntent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                    startActivity(installIntent);
                }
            }
        }
    };

    protected abstract void initView();

    public void tryLoginFirst(final MoeMoeCallback callback){
        final AuthorInfo authorInfo = PreferenceManager.getInstance(this).getThirdPartyLoginMsg();
        if(ThirdPartySDKManager.getInstance(this).isThirdParty(authorInfo.getmPlatform())){
            Otaku.getAccountV2().loginThird(this,authorInfo.getmUid(), authorInfo.getmPlatform()).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
                @Override
                public void success(String token, String s) {
                    authorInfo.setmToken(token);
                    PreferenceManager.getInstance(BaseActivity.this).updateAccessTokenTimeToNow();
                    try {
                        JSONObject json = new JSONObject(s);
                        int ok = json.optInt("ok");
                        if(ok == Otaku.SERVER_OK){
                            String id = json.optString("user_id");
                            authorInfo.setmUUid(id);
                            saveAuthInfo(authorInfo);
                            if (callback != null){
                                callback.onSuccess();
                            }
                            Otaku.getAccountV2().requestSelfData(token,BaseActivity.this);
                        }else{

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void failure(String e) {
                    clearAuthInfo();
                    ToastUtil.showToast(BaseActivity.this, R.string.msg_auto_login_fail);
                    if(callback != null){
                        callback.onFailure();
                    }
                }
            }));
        }else if(authorInfo.getmPhone() != null && !authorInfo.getmPhone().equals("")){
            Otaku.getAccountV2().login(this, authorInfo.getmPhone(), authorInfo.getmPassword()).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
                @Override
                public void success(String token, String s) {
                    System.out.println("token:" + token);
                    authorInfo.setmToken(token);
                    PreferenceManager.getInstance(BaseActivity.this).updateAccessTokenTimeToNow();
                    try {
                        JSONObject json = new JSONObject(s);
                        int ok = json.optInt("ok");
                        if(ok == Otaku.SERVER_OK) {
                            String id = json.optString("user_id");
                            authorInfo.setmUUid(id);
                            authorInfo.setmDevId(PhoneUtil.getLocaldeviceId(getApplicationContext()));
                            saveAuthInfo(authorInfo);
                            if (callback != null){
                                callback.onSuccess();
                            }
                            Otaku.getAccountV2().requestSelfData(token,BaseActivity.this);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void failure(String e) {
                    clearAuthInfo();
                    ToastUtil.showToast(BaseActivity.this, R.string.msg_auto_login_fail);
                    if(callback != null){
                        callback.onFailure();
                    }
                }
            }));
        }else {
            PreferenceManager.getInstance(this).clearThirdPartyLoginMsg();
        }
    }

    /**
     * 以防以后返回数据
     * @param authorInfo
     */
    private void saveAuthInfo(AuthorInfo authorInfo){
        PreferenceManager.getInstance(this).saveThirdPartyLoginMsg(authorInfo);
    }

    private void clearAuthInfo(){
        PreferenceManager.getInstance(this).clearThirdPartyLoginMsg();
    }

    public void createDialog(String message) {
        if (this == null || this.isFinishing())
            return;
        try {
            if (mDialog != null)
                mDialog.dismiss();
            mDialog = ProgressDialog.show(this, "", message);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void createDialog() {
        if (this == null || this.isFinishing())
            return;
        try {
            if (mDialog != null && mDialog.isShowing()) return;
            mDialog = ProgressDialog.show(this, "", getString(R.string.msg_on_loading));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void finalizeDialog() {
        if (mDialog == null) return;
        try {
            if(mDialog.isShowing()) mDialog.dismiss();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        mDialog = null;
    }

    /**
     * 网页加载完成执行
     * @param url
     */
    public void onPageFinished(String url){

    }

    /**
     * 关闭网页进度条
     */
    public void cancelProgressBar(){

    }

    /**
     * 网页加载开始
     * @param url
     */
    public void onPageStarted(String url){

    }

    public void showCustomView(View view,WebChromeClient.CustomViewCallback callback,boolean isUseNew){

    }

    public void hideCustomView(boolean isUseNew){}

    @Override
    protected void onPause() {
        super.onPause();
        MoemoeApplication.getInstance().setIsAppRunningFront(false);
        if(AppSetting.OpenUmeng){
            MobclickAgent.onPause(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MoemoeApplication.getInstance().setIsAppRunningFront(true);
        if(AppSetting.OpenUmeng){
            MobclickAgent.onResume(this);
        }
    }


    protected void showUpdateDialog(final AppUpdateInfo info){
        if (this == null || this.isFinishing()) return;
        final AlertDialogUtil alertDialogUtil = AlertDialogUtil.getInstance();
        alertDialogUtil.createPromptDialog(this, info.getTitle(), info.getContent());
        alertDialogUtil.setButtonText(getString(R.string.label_update), getString(R.string.label_later), info.getUpdateStatus());
        alertDialogUtil.setOnClickListener(new AlertDialogUtil.OnClickListener() {
            @Override
            public void CancelOnClick() {
                alertDialogUtil.dismissDialog();
            }

            @Override
            public void ConfirmOnClick() {
                alertDialogUtil.dismissDialog();
                try {
                    DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    Uri uri = Uri.parse(info.getUrl());
                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    updateApkName = "neta-update" + new Date().getTime() + ".apk";
                    request.setDestinationInExternalFilesDir(BaseActivity.this, null, updateApkName);
                    mUpdateDownloadId = downloadManager.enqueue(request);
                } catch (Throwable t) {
                    t.printStackTrace();
                    Toast.makeText(BaseActivity.this, R.string.label_error_storage, Toast.LENGTH_SHORT).show();
                }
            }
        });
        alertDialogUtil.showDialog();
    }
}
