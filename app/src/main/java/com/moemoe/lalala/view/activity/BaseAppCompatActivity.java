package com.moemoe.lalala.view.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.utils.ToastUtils;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;

/**
 * base activity
 * Created by yi on 2016/11/27.
 */

public abstract class BaseAppCompatActivity extends AppCompatActivity {
    protected ProgressDialog mDialog;
    public static final String UUID = "uuid";
    public ProgressBar mProgressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            savedInstanceState.putParcelable("android:support:fragments", null);
        }
        super.onCreate(savedInstanceState);
        this.setContentView(this.getLayoutId());
        ButterKnife.bind(this);
        this.initToolbar(savedInstanceState);
        this.initViews(savedInstanceState);
        this.initData();
        this.initListeners();
    }

    /**
     * Fill int layout id
     * @return layout id
     */
    protected abstract int getLayoutId();

    /**
     * Initialize the view in the layout
     * @param savedInstanceState
     */
    protected abstract void initViews(Bundle savedInstanceState);

    /**
     * Initialize the toolbar in the layout
     * @param savedInstanceState
     */
    protected abstract void initToolbar(Bundle savedInstanceState);

    /**
     * Initalize the view Of the listener
     */
    protected abstract void initListeners();

    /**
     * Initialize the Activity Data
     */
    protected abstract void initData();

    public void showToast(String msg) {
        this.showToast(msg, Toast.LENGTH_SHORT);
    }


    public void showToast(String msg, int duration) {
        if (msg == null) return;
        if (duration == Toast.LENGTH_SHORT || duration == Toast.LENGTH_LONG) {
            ToastUtils.show(this, msg, duration);
        } else {
            ToastUtils.show(this, msg, ToastUtils.LENGTH_SHORT);
        }
    }


    public void showToast(int resId) {
        this.showToast(resId, Toast.LENGTH_SHORT);
    }


    public void showToast(int resId, int duration) {
        if (duration == Toast.LENGTH_SHORT || duration == Toast.LENGTH_LONG) {
            ToastUtils.show(this, resId, duration);
        } else {
            ToastUtils.show(this, resId, ToastUtils.LENGTH_SHORT);
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onTrimMemory(int level) {
        Glide.with(this).onTrimMemory(level);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(AppSetting.OpenUmeng){
            MobclickAgent.onResume(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(AppSetting.OpenUmeng){
            MobclickAgent.onPause(this);
        }
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

    public void showCustomView(View view, WebChromeClient.CustomViewCallback callback, boolean isUseNew){

    }

    public void hideCustomView(boolean isUseNew){}
}
