package com.moemoe.lalala.view.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.app.AppStatusConstant;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.app.RxBus;
import com.moemoe.lalala.di.modules.MapModule;
import com.moemoe.lalala.dialog.SignDialog;
import com.moemoe.lalala.event.BackSchoolEvent;
import com.moemoe.lalala.event.SystemMessageEvent;
import com.moemoe.lalala.model.entity.AppUpdateEntity;
import com.moemoe.lalala.model.entity.AuthorInfo;
import com.moemoe.lalala.model.entity.BuildEntity;
import com.moemoe.lalala.model.entity.DailyTaskEntity;
import com.moemoe.lalala.model.entity.JuQIngStoryEntity;
import com.moemoe.lalala.model.entity.JuQingTriggerEntity;
import com.moemoe.lalala.model.entity.NetaEvent;
import com.moemoe.lalala.model.entity.PersonalMainEntity;
import com.moemoe.lalala.model.entity.SignEntity;
import com.moemoe.lalala.presenter.MapContract;
import com.moemoe.lalala.presenter.MapPresenter;
import com.moemoe.lalala.service.DaemonService;
import com.moemoe.lalala.utils.AlertDialogUtil;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.JuQingUtil;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.di.components.DaggerMapComponent;
import com.moemoe.lalala.view.widget.netamenu.BottomMenuFragment;
import com.moemoe.lalala.view.widget.netamenu.MenuItem;
import com.unity3d.player.UnityPlayer;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.rong.imkit.RongIM;
import io.rong.imkit.manager.IUnReadMessageObserver;
import io.rong.imlib.model.Conversation;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.moemoe.lalala.utils.StartActivityConstant.REQUEST_CODE_CREATE_DOC;

/**
 * 地图主界面
 * Created by yi on 2016/11/27.
 */

public class MapActivity extends BaseAppCompatActivity implements MapContract.View,IUnReadMessageObserver {

    @BindView(R.id.main_root)
    RelativeLayout mMainRoot;
    @BindView(R.id.fl_map_root)
    FrameLayout mMap;
    @BindView(R.id.iv_bag)
    ImageView mIvBag;
    @BindView(R.id.iv_card)
    ImageView mIvCard;
    @BindView(R.id.fl_card_root)
    View mCardRoot;
    @BindView(R.id.iv_card_dot)
    View mCardDot;
    @BindView(R.id.iv_main)
    ImageView mIvMain;
    @BindView(R.id.rl_main_list_root)
    View mPhoneRoot;
    @BindView(R.id.iv_sign)
    ImageView mIvSign;
    @BindView(R.id.tv_msg)
    TextView mTvMsg;
    @BindView(R.id.iv_role)
    ImageView mIvRole;
    @BindView(R.id.iv_create_dynamic)
    ImageView mIvCreatDynamic;
    @BindView(R.id.iv_create_wenzhang)
    ImageView mIvCreateWen;
    @BindView(R.id.tv_show_text)
    TextView mTvText;
    @BindView(R.id.rl_role_root)
    RelativeLayout mRoleRoot;
    @BindView(R.id.tv_sys_time)
    TextView mTvTime;

    @Inject
    MapPresenter mPresenter;

    protected UnityPlayer mUnityPlayer;

    private TextView mEventTv;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private boolean isGisterReciver;
    private long mLastBackTime = 0;
    private String mSchema;
    public static String updateApkName;
    private boolean mIsOut = false;
    public static long mUpdateDownloadId = Integer.MIN_VALUE;
    private boolean mIsSignPress = false;
    private boolean isLoadUnity;
    private BottomMenuFragment menuFragment;

    static {
        System.loadLibrary("kira");
    }

    public native int initNDK();

    public void StartLoad(){
        isLoadUnity = true;
        imgIn();
        mIsOut = false;
        mPresenter.checkStoryVersion();
        Intent i3 = new Intent(MapActivity.this,WallBlockActivity.class);
        startActivity(i3);
        if(!TextUtils.isEmpty(mSchema)){
            IntentUtils.toActivityFromUri(this, Uri.parse(mSchema),null);
        }
    }

