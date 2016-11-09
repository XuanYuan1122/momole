package com.moemoe.lalala;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.app.annotation.ContentView;
import com.app.annotation.FindView;
import com.app.common.Callback;
import com.app.http.request.UriRequest;
import com.moemoe.lalala.app.AlertDialog;
import com.moemoe.lalala.network.CallbackFactory;
import com.moemoe.lalala.network.OnNetWorkCallback;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.view.NoDoubleClickListener;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Haru on 2016/5/1 0001.
 */
@ContentView(R.layout.ac_about)
public class AboutActivity extends BaseActivity{

    private static int CLICK_INTERVAL_MAX = 1000;
    private static int CLICK_NUMBER = 4;

    @FindView(R.id.toolbar)
    private Toolbar mToolbar;
    @FindView(R.id.tv_toolbar_title)
    private TextView mTvTitle;
    /**
     * 前一次点击的时间
     */
    private long mLastClickTime;
    /**
     * 点击view 的次数
     */
    private int mCurrClickNum;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            // 透明状态栏
//            getWindow().addFlags(
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            // 透明导航栏
//            getWindow().addFlags(
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            SystemBarTintManager tintManager = new SystemBarTintManager(this);
//            tintManager.setStatusBarTintEnabled(true);
//            // 设置状态栏的颜色
//            tintManager.setStatusBarTintResource(R.color.main_title_cyan);
//            getWindow().getDecorView().setFitsSystemWindows(true);
//        }
//    }

    @Override
    protected void initView() {
        mToolbar.setNavigationOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mTvTitle.setText(R.string.label_about_neta);
        findViewById(R.id.iv_neta_secret).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNetaLogoClick();
            }
        });
    }

//    @Override
//    public void onClick(View v) {
//        int id = v.getId();
//        if(id == R.id.iv_neta_secret){
//            onNetaLogoClick();
//        }
//    }

    /**
     * logo点击，5次触发神秘事件
     */
    private void onNetaLogoClick(){
        if(System.currentTimeMillis() - mLastClickTime <= CLICK_INTERVAL_MAX){
            mCurrClickNum ++;
            if(mCurrClickNum == CLICK_NUMBER){
                onSecretEvent();
                // 点击次数到达了，响应
                mCurrClickNum = 0;
            }
        }else{
            mCurrClickNum = 0;
        }
        mLastClickTime = System.currentTimeMillis();
    }

    /**
     * 神秘事件发生
     */
    private void onSecretEvent(){
        if(mPreferMng.isLogin(this)){
            // 用户已经登录
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.a_dlg_title_wu_code);
            final EditText edt = (EditText) LayoutInflater.from(this).inflate(R.layout.c_only_edittext, null);
            builder.setView(edt);
            builder.setPositiveButton(R.string.a_dlg_ok, new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String code = edt.getText().toString();
                    if(!TextUtils.isEmpty(code)){
                        Otaku.getDocV2().request5Club(mPreferMng.getToken(), code).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
                            @Override
                            public void success(String token, String s) {
                                String clubId = s;
                                Intent intent = new Intent(AboutActivity.this, ClubPostListActivity.class);
                                intent.putExtra(ClubPostListActivity.EXTRA_KEY_UUID, clubId);
                                startActivity(intent);
                            }

                            @Override
                            public void failure(String e) {

                            }
                        }));
//                        Otaku.getDoc().request5Club(mPreferMng.getToken(), code, new Callback.InterceptCallback<String>() {
//                            @Override
//                            public void beforeRequest(UriRequest request) throws Throwable {
//
//                            }
//
//                            @Override
//                            public void afterRequest(UriRequest request) throws Throwable {
//
//                            }
//
//                            @Override
//                            public void onSuccess(String result) {
//                                try {
//                                    JSONObject json = new JSONObject(result);
//                                    if(json.optInt("ok") == Otaku.SERVER_OK){
//                                        String clubId = json.optString("data");
//                                        Intent intent = new Intent(AboutActivity.this, ClubPostListActivity.class);
//                                        intent.putExtra(ClubPostListActivity.EXTRA_KEY_UUID, clubId);
//                                        startActivity(intent);
//                                    }
//
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//
//                            }
//
//                            @Override
//                            public void onError(Throwable ex, boolean isOnCallback) {
//
//                            }
//
//                            @Override
//                            public void onCancelled(CancelledException cex) {
//
//                            }
//
//                            @Override
//                            public void onFinished() {
//
//                            }
//                        },AboutActivity.this);
                    }
                }

            });
            builder.setNegativeButton(R.string.a_dlg_cancel, null);

            Dialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            try {
                dialog.show();
            } catch (Exception e) {
            }
        }
    }

}
