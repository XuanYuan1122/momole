package com.moemoe.lalala.view.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.app.AppStatusConstant;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.app.RxBus;
import com.moemoe.lalala.di.components.DaggerMapComponent;
import com.moemoe.lalala.di.modules.MapModule;
import com.moemoe.lalala.dialog.SignDialog;
import com.moemoe.lalala.event.BackSchoolEvent;
import com.moemoe.lalala.event.PrivateMessageEvent;
import com.moemoe.lalala.event.SystemMessageEvent;
import com.moemoe.lalala.galgame.FileManager;
import com.moemoe.lalala.galgame.Live2DManager;
import com.moemoe.lalala.galgame.Live2DView;
import com.moemoe.lalala.galgame.SoundManager;
import com.moemoe.lalala.greendao.gen.PrivateMessageItemEntityDao;
import com.moemoe.lalala.model.entity.AppUpdateEntity;
import com.moemoe.lalala.model.entity.AuthorInfo;
import com.moemoe.lalala.model.entity.BuildEntity;
import com.moemoe.lalala.model.entity.DailyTaskEntity;
import com.moemoe.lalala.model.entity.MapMarkContainer;
import com.moemoe.lalala.model.entity.MapMarkEntity;
import com.moemoe.lalala.model.entity.NetaEvent;
import com.moemoe.lalala.model.entity.PersonalMainEntity;
import com.moemoe.lalala.model.entity.PrivateMessageItemEntity;
import com.moemoe.lalala.model.entity.SignEntity;
import com.moemoe.lalala.model.entity.SnowShowEntity;
import com.moemoe.lalala.presenter.MapContract;
import com.moemoe.lalala.presenter.MapPresenter;
import com.moemoe.lalala.utils.AlertDialogUtil;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.GreenDaoManager;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ToolTipUtils;
import com.moemoe.lalala.view.widget.explosionfield.ExplosionField;
import com.moemoe.lalala.view.widget.map.MapWidget;
import com.moemoe.lalala.view.widget.map.config.OfflineMapConfig;
import com.moemoe.lalala.view.widget.map.events.MapTouchedEvent;
import com.moemoe.lalala.view.widget.map.events.ObjectTouchEvent;
import com.moemoe.lalala.view.widget.map.interfaces.Layer;
import com.moemoe.lalala.view.widget.map.interfaces.MapEventsListener;
import com.moemoe.lalala.view.widget.map.interfaces.OnMapTilesFinishedLoadingListener;
import com.moemoe.lalala.view.widget.map.interfaces.OnMapTouchListener;
import com.moemoe.lalala.view.widget.map.model.MapImage;
import com.moemoe.lalala.view.widget.map.model.MapImgLayer;
import com.moemoe.lalala.view.widget.map.model.MapObject;
import com.moemoe.lalala.view.widget.tooltip.Tooltip;
import com.moemoe.lalala.view.widget.tooltip.TooltipAnimation;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 地图主界面
 * Created by yi on 2016/11/27.
 */

public class MapActivity extends BaseAppCompatActivity implements MapContract.View {

    public static final int REQ_SELECT_FUKU = 5555;
    private static final int MAP_SCHOOL = 0;
    private static final int MAP_SCHOOL_YORU = 1;
    private static final int MAP_SCHOOL_KILL = 2;
    private static final int MAP_SCHOOL_BACK = 3;

