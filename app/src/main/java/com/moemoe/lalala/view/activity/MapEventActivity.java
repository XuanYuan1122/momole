//package com.moemoe.lalala.view.activity;
//
//import android.content.res.Configuration;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.MotionEvent;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//
//import com.moemoe.lalala.R;
//import com.moemoe.lalala.app.AppSetting;
//import com.moemoe.lalala.app.MoeMoeApplication;
//import com.moemoe.lalala.app.RxBus;
//import com.moemoe.lalala.di.components.DaggerJuQIngChatComponent;
//import com.moemoe.lalala.di.modules.JuQingChatModule;
//import com.moemoe.lalala.event.EventDoneEvent;
//import com.moemoe.lalala.presenter.JuQIngChatContract;
//import com.moemoe.lalala.presenter.JuQingChatPresenter;
//import com.moemoe.lalala.utils.JuQingUtil;
//import com.moemoe.lalala.utils.MyUnityPlayer;
//import com.moemoe.lalala.utils.NoDoubleClickListener;
//import com.moemoe.lalala.utils.PreferenceUtils;
//import com.moemoe.lalala.utils.ViewUtils;
//import com.unity3d.player.UnityPlayer;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import javax.inject.Inject;
//
//import butterknife.BindView;
//
///**
// * Created by yi on 2017/10/11.
// */
//
//public class MapEventActivity extends BaseAppCompatActivity  implements JuQIngChatContract.View{
//
//    @BindView(R.id.iv_back)
//    ImageView mIvBack;
//    @BindView(R.id.rl_root)
//    RelativeLayout mRoot;
//
//    @Inject
//    JuQingChatPresenter mPresenter;
//
//    private MyUnityPlayer mUnityPlayer;
//    private String mId;
//
//    static {
//        System.loadLibrary("kira");
//    }
//
//    public native int initNDK1();
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.ac_map_event_unity;
//    }
//
//    @Override
//    protected void initViews(Bundle savedInstanceState) {
//        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
//        DaggerJuQIngChatComponent.builder()
//                .juQingChatModule(new JuQingChatModule(this))
//                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
//                .build()
//                .inject(this);
//        if(1 != initNDK1()){
//            Log.e("NDKUtil","初始化失败");
//        }else {
//            Log.e("NDKUtil","初始化成功");
//        }
//        mId = getIntent().getStringExtra("id");
//        if(TextUtils.isEmpty(mId)){
//            finish();
//            return;
//        }
//        mUnityPlayer = new MyUnityPlayer(this);
//        mRoot.addView(mUnityPlayer,0);
//        mUnityPlayer.requestFocus();
//    }
//
//    @Override
//    protected void initToolbar(Bundle savedInstanceState) {
//
//    }
//
//    @Override
//    protected void initListeners() {
//        mIvBack.setOnClickListener(new NoDoubleClickListener() {
//            @Override
//            public void onNoDoubleClick(View v) {
//                finish();
//            }
//        });
//    }
//
//    @Override
//    protected void initData() {
//
//    }
//
//    public void EventInit(){
//        try {
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("targetId",mId);
//            jsonObject.put("X-X-APP-PLATFORM-TOKEN", AppSetting.CHANNEL);
//            jsonObject.put("X-APP-VERSION", AppSetting.VERSION_CODE+"");
//            jsonObject.put("X-ACCESS-TOKEN", PreferenceUtils.getToken());
//            jsonObject.put("X-APP-TYPE", android.os.Build.MODEL);
//            jsonObject.put("userId",PreferenceUtils.getUUid());
//            String str = jsonObject.toString();
//            System.out.println("json:"+str);
//            UnityPlayer.UnitySendMessage("MainSenc", "OpenGal", jsonObject.toString());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void EventDone(){
//        mPresenter.doneJuQing(mId);
//    }
//
//    public void EventError(){
//        showToast("剧情加载出错");
//        finish();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        this.mUnityPlayer.quit();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        this.mUnityPlayer.pause();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        this.mUnityPlayer.resume();
//    }
//
//    @Override
//    public void onLowMemory() {
//
//    }
//
//    @Override
//    public void onTrimMemory(int var1) {
//
//    }
//
//    @Override
//    public void onConfigurationChanged(Configuration var1) {
//        super.onConfigurationChanged(var1);
//        this.mUnityPlayer.configurationChanged(var1);
//    }
//
//    @Override
//    public void onWindowFocusChanged(boolean var1) {
//        super.onWindowFocusChanged(var1);
//        this.mUnityPlayer.windowFocusChanged(var1);
//    }
//
//    @Override
//    public boolean dispatchKeyEvent(KeyEvent var1) {
//        return var1.getAction() == 2?this.mUnityPlayer.injectEvent(var1):super.dispatchKeyEvent(var1);
//    }
//
//    @Override
//    public boolean onKeyUp(int var1, KeyEvent var2) {
//        return this.mUnityPlayer.injectEvent(var2);
//    }
//
//    @Override
//    public boolean onKeyDown(int var1, KeyEvent var2) {
//        if(var1 == 4){
//            finish();
//            return true;
//        }else {
//            return this.mUnityPlayer.injectEvent(var2);
//        }
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent var1) {
//        return this.mUnityPlayer.injectEvent(var1);
//    }
//
//    @Override
//    public boolean onGenericMotionEvent(MotionEvent var1) {
//        return this.mUnityPlayer.injectEvent(var1);
//    }
//
//    @Override
//    public void onFailure(int code, String msg) {
//
//    }
//
//    @Override
//    public void onDoneSuccess(long time) {
//        if(3 != JuQingUtil.getLevel(mId)){
//            JuQingUtil.saveJuQingDone(mId,time);
//        }
//        RxBus.getInstance().post(new EventDoneEvent("map",""));
//    }
//}
