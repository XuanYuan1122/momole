package com.moemoe.lalala.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.MotaResult;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.view.fragment.WebViewFragment;
import com.moemoe.lalala.view.widget.netamenu.BottomMenuFragment;
import com.moemoe.lalala.view.widget.netamenu.MenuItem;
import com.pingplusplus.android.Pingpp;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by yi on 2016/12/2.
 */

public class WebViewActivity extends BaseAppCompatActivity {
    public static final String EXTRA_KEY_SHARE = "share";
    public static final String EXTRA_KEY_URL = "url";
    public static final String EXTRA_KEY_SHOW_TOOLBAR = "show_toolbar";
    public static final String EXTRA_KEY_SHOW_MORE_MENU = "isshowmoremenu";
    private static final String EXTRA_KEY_LABEL = "tagetkfkalabel";
    private static final String EXTRA_KEY_FIX_LABEL = "islabelfix";
    private final String FULL_SCREEN = "full_screen";
    private final int MENU_OPEN_OUT = 102;
    private final int MENU_OPEN_SHARE = 103;

    @BindView(R.id.rl_bar)
    View mToolbar;
    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.iv_menu_list)
    ImageView mIvMenu;
    private WebViewFragment mWebViewFragment;

    private String mUrl;
    private boolean mShouldTint = false;
    private boolean mHaveShare = false;
    private BottomMenuFragment bottomMenuFragment;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_webview;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mProgressBar = (ProgressBar) findViewById(R.id.pgbar_progress);
        mIvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        String  mTitle = getIntent().getStringExtra(EXTRA_KEY_LABEL);
        mUrl = getIntent().getStringExtra(EXTRA_KEY_URL);
        boolean mShowToolBar = getIntent().getBooleanExtra(EXTRA_KEY_SHOW_TOOLBAR,true);
        mHaveShare = getIntent().getBooleanExtra(EXTRA_KEY_SHARE,false);
        if(TextUtils.isEmpty(mUrl)){
            mUrl = getIntent().getStringExtra(UUID);
            if(TextUtils.isEmpty(mUrl)){
                finish();
            }
        }
        if (mShowToolBar && mUrl.contains(FULL_SCREEN)){
            mShowToolBar = false;
        }
        if(mShowToolBar){
            mToolbar.setVisibility(View.VISIBLE);
        }else {
            mToolbar.setVisibility(View.GONE);
        }
        if(!TextUtils.isEmpty(mTitle)){
            mTvTitle.setText(mTitle);
        }
        mIvMenu.setVisibility(View.VISIBLE);
        mIvMenu.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if(bottomMenuFragment != null)bottomMenuFragment.show(getSupportFragmentManager(),"WebMenu");
            }
        });
        initPopupMenus();
        mWebViewFragment = WebViewFragment.newInstance(mUrl);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mWebViewFragment).commit();
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void initData() {

    }

    public void showShare(final int type) {//0 抽奖 1魔塔
        ShareSDK.initSDK(this);
        final OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        String url;
        if(type == 0){
            oks.setTitle("来来来，扭蛋机每天免费给你抽宝贝！");
            url = "http://prize.moemoe.la:8000/prize/share/";
            oks.setText("异次元穿越而来的扭蛋机，每天三枚代币免费抽奖！隔三差五送出正版写真、一言不合秒充流量话费，" +
                    "抽奖累积节操积分更能换取本子、高清、熟肉等”不可描述“珍稀资源。二次元没有骗局，前方真心高能！");
            oks.setImageUrl("http://source.moemoe.la/static/image3/18.png");
        }else {
            oks.setTitle("↑←↓→ Neta学园的高智商战斗剧情");
            url = "http://ad.moemoe.la:8001/ad/ad_mota";
            oks.setText("关于拯救校长这件事，能用武力解决就不用再讲道理了，即便只用↑←↓→，能通关的同学寥寥无几。");
            oks.setImageUrl("http://s.moemoe.la/mota/h.jpg");
        }
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用

        oks.setTitleUrl(url);
        // text是分享文本，所有平台都需要这个字段

        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片

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
                    if (type == 0){
                        mWebViewFragment.shareUrl();
                    }else {
                        MotaResult pay = new MotaResult();
                        pay.setResult("success");
                        pay.setType("share");
                        Gson gson = new Gson();
                        String temp = gson.toJson(pay);
                        mWebViewFragment.resultMota(temp);
                    }
                }
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                MotaResult pay = new MotaResult();
                pay.setResult("fail");
                pay.setType("share");
                Gson gson = new Gson();
                String temp = gson.toJson(pay);
                mWebViewFragment.resultMota(temp);
            }

            @Override
            public void onCancel(Platform platform, int i) {
                MotaResult pay = new MotaResult();
                pay.setResult("cancel");
                pay.setType("share");
                Gson gson = new Gson();
                String temp = gson.toJson(pay);
                mWebViewFragment.resultMota(temp);
            }
        });
        oks.show(this);
    }

    private void initPopupMenus() {
        bottomMenuFragment = new BottomMenuFragment();
        ArrayList<MenuItem> items = new ArrayList<>();
        MenuItem item = new MenuItem(MENU_OPEN_OUT, getString(R.string.label_open_out));
        items.add(item);
        if (mHaveShare){
            MenuItem share = new MenuItem(MENU_OPEN_SHARE,getString(R.string.label_share));
            items.add(share);
        }
        bottomMenuFragment.setMenuType(BottomMenuFragment.TYPE_VERTICAL);
        bottomMenuFragment.setMenuItems(items);
        bottomMenuFragment.setShowTop(false);
        bottomMenuFragment.setmClickListener(new BottomMenuFragment.MenuItemClickListener() {
            @Override
            public void OnMenuItemClick(int itemId) {
                if (itemId == MENU_OPEN_OUT) {
                    Uri uri = Uri.parse(mUrl);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }else if(itemId == MENU_OPEN_SHARE){
                    showShare(0);
                }
            }
        });
    }

    public static void startActivity(Context context, String url) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(EXTRA_KEY_URL, url);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, boolean needToolBar,String url) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(EXTRA_KEY_URL, url);
        intent.putExtra(EXTRA_KEY_SHOW_TOOLBAR, needToolBar);
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
    public void showCustomView(View view, WebChromeClient.CustomViewCallback callback, boolean isUseNew){
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
            showToast(R.string.msg_connection);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Pingpp.REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getExtras().getString("pay_result");
                /* 处理返回值
                 * "success" - payment succeed
                 * "fail"    - payment failed
                 * "cancel"  - user canceld
                 * "invalid" - payment plugin not installed
                 */
                String errorMsg = data.getExtras().getString("error_msg"); // 错误信息
                String extraMsg = data.getExtras().getString("extra_msg"); // 错误信息
                // showMsg(result, errorMsg, extraMsg);
                MotaResult pay = new MotaResult();
                pay.setResult(result);
                pay.setType("pay");
                Gson gson = new Gson();
                String temp = gson.toJson(pay);
                mWebViewFragment.resultMota(temp);
            }
        }
    }
}
