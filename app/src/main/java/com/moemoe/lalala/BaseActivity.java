package com.moemoe.lalala;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks2;
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

import com.igexin.sdk.PushManager;
import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.callback.MoeMoeCallback;
import com.moemoe.lalala.data.AppUpdateInfo;
import com.moemoe.lalala.data.AuthorInfo;
import com.moemoe.lalala.data.LoginResultBean;
import com.moemoe.lalala.network.OneParameterCallback;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.thirdopen.ThirdPartySDKManager;
import com.moemoe.lalala.utils.AlertDialogUtil;
import com.moemoe.lalala.utils.PreferenceManager;
import com.moemoe.lalala.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.File;
import java.util.Date;

/**
 * Created by Haru on 2016/4/26 0026.
 */
public abstract class BaseActivity extends AppCompatActivity{
    public static final String EXTRA_KEY_UUID = "uuid";
    protected Intent mIntent;
    protected PreferenceManager mPreferMng = null;
    protected PreferenceManager.PreferenceInfo mPreferInfo = null;
    protected ProgressDialog mDialog = null;
    public ProgressBar mProgressBar;
    protected String updateApkName = null ;
    protected long mUpdateDownloadId = Integer.MIN_VALUE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        mIntent = getIntent();
        mPreferMng = PreferenceManager.getInstance(getApplicationContext());
        //mPreferInfo = PreferenceManager.initPreferenceInfo();
        mPreferInfo = mPreferMng.getPreferInfo();
        initView();
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onTrimMemory(int level) {
        if (level >= ComponentCallbacks2.TRIM_MEMORY_MODERATE) {
            // Clear the caches.  Note all pending requests will be removed too.
        }
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
       // DbManager db = Utils.getDb(MoemoeApplication.sDaoConfig);
        DbManager db = x.getDb(MoemoeApplication.sDaoConfig);
        AuthorInfo authorInfo = null;
        String userId = mPreferMng.getUserId();
        try {
            authorInfo = db.selector(AuthorInfo.class)
                    .where("userId","=",userId)
                    .findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        if(authorInfo == null) authorInfo = new AuthorInfo();
        if(ThirdPartySDKManager.getInstance().isThirdParty(authorInfo.getPlatform())){
            final AuthorInfo finalAuthorInfo = authorInfo;
            Otaku.getAccountV2().loginThird(this, authorInfo.getUserName(), authorInfo.getOpenId(), authorInfo.getPlatform(), new OneParameterCallback<LoginResultBean>() {
                @Override
                public void action(LoginResultBean loginResultBean) {
                    if(loginResultBean != null){
                        finalAuthorInfo.setToken(loginResultBean.getToken());
                        PreferenceManager.setToken(loginResultBean.getToken());
                        finalAuthorInfo.setUserId(loginResultBean.getUserId());
                        saveAuthInfo(finalAuthorInfo);
                        if (callback != null){
                            callback.onSuccess();
                        }
                        Otaku.getAccountV2().requestUserInfo(finalAuthorInfo.getUserId(),BaseActivity.this);
                    }
                }
            }, new OneParameterCallback<Integer>() {
                @Override
                public void action(Integer integer) {
                    clearAuthInfo();
                    if(callback != null){
                        callback.onFailure();
                    }
                }
            });
        }else if(authorInfo.getPhone() != null && !authorInfo.getPhone().equals("")){
            final AuthorInfo finalAuthorInfo1 = authorInfo;
            Otaku.getAccountV2().login(this, authorInfo.getPhone(), authorInfo.getPassword(), new OneParameterCallback<LoginResultBean>() {
                @Override
                public void action(LoginResultBean loginResultBean) {
                    finalAuthorInfo1.setToken(loginResultBean.getToken());
                    PreferenceManager.setToken(loginResultBean.getToken());
                    finalAuthorInfo1.setUserId(loginResultBean.getUserId());
                    finalAuthorInfo1.setDevId(PushManager.getInstance().getClientid(BaseActivity.this) + "@and");
                    saveAuthInfo(finalAuthorInfo1);
                    if (callback != null){
                        callback.onSuccess();
                    }
                    Otaku.getAccountV2().requestUserInfo(finalAuthorInfo1.getUserId(),BaseActivity.this);
                }
            }, new OneParameterCallback<Integer>() {
                @Override
                public void action(Integer integer) {
                    clearAuthInfo();
                    ToastUtil.showToast(BaseActivity.this, R.string.msg_auto_login_fail);
                    if(callback != null){
                        callback.onFailure();
                    }
                }
            });
        }else {
            clearAuthInfo();
        }
    }

    /**
     * 以防以后返回数据
     */
    private void saveAuthInfo(AuthorInfo authorInfo){
        PreferenceManager.getInstance(this).setAuthorInfo(authorInfo);
    }

    private void clearAuthInfo(){
        PreferenceManager.getInstance(this).clearAuthorInfo();
    }

    public void createDialog(String message) {
        if (this.isFinishing())
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
        if (this.isFinishing())
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
        if (this.isFinishing()) return;
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