    @BindView(R.id.main_root)
    RelativeLayout mMainRoot;
    @BindView(R.id.fl_map_root)
    FrameLayout mMap;
    @BindView(R.id.iv_bag)
    ImageView mIvBag;
    @BindView(R.id.iv_search)
    ImageView mIvSearch;
    @BindView(R.id.iv_cal)
    ImageView mIvCal;
    @BindView(R.id.iv_card)
    ImageView mIvCard;
    @BindView(R.id.fl_card_root)
    View mCardRoot;
    @BindView(R.id.iv_card_dot)
    View mCardDot;
    @BindView(R.id.iv_live2d)
    ImageView mIvGal;
    @BindView(R.id.iv_square)
    ImageView mIvSquare;
    @BindView(R.id.live2DLayout)
    FrameLayout mLive2DLayout;
    @BindView(R.id.tv_exit_live2d)
    TextView mExitLive2D;
    @BindView(R.id.iv_select_deskmate)
    ImageView mIvSelectMate;
    @BindView(R.id.iv_select_language)
    ImageView mIvSelectLanguage;
    @BindView(R.id.iv_select_fuku)
    ImageView mIvSelectFuku;
    @BindView(R.id.iv_sound_load)
    ImageView mIvSoundLoad;
    @BindView(R.id.iv_sign)
    ImageView mIvSign;
    @BindView(R.id.btn_ip)
    Button mIp;
    @Inject
    MapPresenter mPresenter;
    private MapWidget mapWidget;
    private TextView mEventTv;