    public void changeButtonState(){
        if(mIsOut){
            imgIn();
            mIsOut = false;
        }else {
            imgOut();
            mIsOut = true;
        }
    }

    public void toNativeView(String schema,String name){
        try{
            if(!TextUtils.isEmpty(schema)) {
                String temp = schema;
                if (name.equals("恋爱讲座")) {
                    if (menuFragment == null) {
                        ArrayList<MenuItem> items = new ArrayList<>();
                        MenuItem item = new MenuItem(0, "赤印");
                        items.add(item);
                        item = new MenuItem(1, "雪之本境");
                        items.add(item);
                        item = new MenuItem(2, "且听琴语");
                        items.add(item);
                        menuFragment = new BottomMenuFragment();
                        menuFragment.setShowTop(true);
                        menuFragment.setTopContent("选择听哪个故事呢？");
                        menuFragment.setMenuItems(items);
                        menuFragment.setMenuType(BottomMenuFragment.TYPE_VERTICAL);
                        menuFragment.setmClickListener(new BottomMenuFragment.MenuItemClickListener() {
                            @Override
                            public void OnMenuItemClick(int itemId) {
                                String url = "";
                                if (itemId == 0) {
                                    url = "https://www.iqing.in/play/653";
                                } else if (itemId == 1) {
                                    url = "https://www.iqing.in/play/654";
                                } else if (itemId == 2) {
                                    url = "https://www.iqing.in/play/655";
                                }
                                WebViewActivity.startActivity(MapActivity.this, url, true);
                            }
                        });
                    }
                    menuFragment.show(getSupportFragmentManager(), "mapMenu");
                } else if (name.equals("扭蛋机抽奖")) {
                    if (DialogUtils.checkLoginAndShowDlg(MapActivity.this)) {
                        AuthorInfo authorInfo = PreferenceUtils.getAuthorInfo();
                        try {
                            temp += "?user_id=" + authorInfo.getUserId()
                                    + "&nickname=" + (TextUtils.isEmpty(authorInfo.getUserName()) ? "" : URLEncoder.encode(authorInfo.getUserName(), "UTF-8"))
                                    + "&token=" + PreferenceUtils.getToken();
                            Uri uri = Uri.parse(temp);
                            IntentUtils.haveShareWeb(MapActivity.this, uri, null);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (name.equals("返校-人物")) {
                    int level = PreferenceUtils.getBackSchoolLevel(MapActivity.this);
                    if (level == 5) {
                        if (!AppSetting.isShowBackSchoolAll) showEventDialog("是否回顾剧情", 2);
                    } else {
                        temp += "?token=" + PreferenceUtils.getToken()
                                + "&full_screen";
                        Uri uri = Uri.parse(temp);
                        IntentUtils.toActivityFromUri(MapActivity.this, uri, null);
                    }
                } else {
                    if (temp.contains("http://prize.moemoe.la:8000/mt")) {
                        AuthorInfo authorInfo = PreferenceUtils.getAuthorInfo();
                        temp += "?user_id=" + authorInfo.getUserId() + "&nickname=" + authorInfo.getUserName();
                    }
                    if (temp.contains("http://prize.moemoe.la:8000/netaopera/chap")) {
                        AuthorInfo authorInfo = PreferenceUtils.getAuthorInfo();
                        temp += "?pass=" + PreferenceUtils.getPassEvent(MapActivity.this) + "&user_id=" + authorInfo.getUserId();
                    }
                    if (temp.contains("http://neta.facehub.me/")) {
                        AuthorInfo authorInfo = PreferenceUtils.getAuthorInfo();
                        temp += "?open_id=" + authorInfo.getUserId() + "&nickname=" + authorInfo.getUserName() + "&pay_way=alipay,wx,qq" + "&full_screen";
                    }
                    if (temp.contains("fanxiao/final.html")) {
                        temp += "?token=" + PreferenceUtils.getToken()
                                + "&full_screen";
                    }
                    if (temp.contains("fanxiao/paihang.html")) {
                        temp += "?token=" + PreferenceUtils.getToken();
                    }
                    Uri uri = Uri.parse(temp);
                    IntentUtils.toActivityFromUri(MapActivity.this, uri, null);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ac_map;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ViewUtils.setStatusBarLight(getWindow(),null);
        AppSetting.isRunning = true;
        Intent intent = getIntent();
        if(intent != null){
            mSchema = intent.getStringExtra("schema");
        }
        DaggerMapComponent.builder()
                .mapModule(new MapModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        if(1 != initNDK()){
            Log.e("NDKUtil","初始化失败");
        }else {
            Log.e("NDKUtil","初始化成功");
        }
        final Conversation.ConversationType[] conversationTypes = {
                Conversation.ConversationType.PRIVATE
        };
        RongIM.getInstance().addUnReadMessageCountChangedObserver(this, conversationTypes);
        mUnityPlayer = new UnityPlayer(this);
        mMap.addView(mUnityPlayer);
        mUnityPlayer.requestFocus();
        //imgOut();
        //mIsOut = true;
        startService(new Intent(this, DaemonService.class));
        if(!isGisterReciver){
            IntentFilter filter = new IntentFilter();
            filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            registerReceiver(mReceiver, filter);
            isGisterReciver = true;
        }
        if(NetworkUtils.isNetworkAvailable(this) && NetworkUtils.isWifi(this)){
            mPresenter.checkVersion();
        }
        mPresenter.getEventList();
        String mTime = dateFormat.format(new Date());
        mTvTime.setText(mTime);
    }

    @Override
    protected void onPause() {
        super.onPause();
        RxBus.getInstance().unSubscribe(this);
        hideBtn();
        mUnityPlayer.pause();
    }

    private void subscribeEvent() {
        Disposable sysSubscription = RxBus.getInstance()
                .toObservable(SystemMessageEvent.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .subscribe(new Consumer<SystemMessageEvent>() {
                    @Override
                    public void accept(SystemMessageEvent systemMessageEvent) throws Exception {
                        if(PreferenceUtils.getMessageDot(MapActivity.this,"neta") || PreferenceUtils.getMessageDot(MapActivity.this,"system") || PreferenceUtils.getMessageDot(MapActivity.this,"at_user")){
                            mCardDot.setVisibility(View.VISIBLE);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        RxBus.getInstance().addSubscription(this, sysSubscription);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mUnityPlayer.lowMemory();
    }

    @Override
    public void onTrimMemory(int var1) {
        super.onTrimMemory(var1);
        if(var1 == 15) {
            this.mUnityPlayer.lowMemory();
        }
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
            onBackPressed();
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
    protected void onResume() {
        super.onResume();
        showBtn();
        subscribeEvent();
        subscribeSearchChangedEvent();
        mUnityPlayer.resume();
        if(PreferenceUtils.getMessageDot(this,"neta") || PreferenceUtils.getMessageDot(this,"system") || PreferenceUtils.getMessageDot(this,"at_user") ){//|| showDot){
            mCardDot.setVisibility(View.VISIBLE);
        }else {
            mCardDot.setVisibility(View.GONE);
        }
        String mTime = dateFormat.format(new Date());
        mTvTime.setText(mTime);
        ViewUtils.setRoleButton(mIvRole);
    }

    @Override
    public void onGetTimeSuccess(Date time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        String id = JuQingUtil.checkJuQing(calendar);
        if(!TextUtils.isEmpty(id)){
            if(JuQingUtil.isForce(id)){
                Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                        .appendPath("conversation").appendPath("private")
                        .appendQueryParameter("targetId", "juqing").build();
                Intent i2 = new Intent(MapActivity.this,PhoneMainActivity.class);
                i2.setData(uri);
                startActivity(i2);
            }
        }
    }

    @Override
    public void onGetTriggerSuccess(ArrayList<JuQingTriggerEntity> entities) {
        JuQingUtil.saveJuQingTriggerList(entities);
    }

    @Override
    public void onGetAllStorySuccess(ArrayList<JuQIngStoryEntity> entities) {
        JuQingUtil.saveJuQingStoryList(entities);
    }

    @Override
    public void onCheckStoryVersionSuccess(int version) {
        int version1 = PreferenceUtils.getJuQingVersion(this);
        if(version1 < version){
            mPresenter.getAllStory();
            mPresenter.getTrigger();
        }
        mPresenter.getServerTime();
    }

    @Override
    public void getEventSuccess(ArrayList<NetaEvent> events) {
        for (NetaEvent event : events){
            if(event.getSign().equals("BS")){
                if(!TextUtils.isEmpty(event.getSchedule())) PreferenceUtils.setBackSchoolLevel(this,Integer.valueOf(event.getSchedule()));
            }
        }
    }

    @Override
    public void saveEventSuccess() {

    }

    private void showEventDialog(String content, final int type){
        final AlertDialogUtil alertDialogUtil = AlertDialogUtil.getInstance();
        alertDialogUtil.createNormalDialog(this,content);
        alertDialogUtil.setOnClickListener(new AlertDialogUtil.OnClickListener() {
            @Override
            public void CancelOnClick() {
                alertDialogUtil.dismissDialog();
            }

            @Override
            public void ConfirmOnClick() {
                alertDialogUtil.dismissDialog();
                if(type == 1){
                    backSchoolEvent();
                }else if(type == 2){
                    AppSetting.isShowBackSchoolAll = true;
                    PreferenceUtils.setAllBackSchool(MapActivity.this,true);
                }
            }
        });
        alertDialogUtil.showDialog();
    }

    private void backSchoolEvent(){
        if(!mIsOut){
            imgOut();
            mIsOut = true;
        }

        mEventTv = new TextView(this);
        mEventTv.setGravity(Gravity.CENTER);
        mEventTv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mEventTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
        mEventTv.setTextColor(Color.WHITE);
        mEventTv.setBackgroundColor(ContextCompat.getColor(this,R.color.alph_80));
        TextPaint paint = mEventTv.getPaint();
        paint.setFakeBoldText(true);
        mEventTv.setText("“我在哪儿？我是谁？”\n" + "我慌乱的从地上做了起来，周围烟雾弥漫，气氛诡异");
        mMainRoot.addView(mEventTv);
        mEventTv.bringToFront();
        mEventTv.setOnClickListener(new View.OnClickListener() {
            int i = 1;
            @Override
            public void onClick(View v) {
                switch (i){
                    case 1:
                        mEventTv.setText("隐约记得我是在找人来着\n" + "也不知着了什么魔怔，一下什么也想不起来\n" + "“魏仲廷！对了，就是他！”");
                        i++;
                        break;
                    case 2:
                        mEventTv.setText("脑海里，隐约想起了一些\n" + "记得是一场台风，将我两个人困在了学校\n" + "和他的相识便是在这个时候");
                        i++;
                        break;
                    case 3:
                        mEventTv.setText("本来在教室说笑的我们本打算就这么过夜了\n" + "突然他说要去打电话试试能不能联系外界，便让他去了");
                        i++;
                        break;
                    case 4:
                        mEventTv.setText("他的脚步声轻声回荡，勾起我心中一丝不安\n" + "然而时间戛然停止，世界分崩离析\n" + "转眼间虚无已将我吞噬……\n" + "回过神来时……我已站在了这儿");
                        i++;
                        break;
                    case 5:
                        mEventTv.setVisibility(View.GONE);
                        mMainRoot.removeView(mEventTv);
                        mEventTv.setOnClickListener(null);
                        mEventTv = null;
                        //showEventMapMark(false);
                        break;
                }
            }
        });
    }

    private void subscribeSearchChangedEvent() {
        Disposable subscription = RxBus.getInstance()
                .toObservable(BackSchoolEvent.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .subscribe(new Consumer<BackSchoolEvent>() {
                    @Override
                    public void accept(BackSchoolEvent backSchoolEvent) throws Exception {
                        mPresenter.saveEvent(new NetaEvent(backSchoolEvent.getPass() + "", "BS"));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        RxBus.getInstance().addSubscription(this, subscription);
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    public void checkBuildSuccess(BuildEntity s) {
    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void initData() {

    }

    @Override
    public void changeSignState(SignEntity entity,boolean sign) {
        if(sign) showToast(R.string.label_sign_suc);
    }

    public void loadPerson(){
        mPresenter.requestPersonMain();
    }

    @Override
    public void onPersonMainLoad(PersonalMainEntity entity) {
        PersonalLevelActivity.startActivity(this,entity.getLevelName(),entity.getLevelColor(),entity.getScore(),entity.getLevelScoreStart(),entity.getLevelScoreEnd(),entity.getLevel());
    }

    @Override
    public void showUpdateDialog(final AppUpdateEntity entity) {
        if (this.isFinishing()) return;
        final AlertDialogUtil alertDialogUtil = AlertDialogUtil.getInstance();
        alertDialogUtil.createPromptDialog(this, entity.getTitle(), entity.getContent());
        alertDialogUtil.setButtonText(getString(R.string.label_update), getString(R.string.label_later), entity.getUpdateStatus());
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
                    Uri uri = Uri.parse(entity.getUrl());
                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    updateApkName = "neta-update" + new Date().getTime() + ".apk";
                    request.setDestinationInExternalFilesDir(MapActivity.this, null, updateApkName);
                    mUpdateDownloadId = downloadManager.enqueue(request);
                } catch (Throwable t) {
                    t.printStackTrace();
                    showToast(R.string.label_error_storage);
                }
            }
        });
        alertDialogUtil.showDialog();
    }

    @Override
    public void onFailure(int code,String msg) {
        mIsSignPress = false;
        ErrorCodeUtils.showErrorMsgByCode(MapActivity.this,code,msg);
    }

    @OnClick({R.id.iv_bag,R.id.iv_card,R.id.rl_main_list_root,R.id.iv_main,R.id.iv_sign,R.id.iv_create_dynamic,R.id.iv_create_wenzhang,R.id.iv_role})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.iv_bag:
                if(NetworkUtils.checkNetworkAndShowError(this) && DialogUtils.checkLoginAndShowDlg(MapActivity.this)){
                    if(PreferenceUtils.getAuthorInfo().isOpenBag()){
                        Intent i2 = new Intent(MapActivity.this,NewBagActivity.class);
                        i2.putExtra(UUID,PreferenceUtils.getUUid());
                        startActivity(i2);
                    }else {
                        Intent i2 = new Intent(MapActivity.this,BagOpenActivity.class);
                        startActivity(i2);
                    }
                }
                break;
            case R.id.iv_card:
                if(NetworkUtils.checkNetworkAndShowError(this) && DialogUtils.checkLoginAndShowDlg(MapActivity.this)){
                    Intent i1 = new Intent(MapActivity.this,NewPersonalActivity.class);
                    i1.putExtra(UUID,PreferenceUtils.getUUid());
                    startActivity(i1);
                }
                break;
            case R.id.rl_main_list_root:
                if(NetworkUtils.checkNetworkAndShowError(this) && DialogUtils.checkLoginAndShowDlg(MapActivity.this)){
                    Intent i2 = new Intent(MapActivity.this,PhoneMainActivity.class);
                    startActivity(i2);
                }
                break;
            case R.id.iv_main:
                Intent i3 = new Intent(MapActivity.this,WallBlockActivity.class);
                startActivity(i3);
                overridePendingTransition(R.anim.main_list_in,0);
                break;
            case R.id.iv_sign:
                if(DialogUtils.checkLoginAndShowDlg(MapActivity.this) && !mIsSignPress){
                    mIsSignPress = true;
                    mPresenter.getDailyTask();
                }
                break;
            case R.id.iv_create_dynamic:
                Intent i4 = new Intent(MapActivity.this,CreateDynamicActivity.class);
                i4.putExtra("default_tag","广场");
                startActivity(i4);
                break;
            case R.id.iv_create_wenzhang:
                Intent intent = new Intent(MapActivity.this, CreateRichDocActivity.class);
                intent.putExtra(CreateRichDocActivity.TYPE_QIU_MING_SHAN,3);
                intent.putExtra(CreateRichDocActivity.TYPE_TAG_NAME_DEFAULT,"书包");
                intent.putExtra("from_name","书包");
                intent.putExtra("from_schema","neta://com.moemoe.lalala/bag_2.0");
                startActivityForResult(intent, REQUEST_CODE_CREATE_DOC);
                break;
            case R.id.iv_role:
                clickRole();
                break;
        }
    }

    private void clickRole(){
        mIvRole.setSelected(!mIvRole.isSelected());
        if(mIvRole.isSelected()){
            mIvCreatDynamic.setVisibility(View.VISIBLE);
            mIvCreateWen.setVisibility(View.VISIBLE);
            mTvText.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mRoleRoot.setLayoutParams(lp);
            mRoleRoot.setBackgroundColor(ContextCompat.getColor(MapActivity.this,R.color.alph_60));
            mRoleRoot.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {

                }
            });
            RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            lp1.addRule(RelativeLayout.ALIGN_PARENT_END);
            mIvRole.setLayoutParams(lp1);
        }else {
            mIvCreatDynamic.setVisibility(View.GONE);
            mIvCreateWen.setVisibility(View.GONE);
            mTvText.setVisibility(View.GONE);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            lp.addRule(RelativeLayout.ALIGN_PARENT_END);
            mRoleRoot.setLayoutParams(lp);
            mRoleRoot.setBackgroundColor(ContextCompat.getColor(MapActivity.this,R.color.transparent));
            mRoleRoot.setOnClickListener(null);
            RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mIvRole.setLayoutParams(lp1);
        }
    }

    @Override
    public void onDailyTaskLoad(DailyTaskEntity entity) {
        mIsSignPress = false;
        SignDialog dialog = new SignDialog(MapActivity.this);
        dialog.setTask(entity);
        dialog.setAnimationEnable(true)
                .setPositiveListener(new SignDialog.OnPositiveListener() {
                    @Override
                    public void onClick(SignDialog dialog) {
                        if(NetworkUtils.checkNetworkAndShowError(MapActivity.this)){
                            mPresenter.signToday(dialog);
                        }
                    }
                }).show();
    }

    private void imgIn(){
        ObjectAnimator cardAnimator = ObjectAnimator.ofFloat(mCardRoot,"translationY",-mCardRoot.getHeight()- DensityUtil.dip2px(this,12),0).setDuration(300);
        cardAnimator.setInterpolator(new OvershootInterpolator());
        ObjectAnimator bagAnimator = ObjectAnimator.ofFloat(mIvBag,"translationY",-mIvBag.getHeight()- DensityUtil.dip2px(this,12),0).setDuration(300);
        bagAnimator.setInterpolator(new OvershootInterpolator());
        ObjectAnimator squareAnimator = ObjectAnimator.ofFloat(mIvMain,"translationY",mIvMain.getHeight(),0).setDuration(300);
        squareAnimator.setInterpolator(new OvershootInterpolator());
        ObjectAnimator phoneAnimator = ObjectAnimator.ofFloat(mPhoneRoot,"translationY",mPhoneRoot.getHeight(),0).setDuration(300);
        phoneAnimator.setInterpolator(new OvershootInterpolator());
        ObjectAnimator signAnimator = ObjectAnimator.ofFloat(mIvSign,"translationX",mIvSign.getWidth() + DensityUtil.dip2px(this,14),0).setDuration(300);
        signAnimator.setInterpolator(new OvershootInterpolator());
        ObjectAnimator roleAnimator = ObjectAnimator.ofFloat(mRoleRoot,"translationY",mRoleRoot.getHeight(),0).setDuration(300);
        roleAnimator.setInterpolator(new OvershootInterpolator());
        AnimatorSet set = new AnimatorSet();
        set.play(cardAnimator).with(bagAnimator);
        set.play(bagAnimator).with(squareAnimator);
        set.play(squareAnimator).with(phoneAnimator);
        set.play(phoneAnimator).with(signAnimator);
        set.play(signAnimator).with(roleAnimator);
        set.start();
    }

    private void imgOut(){
        ObjectAnimator cardAnimator = ObjectAnimator.ofFloat(mCardRoot,"translationY",0,-mCardRoot.getHeight()- DensityUtil.dip2px(this,12)).setDuration(300);
        cardAnimator.setInterpolator(new OvershootInterpolator());
        ObjectAnimator bagAnimator = ObjectAnimator.ofFloat(mIvBag,"translationY",0,-mIvBag.getHeight()- DensityUtil.dip2px(this,12)).setDuration(300);
        bagAnimator.setInterpolator(new OvershootInterpolator());
        ObjectAnimator squareAnimator = ObjectAnimator.ofFloat(mIvMain,"translationY",0,mIvMain.getHeight()).setDuration(300);
        squareAnimator.setInterpolator(new OvershootInterpolator());
        ObjectAnimator phoneAnimator = ObjectAnimator.ofFloat(mPhoneRoot,"translationY",0,mPhoneRoot.getHeight()).setDuration(300);
        phoneAnimator.setInterpolator(new OvershootInterpolator());
        ObjectAnimator signAnimator = ObjectAnimator.ofFloat(mIvSign,"translationX",0,mIvSign.getWidth() + DensityUtil.dip2px(this,14)).setDuration(300);
        signAnimator.setInterpolator(new OvershootInterpolator());
        ObjectAnimator roleAnimator = ObjectAnimator.ofFloat(mRoleRoot,"translationY",0,mRoleRoot.getHeight()).setDuration(300);
        roleAnimator.setInterpolator(new OvershootInterpolator());
        AnimatorSet set = new AnimatorSet();
        set.play(cardAnimator).with(bagAnimator);
        set.play(bagAnimator).with(squareAnimator);
        set.play(squareAnimator).with(phoneAnimator);
        set.play(phoneAnimator).with(signAnimator);
        set.play(signAnimator).with(roleAnimator);
        set.start();
    }

    private void showBtn(){
        mCardRoot.setVisibility(View.VISIBLE);
        mIvBag.setVisibility(View.VISIBLE);
        mIvMain.setVisibility(View.VISIBLE);
        mIvSign.setVisibility(View.VISIBLE);
        mIvRole.setVisibility(View.VISIBLE);
        mPhoneRoot.setVisibility(View.VISIBLE);
    }

    private void hideBtn(){
        mCardRoot.setVisibility(View.INVISIBLE);
        mIvBag.setVisibility(View.INVISIBLE);
        mIvMain.setVisibility(View.INVISIBLE);
        mIvSign.setVisibility(View.INVISIBLE);
        mIvRole.setVisibility(View.INVISIBLE);
        mPhoneRoot.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        if(mIvRole.isSelected()){
            clickRole();
            return;
        }
        long currentTime = System.currentTimeMillis();
        if(currentTime - mLastBackTime > 2000){
            showToast(R.string.msg_click_twice_to_exit);
            mLastBackTime = currentTime;
        }else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    @Override
    protected void onDestroy() {
        if(mPresenter != null) mPresenter.release();
        if(isGisterReciver){
            unregisterReceiver(mReceiver);
            isGisterReciver = false;
        }
        RxBus.getInstance().unSubscribe(this);
        AppSetting.isRunning = false;
        RongIM.getInstance().removeUnReadMessageCountChangedObserver(this);
        mUnityPlayer.quit();
        super.onDestroy();
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

    @Override
    protected void restartApp() {
        startActivity(new Intent(this, SplashActivity.class));
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int action = intent.getIntExtra(AppStatusConstant.KEY_HOME_ACTION,AppStatusConstant.ACTION_BACK_TO_HOME);
        switch (action) {
            case AppStatusConstant.ACTION_RESTART_APP:
                restartApp();
                break;
        }
    }

    @Override
    public void onCountChanged(int i) {
        if(i > 0){
            mTvMsg.setText(i + "条新消息");
            mTvMsg.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this,R.drawable.ic_inform_reddot),null,null,null);
            mTvMsg.setCompoundDrawablePadding((int) getResources().getDimension(R.dimen.x4));
        }else {
            mTvMsg.setText("无新信息");
            mTvMsg.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
            mTvMsg.setCompoundDrawablePadding(0);
        }
    }
}
