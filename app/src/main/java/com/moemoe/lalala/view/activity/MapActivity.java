package com.moemoe.lalala.view.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.app.MoeMoeApplicationLike;
import com.moemoe.lalala.di.components.DaggerMapComponent;
import com.moemoe.lalala.di.modules.MapModule;
import com.moemoe.lalala.dialog.SignDialog;
import com.moemoe.lalala.galgame.FileManager;
import com.moemoe.lalala.galgame.Live2DManager;
import com.moemoe.lalala.galgame.Live2DView;
import com.moemoe.lalala.galgame.SoundManager;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.AppUpdateEntity;
import com.moemoe.lalala.model.entity.AuthorInfo;
import com.moemoe.lalala.model.entity.BuildEntity;
import com.moemoe.lalala.model.entity.DailyTaskEntity;
import com.moemoe.lalala.model.entity.MapMarkContainer;
import com.moemoe.lalala.model.entity.MapMarkEntity;
import com.moemoe.lalala.model.entity.PersonalMainEntity;
import com.moemoe.lalala.model.entity.SignEntity;
import com.moemoe.lalala.model.entity.SnowShowEntity;
import com.moemoe.lalala.presenter.MapContract;
import com.moemoe.lalala.presenter.MapPresenter;
import com.moemoe.lalala.utils.AlertDialogUtil;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StorageUtils;
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
import com.moemoe.lalala.view.widget.tooltip.Tooltip;
import com.moemoe.lalala.view.widget.tooltip.TooltipAnimation;
import com.tencent.tinker.lib.tinker.TinkerInstaller;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import zlc.season.rxdownload.RxDownload;
import zlc.season.rxdownload.entity.DownloadStatus;

/**
 * 地图主界面
 * Created by yi on 2016/11/27.
 */

public class MapActivity extends BaseAppCompatActivity implements MapContract.View {

    public static final int REQ_SELECT_FUKU = 5555;
    private static final int MAP_SCHOOL = 0;
    private static final int MAP_SCHOOL_YORU = 1;
    private static final int MAP_SCHOOL_KILL = 2;

    @BindView(R.id.fl_map_root)
    FrameLayout mMap;
    @BindView(R.id.iv_bag)
    ImageView mIvBag;
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