    private boolean isGisterReciver;
    private long mLastBackTime = 0;
    private String mSchema;
    public static String updateApkName;
    private Live2DManager live2DMgr;
    private String mFuku;
    private Live2DView mLive2dView;
    private boolean mIsOut = false;
    private int mMapState = MAP_SCHOOL;
    private ObjectAnimator mSoundLoadAnim;
    public static long mUpdateDownloadId = Integer.MIN_VALUE;
    private MapMarkContainer mContainer;
    private ExplosionField mExplosionField;
    private boolean mIsSignPress = false;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_map;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
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
        if(PreferenceUtils.isAppFirstLaunch(this) || PreferenceUtils.isVersion2FirstLaunch(this)){
            Intent i = new Intent(this,MengXinActivity.class);
            startActivity(i);
        }
        initMap("map");
        SoundManager.init(this);
        FileManager.init(this);
        mFuku = PreferenceUtils.getSelectFuku(this);
        mExplosionField = ExplosionField.attach2Window(this);
        live2DMgr = new Live2DManager(mFuku);
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
    }

    private void initMap(String map){
        mapWidget = new MapWidget(this,map,12);
        mapWidget.centerMap();
        float scale = (float) DensityUtil.getScreenHeight(this) / mapWidget.getOriginalMapHeight();
        mapWidget.setScale(scale);
        OfflineMapConfig config = mapWidget.getConfig();
        mapWidget.scrollMapTo(0,0);
        config.setPinchZoomEnabled(true);
        config.setFlingEnabled(true);
        config.setMaxZoomLevelLimit(14);
        config.setMinZoomLevelLimit(12);
        config.setZoomBtnsVisible(false);
        config.setMapCenteringEnabled(true);
        mMap.removeAllViews();
        mMap.addView(mapWidget);
        if(mMapState == MAP_SCHOOL){
            mPresenter.addDayMapMark(this,mapWidget,scale);
        }else if(mMapState == MAP_SCHOOL_YORU){
            mPresenter.addNightMapMark(this,mapWidget,scale);
        }else if(mMapState == MAP_SCHOOL_KILL){
            mPresenter.addNightEventMapMark(this,mapWidget,scale);
        }else if(mMapState == MAP_SCHOOL_BACK){
            mPresenter.addBackSchoolMapMark(this,mapWidget,scale);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        RxBus.getInstance().unSubscribe(this);
        hideBtn();
    }

    private void clearMap(){
        if(mapWidget != null){
            mapWidget.clearLayers();
            mapWidget = null;
        }
    }

    private void subscribeEvent() {
        Subscription subscription = RxBus.getInstance()
                .toObservable(PrivateMessageEvent.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .subscribe(new Action1<PrivateMessageEvent>() {
                    @Override
                    public void call(PrivateMessageEvent event) {
                        if(event.isShow()){
                            mCardDot.setVisibility(View.VISIBLE);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
        Subscription sysSubscription = RxBus.getInstance()
                .toObservable(SystemMessageEvent.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .subscribe(new Action1<SystemMessageEvent>() {
                    @Override
                    public void call(SystemMessageEvent event) {
                        if(PreferenceUtils.getMessageDot(MapActivity.this,"neta") || PreferenceUtils.getMessageDot(MapActivity.this,"system") || PreferenceUtils.getMessageDot(MapActivity.this,"at_user")){
                            mCardDot.setVisibility(View.VISIBLE);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
        RxBus.getInstance().addSubscription(this, subscription);
        RxBus.getInstance().addSubscription(this, sysSubscription);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showBtn();
        subscribeEvent();
        subscribeSearchChangedEvent();
        if(StringUtils.isyoru()){
            if(StringUtils.isBackSchool()){
                if(mMapState != MAP_SCHOOL_BACK){
                    clearMap();
                    mMapState = MAP_SCHOOL_BACK;
                    initMap("map_back_school");
                    initMapListeners();
                    if(!PreferenceUtils.getBackSchoolDialog(this) && PreferenceUtils.getBackSchoolLevel(this) == 0){
                        mPresenter.getServerTime();
                    }
                }
            }else if(!AppSetting.isEnterEventToday && StringUtils.isKillEvent() ){
                if(mMapState != MAP_SCHOOL_KILL){
                    clearMap();
                    mMapState = MAP_SCHOOL_KILL;
                    initMap("map_kill_event");
                    initMapListeners();
                }
            }else if(mMapState != MAP_SCHOOL_YORU){
                clearMap();
                mMapState = MAP_SCHOOL_YORU;
                initMap("map_yoru");
                initMapListeners();
            }
        }else {
            if(mMapState != MAP_SCHOOL){
                clearMap();
                mMapState = MAP_SCHOOL;
                initMap("map");
                initMapListeners();
            }
            Layer layer = mapWidget.getLayerById(3);
            boolean isVisible = layer.isVisible();
            if(!StringUtils.isDayEvent()){
                if(isVisible){
                    layer.setVisible(false);
                    mapWidget.invalidate();
                }
            }else {
                if(!isVisible){
                    layer.setVisible(true);
                    mapWidget.invalidate();
                }
            }

        }
        PrivateMessageItemEntityDao dao = GreenDaoManager.getInstance().getSession().getPrivateMessageItemEntityDao();
        List<PrivateMessageItemEntity> list = dao.queryBuilder().list();
        boolean showDot = false;
        for(PrivateMessageItemEntity entity : list){
            if(entity.getDot() > 0){
                showDot = true;
                break;
            }
        }
        if(PreferenceUtils.getMessageDot(this,"neta") || PreferenceUtils.getMessageDot(this,"system") || PreferenceUtils.getMessageDot(this,"at_user") || showDot){
            mCardDot.setVisibility(View.VISIBLE);
        }else {
            mCardDot.setVisibility(View.GONE);
        }
    }

    @Override
    public void onGetTimeSuccess(Date time) {
        try {
            String temp = "2017-04-30 22:00:00";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date checkTime = sdf.parse(temp);
            if(time.getTime() > checkTime.getTime()){
                showEventDialog(getString(R.string.label_enter_event),1);
                PreferenceUtils.setBackSchoolDialog(this,true);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
                    showEventMapMark(true);
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
                        showEventMapMark(false);
                        break;
                }
            }
        });
    }

    private void showEventMapMark(boolean showAll){
        Layer layer = mapWidget.getLayerById(5);
        if(layer == null){
            return;
        }
        int i = PreferenceUtils.getBackSchoolLevel(this);
        int n = layer.getMapObjectCount();
        for(int h = 0;h < n - 1;h++){
            MapObject mapObject = layer.getMapObjectByIndex(h);
            if(h == i || showAll){
                mapObject.setVisible(true);
            }else {
                mapObject.setVisible(false);
            }
        }
        if(i > 0){
            MapObject mapObject = layer.getMapObjectByIndex(n - 1);
            mapObject.setVisible(true);
        }
        mapWidget.invalidate();
    }

    private void subscribeSearchChangedEvent() {
        Subscription subscription = RxBus.getInstance()
                .toObservable(BackSchoolEvent.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .subscribe(new Action1<BackSchoolEvent>() {
                    @Override
                    public void call(BackSchoolEvent event) {
                        mPresenter.saveEvent(new NetaEvent(event.getPass()+"","BS"));
                        showEventMapMark(false);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
        RxBus.getInstance().addSubscription(this, subscription);
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    public void onSnowmanSuccess(Object objectId,int mapX,int mapY) {
        MapImgLayer layer = (MapImgLayer) mapWidget.getLayerById(233);
        MapImage mapImage = layer.getMapImgObject(objectId);
        layer.removeMapObject(objectId);
        SnowShowEntity.removeFromCache(mapImage.getRealPos().x,mapImage.getRealPos().y);
        mExplosionField.setPosition(mapX,mapY);
        mExplosionField.explode(mapImage);
        mapWidget.invalidate();
    }

    @Override
    public void checkBuildSuccess(BuildEntity s) {
    }

    private void initMapListeners(){
        mapWidget.setOnMapTouchListener(new OnMapTouchListener() {
            @Override
            public void onTouch(MapWidget v, MapTouchedEvent event) {
                List<ObjectTouchEvent> objectTouchEvents = event.getTouchedObjectEvents();
                if(objectTouchEvents.size() == 0){
                    if(mIsOut){
                        imgIn();
                        mIsOut = false;
                    }else {
                        imgOut();
                        mIsOut = true;
                    }
                }
                if(objectTouchEvents.size() == 1){
                    int mapX = event.getScreenX();
                    int mapY = event.getScreenY();

                    ObjectTouchEvent objectTouchEvent = objectTouchEvents.get(0);
                    long layerId = objectTouchEvent.getLayerId();
                    Object objectId =  objectTouchEvent.getObjectId();
                    if(layerId == 233){
                        mExplosionField.clear();
//                        MapImgLayer layer = (MapImgLayer) mapWidget.getLayerById(233);
//                        MapImage mapImage = layer.getMapImgObject(objectId);
//                        layer.removeMapObject(objectId);
//                        SnowShowEntity.removeFromCache(mapImage.getRealPos().x,mapImage.getRealPos().y);
//                        mExplosionField.setPosition(mapX,mapY);
//                        mExplosionField.explode(mapImage);
//                        mapWidget.invalidate();
                        mPresenter.clickSnowman(objectId,mapX,mapY);
                    }else {
                        MapMarkEntity entity = mContainer.getMarkById((String) objectId);
                        if(!TextUtils.isEmpty(entity.getSchema())){
                            String temp = entity.getSchema();
                            if(entity.getId().equals("扭蛋机抽奖")){
                                if (DialogUtils.checkLoginAndShowDlg(MapActivity.this)){
                                    AuthorInfo authorInfo =  PreferenceUtils.getAuthorInfo();
                                    try {
                                        temp += "?user_id=" + authorInfo.getUserId()
                                                + "&nickname=" + (TextUtils.isEmpty(authorInfo.getUserName())? "" : URLEncoder.encode(authorInfo.getUserName(),"UTF-8"))
                                                + "&token=" + PreferenceUtils.getToken();
                                        Uri uri = Uri.parse(temp);
                                        IntentUtils.haveShareWeb(MapActivity.this, uri, v);
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }else if(entity.getId().equals("返校-人物")){
                                int level = PreferenceUtils.getBackSchoolLevel(MapActivity.this);
                                if(level == 5){
                                    if(!AppSetting.isShowBackSchoolAll) showEventDialog("是否回顾剧情",2);
                                }else {
                                    temp += "?token=" + PreferenceUtils.getToken()
                                            + "&full_screen";
                                    Uri uri = Uri.parse(temp);
                                    IntentUtils.toActivityFromUri(MapActivity.this, uri, v);
                                }
                            }else {
                                if(temp.contains("http://prize.moemoe.la:8000/mt")){
                                    AuthorInfo authorInfo =  PreferenceUtils.getAuthorInfo();
                                    temp +="?user_id=" + authorInfo.getUserId() + "&nickname="+authorInfo.getUserName();
                                }
                                if(temp.contains("http://prize.moemoe.la:8000/netaopera/chap")){
                                    AuthorInfo authorInfo =  PreferenceUtils.getAuthorInfo();
                                    temp +="?pass=" + PreferenceUtils.getPassEvent(MapActivity.this) + "&user_id=" + authorInfo.getUserId();
                                }
                                if(temp.contains("http://neta.facehub.me/")){
                                    AuthorInfo authorInfo =  PreferenceUtils.getAuthorInfo();
                                    temp +="?open_id=" + authorInfo.getUserId() + "&nickname=" + authorInfo.getUserName() + "&pay_way=alipay,wx,qq"+"&full_screen";
                                }
                                if(temp.contains("fanxiao/final.html")){
                                    temp += "?token=" + PreferenceUtils.getToken()
                                            + "&full_screen";
                                }
                                if(temp.contains("fanxiao/paihang.html")){
                                    temp += "?token=" + PreferenceUtils.getToken();
                                }
                                Uri uri = Uri.parse(temp);
                                IntentUtils.toActivityFromUri(MapActivity.this, uri, v);
                            }
                        }else {
                            TextView textView = (TextView) LayoutInflater.from(MapActivity.this).inflate(R.layout.tooltip_textview, null);
                            int viewHeight = mapWidget.getMapHeight();
                            int type;
                            if(entity.getY() < viewHeight / 2){
                                type = Tooltip.BOTTOM;
                            }else {
                                type = Tooltip.TOP;
                            }
                            int x = xToScreenCoords(entity.getX());
                            int y = yToScreenCoords(entity.getY());
                            if(TextUtils.isEmpty(entity.getContent())){
                                Random random = new Random();
                                int i = random.nextInt(entity.getContents().size());
                                ToolTipUtils.showTooltip(MapActivity.this, mMap, textView, v, entity.getContents().get(i),type,mapX,mapY,entity.getW(),entity.getH(),true,
                                        TooltipAnimation.SCALE_AND_FADE,
                                        ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ContextCompat.getColor(MapActivity.this, R.color.main_cyan));
                                // showLocationsPopup(x,y,entity.getContents().get(i));
                            }else {
                                //  showLocationsPopup(x,y,entity.getContent());
                                ToolTipUtils.showTooltip(MapActivity.this, mMap, textView, v, entity.getContent(),type, mapX,mapY,entity.getW(),entity.getH(),true,
                                        TooltipAnimation.SCALE_AND_FADE,
                                        ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ContextCompat.getColor(MapActivity.this, R.color.main_cyan));
                            }
                        }
                    }
                }
            }
        });
        mapWidget.addMapEventsListener(new MapEventsListener() {
            @Override
            public void onPreZoomIn() {
            }

            @Override
            public void onPostZoomIn() {
            }

            @Override
            public void onPreZoomOut() {
            }

            @Override
            public void onPostZoomOut() {
            }
        });
        mapWidget.setOnMapTilesFinishLoadingListener(new OnMapTilesFinishedLoadingListener() {
            @Override
            public void onMapTilesFinishedLoading() {

            }
        });
    }

    @Override
    protected void initListeners() {
        initMapListeners();
        live2DMgr.setOnSoundLoadListener(new Live2DManager.OnSoundLoadListener() {
            @Override
            public void OnStart() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mIvSoundLoad.setVisibility(View.VISIBLE);
                        soundLoading();
                    }
                });
            }

            @Override
            public void OnLoad(int count, int position) {

            }

            @Override
            public void OnFinish() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mSoundLoadAnim != null) {
                            mSoundLoadAnim.end();
                            mSoundLoadAnim = null;
                        }
                        mIvSoundLoad.setVisibility(View.GONE);
                    }
                });
            }
        });
        if(!TextUtils.isEmpty(mSchema)){
            IntentUtils.toActivityFromUri(this, Uri.parse(mSchema),null);
        }
    }

    private int xToScreenCoords(int mapCoord) {
        return (int)(mapCoord *  mapWidget.getScale() - mapWidget.getScrollX());
    }

    private int yToScreenCoords(int mapCoord) {
        return (int)(mapCoord *  mapWidget.getScale() - mapWidget.getScrollY());
    }

    private void soundLoading(){
        mSoundLoadAnim = ObjectAnimator.ofFloat(mIvSoundLoad,"alpha",0.2f,1f).setDuration(300);
        mSoundLoadAnim.setInterpolator(new LinearInterpolator());
        mSoundLoadAnim.setRepeatMode(ValueAnimator.REVERSE);
        mSoundLoadAnim.setRepeatCount(ValueAnimator.INFINITE);
        mSoundLoadAnim.start();
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
    public void onMapMarkLoaded(MapMarkContainer container) {
        mContainer = container;
    }

    @Override
    public void onFailure(int code,String msg) {
        mIsSignPress = false;
        ErrorCodeUtils.showErrorMsgByCode(MapActivity.this,code,msg);
    }

    @OnClick({R.id.iv_bag,R.id.iv_search,R.id.iv_cal,R.id.iv_card,R.id.iv_live2d,R.id.iv_square,R.id.tv_exit_live2d,R.id.iv_select_deskmate,R.id.iv_select_fuku,R.id.iv_select_language,R.id.iv_sign})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.iv_bag:
                if(NetworkUtils.checkNetworkAndShowError(this) && DialogUtils.checkLoginAndShowDlg(MapActivity.this)){
                    Intent i2 = new Intent(MapActivity.this,BagActivity.class);
                    i2.putExtra(UUID,PreferenceUtils.getUUid());
                    startActivity(i2);
                }
                break;
            case R.id.iv_cal:
                Intent i = new Intent(MapActivity.this,NewCalendarActivity.class);
                startActivity(i);
                break;
            case R.id.iv_card:
                if(NetworkUtils.checkNetworkAndShowError(this) && DialogUtils.checkLoginAndShowDlg(MapActivity.this)){
                    Intent i1 = new Intent(MapActivity.this,NewPersonalActivity.class);
                    i1.putExtra(UUID,PreferenceUtils.getUUid());
                    startActivity(i1);
                }
                break;
            case R.id.iv_live2d:
                if(mLive2dView == null){
                    mLive2dView = live2DMgr.createView(this) ;
                    mLive2DLayout.addView(mLive2dView, 0, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                }
                mLive2DLayout.setVisibility(View.VISIBLE);
                mExitLive2D.setVisibility(View.VISIBLE);
                mIvSelectMate.setVisibility(View.VISIBLE);
                mIvSelectFuku.setVisibility(View.VISIBLE);
                mIvSelectLanguage.setVisibility(View.VISIBLE);
                mIvGal.setVisibility(View.GONE);
                break;
            case R.id.iv_square:
                Intent i3 = new Intent(MapActivity.this,WallBlockActivity.class);
                startActivity(i3);
                break;
            case R.id.tv_exit_live2d:
                mIvGal.setVisibility(View.VISIBLE);
                mLive2DLayout.setVisibility(View.GONE);
                mExitLive2D.setVisibility(View.GONE);
                mIvSelectMate.setVisibility(View.GONE);
                mIvSelectFuku.setVisibility(View.GONE);
                mIvSelectLanguage.setVisibility(View.GONE);
                mIvSoundLoad.setVisibility(View.GONE);
                break;
            case R.id.iv_select_deskmate:
                Intent i4 = new Intent(MapActivity.this,SelectMateActivity.class);
                startActivity(i4);
                break;
            case R.id.iv_select_fuku:
                Intent i5 = new Intent(MapActivity.this,SelectFukuActivity.class);
                startActivityForResult(i5,REQ_SELECT_FUKU);
                break;
            case R.id.iv_select_language:
                showToast(R.string.label_can_not_use);
                break;
            case R.id.iv_sign:
                if(DialogUtils.checkLoginAndShowDlg(MapActivity.this) && !mIsSignPress){
                    mIsSignPress = true;
                    mPresenter.getDailyTask();
                }
                break;
            case R.id.iv_search:
                Intent i6 = new Intent(MapActivity.this,SearchActivity.class);
                startActivity(i6);
                break;
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
                      //  if (!mIsSign){
                        if(NetworkUtils.checkNetworkAndShowError(MapActivity.this)){
                            mPresenter.signToday(dialog);
                        }
//                        }else {
//                            showToast(R.string.label_signed);
//                        }
                    }
                }).show();
    }

    private void imgIn(){
        ObjectAnimator cardAnimator = ObjectAnimator.ofFloat(mCardRoot,"translationY",-mCardRoot.getHeight()- DensityUtil.dip2px(this,14),0).setDuration(300);
        cardAnimator.setInterpolator(new OvershootInterpolator());
        ObjectAnimator bagAnimator = ObjectAnimator.ofFloat(mIvBag,"translationY",-mIvBag.getHeight()- DensityUtil.dip2px(this,14),0).setDuration(300);
        bagAnimator.setInterpolator(new OvershootInterpolator());
        ObjectAnimator searchAnimator = ObjectAnimator.ofFloat(mIvSearch,"translationY",-mIvSearch.getHeight()- DensityUtil.dip2px(this,14),0).setDuration(300);
        searchAnimator.setInterpolator(new OvershootInterpolator());
        ObjectAnimator calAnimator = ObjectAnimator.ofFloat(mIvCal,"translationY",mIvCal.getHeight()+DensityUtil.dip2px(this,5),0).setDuration(300);
        calAnimator.setInterpolator(new OvershootInterpolator());
        ObjectAnimator squareAnimator = ObjectAnimator.ofFloat(mIvSquare,"translationY",mIvSquare.getHeight()+DensityUtil.dip2px(this,5),0).setDuration(300);
        squareAnimator.setInterpolator(new OvershootInterpolator());
        ObjectAnimator galAnimator = ObjectAnimator.ofFloat(mIvGal,"translationY",mIvGal.getHeight(),0).setDuration(300);
        galAnimator.setInterpolator(new OvershootInterpolator());
        ObjectAnimator signAnimator = ObjectAnimator.ofFloat(mIvSign,"translationX",mIvSign.getWidth() + DensityUtil.dip2px(this,14),0).setDuration(300);
        signAnimator.setInterpolator(new OvershootInterpolator());
        AnimatorSet set = new AnimatorSet();
        set.play(cardAnimator).with(bagAnimator);
        set.play(bagAnimator).with(searchAnimator);
        set.play(searchAnimator).with(squareAnimator);
        set.play(squareAnimator).with(calAnimator);
        set.play(calAnimator).with(galAnimator);
        set.play(galAnimator).with(signAnimator);
        set.start();
    }

    private void imgOut(){
        ObjectAnimator cardAnimator = ObjectAnimator.ofFloat(mCardRoot,"translationY",0,-mCardRoot.getHeight()- DensityUtil.dip2px(this,14)).setDuration(300);
        cardAnimator.setInterpolator(new OvershootInterpolator());
        ObjectAnimator bagAnimator = ObjectAnimator.ofFloat(mIvBag,"translationY",0,-mIvBag.getHeight()- DensityUtil.dip2px(this,14)).setDuration(300);
        bagAnimator.setInterpolator(new OvershootInterpolator());
        ObjectAnimator searchAnimator = ObjectAnimator.ofFloat(mIvSearch,"translationY",0,-mIvSearch.getHeight()- DensityUtil.dip2px(this,14)).setDuration(300);
        searchAnimator.setInterpolator(new OvershootInterpolator());
        ObjectAnimator calAnimator = ObjectAnimator.ofFloat(mIvCal,"translationY",0,mIvCal.getHeight()+DensityUtil.dip2px(this,5)).setDuration(300);
        calAnimator.setInterpolator(new OvershootInterpolator());
        ObjectAnimator squareAnimator = ObjectAnimator.ofFloat(mIvSquare,"translationY",0,mIvSquare.getHeight()+DensityUtil.dip2px(this,5)).setDuration(300);
        squareAnimator.setInterpolator(new OvershootInterpolator());
        ObjectAnimator galAnimator = ObjectAnimator.ofFloat(mIvGal,"translationY",0,mIvGal.getHeight()).setDuration(300);
        galAnimator.setInterpolator(new OvershootInterpolator());
        ObjectAnimator signAnimator = ObjectAnimator.ofFloat(mIvSign,"translationX",0,mIvSign.getWidth() + DensityUtil.dip2px(this,14)).setDuration(300);
        signAnimator.setInterpolator(new OvershootInterpolator());
        AnimatorSet set = new AnimatorSet();
        set.play(cardAnimator).with(bagAnimator);
        set.play(bagAnimator).with(searchAnimator);
        set.play(searchAnimator).with(calAnimator);
        set.play(calAnimator).with(squareAnimator);
        set.play(squareAnimator).with(galAnimator);
        set.play(galAnimator).with(signAnimator);
        set.start();
    }

    private void showBtn(){
        mCardRoot.setVisibility(View.VISIBLE);
        mIvBag.setVisibility(View.VISIBLE);
        mIvSearch.setVisibility(View.VISIBLE);
        mIvCal.setVisibility(View.VISIBLE);
        mIvSquare.setVisibility(View.VISIBLE);
        mIvGal.setVisibility(View.VISIBLE);
        mIvSign.setVisibility(View.VISIBLE);
    }

    private void hideBtn(){
        mCardRoot.setVisibility(View.INVISIBLE);
        mIvBag.setVisibility(View.INVISIBLE);
        mIvSearch.setVisibility(View.INVISIBLE);
        mIvCal.setVisibility(View.INVISIBLE);
        mIvSquare.setVisibility(View.INVISIBLE);
        mIvGal.setVisibility(View.INVISIBLE);
        mIvSign.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        if(mIvGal.getVisibility() == View.GONE){
            mIvGal.setVisibility(View.VISIBLE);
            mLive2DLayout.setVisibility(View.GONE);
            mExitLive2D.setVisibility(View.GONE);
            mIvSelectMate.setVisibility(View.GONE);
            mIvSelectFuku.setVisibility(View.GONE);
            mIvSelectLanguage.setVisibility(View.GONE);
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
        if(requestCode == REQ_SELECT_FUKU && resultCode == SelectFukuActivity.RES_OK){
            if(data != null) {
                String fuku = data.getStringExtra("model");
                if(!mFuku.equals(fuku)) {
                    mFuku = fuku;
                    live2DMgr.changeModel(fuku);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        mPresenter.release();
        SnowShowEntity.onDestroy(this);
        if(isGisterReciver){
            unregisterReceiver(mReceiver);
            isGisterReciver = false;
        }
        RxBus.getInstance().unSubscribe(this);
        AppSetting.isRunning = false;
        SoundManager.release();
        FileManager.release();
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
       // super.restartApp();
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
}
