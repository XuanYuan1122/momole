package com.moemoe.lalala;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.annotation.ContentView;
import com.app.annotation.FindView;
import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.fragment.WebViewFragment;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.ToastUtil;
import com.moemoe.lalala.utils.onekeyshare.OnekeyShare;
import com.moemoe.lalala.view.NoDoubleClickListener;
import com.moemoe.lalala.view.menu.MenuItem;
import com.moemoe.lalala.view.menu.PopupListMenu;
import com.moemoe.lalala.view.menu.PopupMenuItems;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;

/**
 * Created by Haru on 2016/5/1 0001.
 */
@ContentView(R.layout.ac_webview)
public class WebViewActivity extends BaseActivity {

    public static final String EXTRA_KEY_URL = "targeturl";
    public static final String EXTRA_KEY_SHOW_MORE_MENU = "isshowmoremenu";
    public static final String EXTRA_KEY_LABEL = "tagetkfkalabel";
    public static final String EXTRA_KEY_FIX_LABEL = "islabelfix";
    public static final String EXTRA_KEY_SHARE = "share";
    private static final int MENU_OPEN_OUT = 102;
    private static final int MENU_OPEN_SHARE = 103;
    @FindView(R.id.toolbar)
    private Toolbar mToolbar;
    @FindView(R.id.tv_toolbar_title)
    private TextView mTvTitle;
    @FindView(R.id.iv_menu_list)
    private ImageView mIvMenu;
    private WebViewFragment mWebViewFragment;

    private String mTitle;
    private String mUrl;
    private boolean mNeedMenu;
    private boolean mShouldTint = false;
    private boolean mHaveShare = false;
    private PopupListMenu mMenu;

    @Override
    protected void initView() {
        mProgressBar = (ProgressBar) findViewById(R.id.pgbar_progress);
        mToolbar.setNavigationOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mTitle = mIntent.getStringExtra(EXTRA_KEY_LABEL);
        mUrl = mIntent.getStringExtra(EXTRA_KEY_URL);
        mNeedMenu = mIntent.getBooleanExtra(EXTRA_KEY_SHOW_MORE_MENU,false);
        mHaveShare = mIntent.getBooleanExtra(EXTRA_KEY_SHARE,false);
        if(TextUtils.isEmpty(mUrl)){
            mUrl = mIntent.getStringExtra(BaseActivity.EXTRA_KEY_UUID);
            if(TextUtils.isEmpty(mUrl)){
                finish();
            }
        }
        if(!TextUtils.isEmpty(mTitle)){
            mTvTitle.setText(mTitle);
        }
        mIvMenu.setVisibility(View.VISIBLE);
        mIvMenu.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                mMenu.showMenu(mIvMenu);
            }
        });
        initPopupMenus();
        mWebViewFragment = WebViewFragment.newInstance(mUrl);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mWebViewFragment).commit();
    }

    private void showShare() {
        final OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("来来来，扭蛋机每天免费给你抽宝贝！");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        String url = "http://prize.moemoe.la:8000/prize/share/";
        oks.setTitleUrl(url);
        // text是分享文本，所有平台都需要这个字段
        oks.setText("异次元穿越而来的扭蛋机，每天三枚代币免费抽奖！隔三差五送出正版写真、一言不合秒充流量话费，" +
                "抽奖累积节操积分更能换取本子、高清、熟肉等”不可描述“珍稀资源。二次元没有骗局，前方真心高能！");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        oks.setImageUrl("http://source.moemoe.la/static/image3/18.png");
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(url);
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(url);
        // 启动分享GUI
        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                if(mWebViewFragment != null){
                    mWebViewFragment.shareUrl();
                }
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {

            }

            @Override
            public void onCancel(Platform platform, int i) {

            }
        });
        oks.show(this);
    }

    private void initPopupMenus() {
        PopupMenuItems items = new PopupMenuItems(this);
        MenuItem item = new MenuItem(MENU_OPEN_OUT, getString(R.string.label_open_out));
        items.addMenuItem(item);
        if (mHaveShare){
            MenuItem share = new MenuItem(MENU_OPEN_SHARE,getString(R.string.label_share));
            items.addMenuItem(share);
        }
        mMenu = new PopupListMenu(this, items);
        mMenu.setMenuItemClickListener(new PopupListMenu.MenuItemClickListener() {

            @Override
            public void OnMenuItemClick(int itemId) {
                if (itemId == MENU_OPEN_OUT) {
                    Uri uri = Uri.parse(mUrl);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }else if(itemId == MENU_OPEN_SHARE){
                    showShare();
                }
            }
        });
    }

    public static void startActivity(Context context, String url) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(EXTRA_KEY_URL, url);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, String url, boolean needMoreMenu) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(EXTRA_KEY_URL, url);
        intent.putExtra(EXTRA_KEY_SHOW_MORE_MENU, needMoreMenu);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, String url, String label, boolean needMoreMenu, boolean titleEditable) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(EXTRA_KEY_URL, url);
        intent.putExtra(EXTRA_KEY_LABEL, label);
        intent.putExtra(EXTRA_KEY_SHOW_MORE_MENU, needMoreMenu);
        intent.putExtra(EXTRA_KEY_FIX_LABEL, titleEditable);
        context.startActivity(intent);
    }

    @Override
    public void onPageFinished(String url){

    }

    @Override
    public void cancelProgressBar(){
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onPageStarted(String url){
        showProgressBar();
    }

    @Override
    public void showCustomView(View view,WebChromeClient.CustomViewCallback callback,boolean isUseNew){
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && mShouldTint) {
            //tintManager.setStatusBarTintEnabled(false);
            // 设置状态栏的颜色
            //tintManager.setStatusBarTintResource(R.color.black);
            getWindow().getDecorView().setFitsSystemWindows(false);
        }
        mToolbar.setVisibility(View.GONE);
    }

    @Override
    public void hideCustomView(boolean isUseNew){
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && mShouldTint) {
            //tintManager.setStatusBarTintEnabled(true);
            // 设置状态栏的颜色
            //tintManager.setStatusBarTintResource(R.color.main_title_cyan);
            getWindow().getDecorView().setFitsSystemWindows(true);
        }
        mToolbar.setVisibility(View.VISIBLE);
    }

    private void showProgressBar(){
        if(NetworkUtils.isNetworkAvailable(this)){
            mProgressBar.setVisibility(View.VISIBLE);
        }else {
            mProgressBar.setVisibility(View.GONE);
            ToastUtil.showToast(this, R.string.msg_server_connection);
        }
    }

    @Override
    public void onBackPressed() {
        if(mWebViewFragment == null || mWebViewFragment.mChromeClient == null) super.onBackPressed();
        if(!mWebViewFragment.mChromeClient.onBackPressed()){
            //if(mWebViewFragment.mWebView.canGoBack()){
           //     mWebViewFragment.mWebView.goBack();
          //  }else {
                super.onBackPressed();
           // }
        }
    }
}