    private long mLastBackTime = 0;
    private String mSchema;
    public static String updateApkName;
    private Live2DManager live2DMgr;
    private String mFuku;
    private Live2DView mLive2dView;
    private boolean mIsSign = false;
    private boolean mIsOut = false;
    private boolean mIsPressed = false;
    private boolean mIsFirstIn = false;
    private int signDay;
    private int mMapState = MAP_SCHOOL;
    private ObjectAnimator mSoundLoadAnim;
    public static long mUpdateDownloadId = Integer.MIN_VALUE;
    private MapMarkContainer mContainer;
    private ExplosionField mExplosionField;
    private boolean mIsSignPress = false;
    private boolean debug = false;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_map;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        AppSetting.isRunning = true;
        mIsFirstIn = true;
        Intent intent = getIntent();
        if(intent != null){
            mSchema = intent.getStringExtra("schema");
        }
        DaggerMapComponent.builder()
                .mapModule(new MapModule(this))
                .netComponent(MoeMoeApplicationLike.getInstance().getNetComponent())
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
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(mReceiver, filter);
        if(NetworkUtils.isNetworkAvailable(this) && NetworkUtils.isWifi(this)){
            mPresenter.checkVersion();
        }
        if(NetworkUtils.isNetworkAvailable(this)){
            mPresenter.checkBuild(PreferenceUtils.getBuildVersion(this),AppSetting.VERSION_CODE);
        }
        if(debug){
            mIp.setVisibility(View.VISIBLE);
            mIp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final EditText text = new EditText(MapActivity.this);
                    new AlertDialog.Builder(MapActivity.this).setTitle("输入IP")
                            .setView(text)
                            .setPositiveButton(R.string.label_confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String coinStr = text.getText().toString();
                                    String res = "http://" + coinStr + "/";
                                    PreferenceUtils.setIp(MapActivity.this,res);
                                    dialogInterface.dismiss();
                                    showToast("设置完毕，请重启应用");
                                }
                            })
                            .setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .show();
                }
            });
        }
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
        }else {
            mPresenter.addNightEventMapMark(this,mapWidget,scale);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsFirstIn = false;
        hideBtn();
    }

    private void clearMap(){
        if(mapWidget != null){
            mapWidget.clearLayers();
            mapWidget = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        showBtn();
        if(StringUtils.isyoru()){
            if(mMapState != MAP_SCHOOL_YORU){
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
            if(!StringUtils.isDayEvent()){
                layer.setVisible(false);
            }else {
                layer.setVisible(true);
            }
            mapWidget.invalidate();
        }
        if(!AppSetting.isEnterEventToday && StringUtils.isKillEvent() ){
            if(mMapState != MAP_SCHOOL_KILL){
                clearMap();
                mMapState = MAP_SCHOOL_KILL;
                initMap("map_kill_event");
                initMapListeners();
            }
        }
        showSnowman();
        if(PreferenceUtils.getMessageDot(this,"neta") || PreferenceUtils.getMessageDot(this,"system")){
            mCardDot.setVisibility(View.VISIBLE);
        }else {
            mCardDot.setVisibility(View.GONE);
        }
    }

    private void showSnowman(){
        long lastTime = PreferenceUtils.getLastSnowTime(this);
        Calendar today = Calendar.getInstance();
        Calendar last = Calendar.getInstance();
        last.setTimeInMillis(lastTime);
        int year = today.get(Calendar.YEAR);
        int mon = today.get(Calendar.MONTH) + 1;
        int day = today.get(Calendar.DAY_OF_MONTH);
        if ((year == 2016 && mon == 12 && day >= 24) || (year == 2017 && mon == 1 && day <=2)){
            if (today.get(Calendar.YEAR) == last.get(Calendar.YEAR) && today.get(Calendar.MONTH) == last.get(Calendar.MONTH)
                    && today.get(Calendar.DAY_OF_MONTH) == last.get(Calendar.DAY_OF_MONTH) && today.get(Calendar.HOUR_OF_DAY) > last.get(Calendar.HOUR_OF_DAY)) {
                //同一天
                SnowShowEntity.clearCache();
                mPresenter.addSnowman(this,mapWidget);
                PreferenceUtils.setLastSnowTime(this,today.getTimeInMillis());
            }else if(today.get(Calendar.YEAR) > last.get(Calendar.YEAR) || today.get(Calendar.MONTH) > last.get(Calendar.MONTH)
                    || today.get(Calendar.DAY_OF_MONTH) > last.get(Calendar.DAY_OF_MONTH)){
                SnowShowEntity.clearCache();
                mPresenter.addSnowman(this,mapWidget);
                PreferenceUtils.setLastSnowTime(this,today.getTimeInMillis());
            }else if(today.get(Calendar.YEAR) == last.get(Calendar.YEAR) && today.get(Calendar.MONTH) == last.get(Calendar.MONTH)
                    && today.get(Calendar.DAY_OF_MONTH) == last.get(Calendar.DAY_OF_MONTH) && today.get(Calendar.HOUR_OF_DAY) == last.get(Calendar.HOUR_OF_DAY)){
                MapImgLayer layer = (MapImgLayer) mapWidget.getLayerById(233);
                if(layer == null) mPresenter.addCacheSnowman(this,mapWidget);
            }
        }
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
        final int version = s.getVersion();
        String path = s.getPath();
        if(!TextUtils.isEmpty(path)){
            RxDownload downloadSub = RxDownload.getInstance()
                    .maxThread(3)
                    .maxRetryCount(3)
                    .defaultSavePath(StorageUtils.getTempRootPath())
                    .retrofit(MoeMoeApplicationLike.getInstance().getNetComponent().getRetrofit());
            if(!path.contains("http://") && !path.contains("https://")){
                path = ApiService.URL_QINIU + path;
            }
            final String temp = System.currentTimeMillis() + "_path_"+ version + ".neta";
            downloadSub.download(path,temp,null)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<DownloadStatus>() {
                        @Override
                        public void onCompleted() {
                            showToast("appBUG已修复，稍后会自动退出，请重新进入app");
                            TinkerInstaller.onReceiveUpgradePatch(getApplicationContext(),StorageUtils.getTempRootPath() + temp);

                        }

                        @Override
                        public void onError(Throwable e) {
                        }

                        @Override
                        public void onNext(DownloadStatus downloadStatus) {

                        }
                    });
        }
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
        if(entity != null){
            mIsSign = entity.isCheckState();
            signDay = entity.getDay();
        }else {
            mIsSign = false;
            signDay = 0;
        }
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

    @OnClick({R.id.iv_bag,R.id.iv_cal,R.id.iv_card,R.id.iv_live2d,R.id.iv_square,R.id.tv_exit_live2d,R.id.iv_select_deskmate,R.id.iv_select_fuku,R.id.iv_select_language,R.id.iv_sign})
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
        set.play(bagAnimator).with(squareAnimator);
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
        set.play(bagAnimator).with(calAnimator);
        set.play(calAnimator).with(squareAnimator);
        set.play(squareAnimator).with(galAnimator);
        set.play(galAnimator).with(signAnimator);
        set.start();
    }

    private void showBtn(){
        mCardRoot.setVisibility(View.VISIBLE);
        mIvBag.setVisibility(View.VISIBLE);
        mIvCal.setVisibility(View.VISIBLE);
        mIvSquare.setVisibility(View.VISIBLE);
        mIvGal.setVisibility(View.VISIBLE);
        mIvSign.setVisibility(View.VISIBLE);
    }

    private void hideBtn(){
        mCardRoot.setVisibility(View.INVISIBLE);
        mIvBag.setVisibility(View.INVISIBLE);
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
        super.onDestroy();
        SnowShowEntity.onDestroy(this);
        unregisterReceiver(mReceiver);
        AppSetting.isRunning = false;
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
}