package com.example.unity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.unity3d.player.UnityPlayer;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by yi on 2017/10/11.
 */

public class MapGameActivity extends AppCompatActivity {

    ImageView mIvBack;
    RelativeLayout mRoot;

    private UnityPlayer mUnityPlayer;
    private String id;
    private String token;
    private String version;
    private String userId;
    private String channel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_map_event_unity);
        mIvBack = (ImageView)findViewById(R.id.iv_back);
        mRoot = (RelativeLayout)findViewById(R.id.rl_root);
        initViews(savedInstanceState);
    }

    static {
        System.loadLibrary("kira");
    }

    public native int initNDK1();

    public void EventInit(){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("targetId",id);
            jsonObject.put("X-APP-PLATFORM", channel);
            jsonObject.put("X-APP-VERSION", version);
            jsonObject.put("X-ACCESS-TOKEN", token);
            jsonObject.put("X-APP-TYPE", android.os.Build.MODEL);
            jsonObject.put("userId",userId);
            String str = jsonObject.toString();
            System.out.println("json:"+str);
            UnityPlayer.UnitySendMessage("MainSenc", "OpenGame", jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void EventError(){
        finish();
    }

    protected void initViews(Bundle savedInstanceState) {
        ViewUtils.setStatusBarLight(getWindow(), findViewById(R.id.top_view));
        id = getIntent().getStringExtra("id");
        token = getIntent().getStringExtra("token");
        version = getIntent().getStringExtra("version");
        userId = getIntent().getStringExtra("userId");
        channel = getIntent().getStringExtra("channel");
        if(TextUtils.isEmpty(id)){
            finish();
            return;
        }
        if(1 != initNDK1()){
            Log.e("NDKUtil","初始化失败");
        }else {
            Log.e("NDKUtil","初始化成功");
        }
        mUnityPlayer = new UnityPlayer(this);
        mRoot.addView(mUnityPlayer,0);
        mUnityPlayer.requestFocus();
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        this.mUnityPlayer.quit();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.mUnityPlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.mUnityPlayer.resume();
    }

    @Override
    public void onLowMemory() {

    }

    @Override
    public void onTrimMemory(int var1) {

    }

    @Override
    public void onConfigurationChanged(Configuration var1) {
        super.onConfigurationChanged(var1);
        this.mUnityPlayer.configurationChanged(var1);
    }

    @Override
    public void onWindowFocusChanged(boolean var1) {
        super.onWindowFocusChanged(var1);
        this.mUnityPlayer.windowFocusChanged(var1);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent var1) {
        return var1.getAction() == 2?this.mUnityPlayer.injectEvent(var1):super.dispatchKeyEvent(var1);
    }

    @Override
    public boolean onKeyUp(int var1, KeyEvent var2) {
        return this.mUnityPlayer.injectEvent(var2);
    }

    @Override
    public boolean onKeyDown(int var1, KeyEvent var2) {
        if(var1 == 4){
            finish();
            return true;
        }else {
            return this.mUnityPlayer.injectEvent(var2);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent var1) {
        return this.mUnityPlayer.injectEvent(var1);
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent var1) {
        return this.mUnityPlayer.injectEvent(var1);
    }
}
