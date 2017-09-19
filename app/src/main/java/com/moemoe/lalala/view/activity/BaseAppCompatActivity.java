package com.moemoe.lalala.view.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.app.AppStatusConstant;
import com.moemoe.lalala.app.AppStatusManager;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.ToastUtils;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * base activity
 * Created by yi on 2016/11/27.
 */

public abstract class BaseAppCompatActivity extends AppCompatActivity {
    protected ProgressDialog mDialog;
    public static final String UUID = "uuid";
    public ProgressBar mProgressBar;
    private Unbinder bind;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            savedInstanceState.putParcelable("android:support:fragments", null);
        }
        super.onCreate(savedInstanceState);
        //ImmersionBar.with(this).init();
        this.getWindow().setBackgroundDrawable(null);//移除默认背景，避免overdraw
        switch (AppStatusManager.getInstance().getAppStatus()) {
            case AppStatusConstant.STATUS_FORCE_KILLED:
                restartApp();
                break;
            case AppStatusConstant.STATUS_NORMAL:
              //  this.setContentView(LayoutInflater.from(this).inflate(this.getLayoutId(),null,false),new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.getScreenHeight(this)));
                this.setContentView(this.getLayoutId());
                bind = ButterKnife.bind(this);
                this.initViews(savedInstanceState);
                this.initToolbar(savedInstanceState);
                this.initData();
                this.initListeners();
                break;
        }
    }

    protected void restartApp() {
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra(AppStatusConstant.KEY_HOME_ACTION,AppStatusConstant.ACTION_RESTART_APP);
        startActivity(intent);
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
        ToastUtils.showShortToast(this,msg);
    }

    public void showToast(int resId) {
        ToastUtils.showShortToast(this,resId);
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
        if(bind != null) bind.unbind();
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

    @SuppressWarnings("unchecked")
    protected  <V extends View> V $(@IdRes int id){
        return (V)findViewById(id);
    }
}
