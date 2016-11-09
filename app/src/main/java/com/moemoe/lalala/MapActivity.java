package com.moemoe.lalala;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.annotation.ContentView;
import com.app.annotation.FindView;
import com.app.common.util.DensityUtil;
import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.data.AppUpdateInfo;
import com.moemoe.lalala.galgame.FileManager;
import com.moemoe.lalala.galgame.Live2DManager;
import com.moemoe.lalala.galgame.Live2DView;
import com.moemoe.lalala.galgame.SoundManager;
import com.moemoe.lalala.netamusic.player.PlaybackService;
import com.moemoe.lalala.network.CallbackFactory;
import com.moemoe.lalala.network.OnNetWorkCallback;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.PreferenceManager;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ToastUtil;
import com.moemoe.lalala.view.dialog.SignDialog;
import com.moemoe.lalala.view.map.MapLayout;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Haru on 2016/7/20 0020.
 */
@ContentView(R.layout.ac_map)
public class MapActivity extends BaseActivity implements View.OnClickListener{

    public static final int REQ_SELECT_FUKU = 5555;
    private static final int MAP_SCHOOL = 0;
    private static final int MAP_SCHOOL_YORU = 1;
    private static final int MAP_SCHOOL_KILL = 2;

    public static boolean sChangeLogin = false;
    @FindView(R.id.map)
    private MapLayout mMap;
    @FindView(R.id.iv_bag)
    private ImageView mIvBag;
    @FindView(R.id.iv_cal)
    private ImageView mIvCal;
    @FindView(R.id.iv_card)
    private ImageView mIvCard;
    @FindView(R.id.iv_live2d)
    private ImageView mIvGal;
    @FindView(R.id.iv_msg)
    private ImageView mIvMsg;
    @FindView(R.id.iv_square)
    private ImageView mIvSquare;
    @FindView(R.id.live2DLayout)
    private FrameLayout mLive2DLayout;
    @FindView(R.id.tv_exit_live2d)
    private TextView mExitLive2D;
    @FindView(R.id.iv_select_deskmate)
    private ImageView mIvSelectMate;
    @FindView(R.id.iv_select_language)
    private ImageView mIvSelectLanguage;
    @FindView(R.id.iv_select_fuku)
    private ImageView mIvSelectFuku;
    @FindView(R.id.iv_sound_load)
    private ImageView mIvSoundLoad;
    @FindView(R.id.iv_sign)
    private ImageView mIvSign;
    @FindView(R.id.map_root)
    private ViewGroup mRoot;

    private String mFuku;
    private Live2DManager live2DMgr ;
    static private Activity instance;

    private String mSchema;
    private long mLastBackTime = 0;
    //public static MusicServiceManager sMusicServiceManager = null;
    private boolean mIsOut = false;
    private boolean mIsPressed = false;
    private boolean mIsChanged = false;
    private boolean mIsFirstIn = false;
    private ObjectAnimator mSoundLoadAnim;
    private boolean mIsSign = false;
    private int signDay;
    private int mMapState = MAP_SCHOOL;
    private boolean mIsServiceBound;
    private PlaybackService mPlaybackService;
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mPlaybackService = ((PlaybackService.LocalBinder) service).getService();
        }

        public void onServiceDisconnected(ComponentName className) {
            mPlaybackService = null;
        }
    };

    private void bindPlaybackService(){
        bindService(new Intent(this,PlaybackService.class),mConnection,BIND_AUTO_CREATE);
        mIsServiceBound = true;
    }

    private void unbindPlaybackService(){
        if(mIsServiceBound){
            unbindService(mConnection);
            mIsServiceBound = false;
        }
    }

    @Override
    protected void initView() {
        mIsFirstIn = true;
        AppSetting.isRunning = true;
        if(mIntent != null){
            mSchema = mIntent.getStringExtra("schema");
        }
        if(mPreferMng == null){
            mPreferMng = PreferenceManager.getInstance(this);
        }
        initMap();
        mMap.setMapResource(R.drawable.map_school);
        SoundManager.init(this);
        FileManager.init(this);
        mFuku = mPreferMng.getSelectFuku();
        live2DMgr = new Live2DManager(mFuku);
        instance = this;
        Live2DView view = live2DMgr.createView(this) ;
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
        mLive2DLayout.addView(view, 0, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mIvBag.setOnClickListener(this);
        mIvCal.setOnClickListener(this);
        mIvCard.setOnClickListener(this);
        mIvGal.setOnClickListener(this);
        mIvMsg.setOnClickListener(this);
        mIvSign.setOnClickListener(this);
        mIvSquare.setOnClickListener(this);
        mExitLive2D.setOnClickListener(this);
        mIvSelectMate.setOnClickListener(this);
        mIvSelectFuku.setOnClickListener(this);
        mIvSelectLanguage.setOnClickListener(this);
        bindPlaybackService();
        checkVersion();
        checkSignState();
        if(!TextUtils.isEmpty(mSchema)){
            IntentUtils.toActivityFromUri(this, Uri.parse(mSchema),null);
        }
    }

    private void soundLoading(){
        mSoundLoadAnim = ObjectAnimator.ofFloat(mIvSoundLoad,"alpha",0.2f,1f).setDuration(300);
        mSoundLoadAnim.setInterpolator(new LinearInterpolator());
        mSoundLoadAnim.setRepeatMode(ValueAnimator.REVERSE);
        mSoundLoadAnim.setRepeatCount(ValueAnimator.INFINITE);
        mSoundLoadAnim.start();
    }

    static public void exit() {
        SoundManager.release();
        instance.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsFirstIn = false;
       // imgOut();
        hideBtn();
        mMap.removeAllMarkView(false);
    }

    public ViewGroup getRoot(){return mRoot;}

    public MapLayout getMap(){
        return mMap;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //imgIn();
        showBtn();
        if(StringUtils.isyoru()){
            if(mMapState != MAP_SCHOOL_YORU){
                mMap.setMapResource(R.drawable.map_school_yoru);//夜晚
                mMapState = MAP_SCHOOL_YORU;
                //mIsChanged = true;
            }
        }else {
            if(mMapState != MAP_SCHOOL){
                mMap.setMapResource(R.drawable.map_school);
                mMapState = MAP_SCHOOL;
                //mIsChanged = false;
            }
        }
        if(!AppSetting.isEnterEventToday && StringUtils.isKillEvent()){
            mMap.setMapResource(R.drawable.map_school_kill_event);//kill
            mMapState = MAP_SCHOOL_KILL;
        }
        if(!mIsFirstIn){
            mMap.rebuildMarks();
        }
        if(sChangeLogin){
            checkSignState();
            sChangeLogin = false;
        }

    }

    public void setMapState(int i){
        mMapState = i;
    }

    public void initMap(){
        mMap.setOnImageClickLietener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsOut) {
                    imgIn();
                    mIsOut = false;
                } else {
                    imgOut();
                    mIsOut = true;
                }
            }
        });
        mMap.addMapMarkView(R.drawable.btn_map_baojian, 0.26f, 0.30f, "neta://com.moemoe.lalala/department_1.0?uuid=10f8433e-5f80-11e6-b42a-d0a637eac7d7&name=保健室", null,"00:00","24:00","00:00","24:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_xiaomaibu, 0.30f, 0.54f, "neta://com.moemoe.lalala/department_1.0?uuid=1dd2dba6-5f80-11e6-ad3c-d0a637eac7d7&name=小卖部", null,"00:00","24:00","00:00","24:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_girls, 0.53f, 0.44f, "neta://com.moemoe.lalala/department_1.0?uuid=cfda0aa2-5f7f-11e6-b844-d0a637eac7d7&name=女性部", null,"00:00","24:00","00:00","24:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_news, 0.57f, 0.20f, "neta://com.moemoe.lalala/department_1.0?uuid=26f9831a-5f7f-11e6-8f94-d0a637eac7d7&name=新闻部", null,"00:00","24:00","00:00","24:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_sport, 0.75f, 0.25f, "neta://com.moemoe.lalala/department_1.0?uuid=393341d4-5f7f-11e6-a5af-d0a637eac7d7&name=体育馆", null, "00:00","24:00","00:00","24:00",null);
        mMap.addMapMarkView(R.drawable.btn_map_lib, 0.82f, 0.42f, "neta://com.moemoe.lalala/department_1.0?uuid=97e18352-5f7f-11e6-ae04-d0a637eac7d7&name=图书馆", null,"00:00","24:00","00:00","24:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_video, 0.83f, 0.66f, "neta://com.moemoe.lalala/department_1.0?uuid=a77f9006-5f7f-11e6-ae2c-d0a637eac7d7&name=影音部",null, "00:00","24:00","00:00","24:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_picture, 0.70f, 0.64f, "neta://com.moemoe.lalala/department_1.0?uuid=be39718c-5f7f-11e6-81f9-d0a637eac7d7&name=美图部",null, "00:00","24:00","00:00","24:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_game, 0.57f, 0.78f, "neta://com.moemoe.lalala/department_1.0?uuid=e255f9d4-5f7f-11e6-8a65-d0a637eac7d7&name=游戏部",null, "00:00","24:00","00:00","24:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_qiu, 0.91f, 0.89f, "neta://com.moemoe.lalala/qiu_1.0",null, "00:00","24:00","00:00","24:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_trash, 0.63f, 0.39f, "neta://com.moemoe.lalala/garbage_1.0",null, "00:00","24:00","00:00","24:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_trash, 0.33f, 0.43f, "neta://com.moemoe.lalala/garbage_img_1.0",null, "00:00","24:00","00:00","24:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_donation, 0.55f, 0.95f,"neta://com.moemoe.lalala/donation_1.0",null, "00:00","24:00","00:00","24:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_gacha, 0.19f, 0.64f, "neta://com.moemoe.lalala/url_inner_1.0?http://prize.moemoe.la:8000/prize/index/",getString(R.string.label_lotter), "00:00","24:00","00:00","24:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_trash_normal, 0.05f, 0.42f, "neta://com.moemoe.lalala/calui_1.0?uuid=6f90946e-7500-11e6-ba28-e0576405f084&name=anitoys",null, "00:00","24:00","00:00","24:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_trash_normal, 0.16f, 0.56f, "neta://com.moemoe.lalala/calui_1.0?uuid=8b504a9a-7500-11e6-9642-e0576405f084&name=ALTER",null,"00:00","24:00","00:00","24:00",  null);
        mMap.addMapMarkView(R.drawable.btn_map_trash_normal, 0.27f, 0.60f, "neta://com.moemoe.lalala/calui_1.0?uuid=9c183758-7500-11e6-b599-e0576405f084&name=animate",null,"00:00","24:00","00:00","24:00",  null);
        mMap.addMapMarkView(R.drawable.btn_map_trash_normal, 0.26f, 0.66f, "neta://com.moemoe.lalala/calui_1.0?uuid=a727b466-7500-11e6-a06a-e0576405f084&name=天闻角川",null, "00:00","24:00","00:00","24:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_trash_normal, 0.32f, 0.70f, "neta://com.moemoe.lalala/calui_1.0?uuid=b422d3fe-7500-11e6-955c-e0576405f084&name=轻漫画",null, "00:00","24:00","00:00","24:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_trash_normal, 0.37f, 0.68f, "neta://com.moemoe.lalala/calui_1.0?uuid=be7a3728-7500-11e6-bdd7-e0576405f084&name=玛莎多拉",null, "00:00","24:00","00:00","24:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_trash_normal, 0.32f, 0.76f, "neta://com.moemoe.lalala/calui_1.0?uuid=c7e1acf8-7500-11e6-a813-e0576405f084&name=口水三国",null, "00:00","24:00","00:00","24:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_trash_normal, 0.09f, 0.56f, "neta://com.moemoe.lalala/calui_1.0?uuid=904a0f34-75b0-11e6-af78-e0576405f084&name=艾漫",null,"00:00","24:00","00:00","24:00",  null);
        mMap.addMapMarkView(R.drawable.btn_map_trash_normal, 0.52f, 0.82f, "neta://com.moemoe.lalala/tag_1.0?9992a75a-ff6a-4dbd-88b5-07ba1d55897c",null, "00:00","24:00","00:00","24:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_trash_normal, 0.63f, 0.85f, "neta://com.moemoe.lalala/tag_1.0?e182cc94-f681-4098-844b-7c7d548c719e",null, "00:00","24:00","00:00","24:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_trash_normal, 0.68f, 0.94f, "neta://com.moemoe.lalala/tag_1.0?a7557872-c1ad-40fc-bba0-a9f517215e8c",null, "00:00","24:00","00:00","24:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_trash_normal, 0.33f, 0.82f, "neta://com.moemoe.lalala/doc_1.0?3a61f262-75b3-11e6-a766-e0576405f084",null, "00:00","24:00", "00:00","24:00",null);

        //-------白天
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.36f, 0.03f,null,"“他们是樱木军团：水户洋平，等等——”\n" + "“谁特么的是等等！！”","06:00","18:00","06:00","18:00",null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.40f, 0.04f,null,"“日本著名中锋赤木刚宪定下的目标是——”\n" + "“得分！篮板！死库水！”","06:00","18:00","06:00","18:00",  null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.30f, 0.08f,null,"“好好练习的话，哥哥也是有可能去甲子园的！”\n" + "“挤挤电车的话，我随时都可以去。”","06:00","18:00","06:00","18:00",  null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.31f, 0.15f,null,"“隔壁薯片半价啦！！”\n" + "“所以不穿女装就不能买吗你们这些混蛋！！！！”", "06:00","18:00","06:00","18:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.11f, 0.29f,null,"“为什么补考三次还不让我通过？”\n" + "“这一切都是命运石之门的选择。”", "06:00","18:00","06:00","18:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.61f, 0.31f,null,"“和我签订契约成为马猴烧酒吧！”\n" + "“学姐：……”", "06:00","18:00","06:00","18:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.76f, 0.37f,null,"“不能逃避、不能逃避、不能逃避！”\n" + "“碇真嗣，你这个笨蛋！”","06:00","18:00","06:00","18:00",  null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.88f, 0.37f,null,"“坂本ですが？”", "06:00","18:00","06:00","18:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.90f, 0.27f,null,"“海贼王的男人和火影的男人——擦身而过了。”", "06:00","18:00","06:00","18:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.95f, 0.54f,null,"“今天的风儿…好喧嚣啊…”\n" + "“嗯，让一下。”", "06:00","18:00","06:00","18:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.71f, 0.55f,null,"“吾名为复仇天使暗猫，否定一切非二次元事物。诅咒面基、诅咒情侣、诅咒这世上的一切，给所有现充们施以破坏的铁锤吧，此乃吾生最大诅咒，噢不，最大邀请函！切身体会吧！”\n" + "“……别、别这样……我加入就是了……”", "06:00","18:00","06:00","18:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.86f, 0.74f,null,"“喔哈哟！Ritsu~紬前辈！”\n" + "“喔哈哟！Mio~梓喵！”\n" + "（心疼呆唯一秒。）", "06:00","18:00","06:00","18:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.03f, 0.50f,null,"“Leader，不让开的话就连你的【哔—】一起炸掉！”\n" + "“不要想要【哔—】的话就试试啊你这个抖s八嘎！”", "06:00","18:00","06:00","18:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.05f, 0.95f,null,"“如果见死不救的话，那还不如一起死了算了。”\n" + "“这就是桐人考试不及格还怪我不给桐人抄的理由？”", "06:00","18:00","06:00","18:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.44f, 0.50f,null,"“你可是比蛋糕还美味千百倍的猎物！”\n" + "“（真是糟糕的台词）行行行…我不跟你抢。”", "06:00","18:00","06:00","18:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.43f, 0.32f,null,"“身为老师，最欣慰的莫过于，自己在犹豫中给予教诲，学生们却得到了明确的答案。”\n" + "……\n" + "……\n" + "……","06:00","18:00","06:00","18:00",  null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.93f, 0.72f,null,"“SOS团招募外星人、未来人、超能力者！！”\n" + "“团、团长……┬＿┬”", "06:00","18:00","06:00","18:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.20f,0.79f,null,"“人们想得到什么，应该先思考能够付出什么！”\n" + "（什么嘛，原来你不会炼手办啊…）", "06:00","18:00","06:00","18:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.19f, 0.70f,null,"“被漆黑烈焰吞噬殆尽吧！”\n" + "“就是因为不想再被人知道是中二病才转学来这里的你就忘了吗！”", "06:00","18:00","06:00","18:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.34f, 0.87f,null,"“多么强壮的躯体…这哪位英灵的雕塑？”\n" + "“不清楚，可我能感受到他散发出的强大哲学气息。”", "06:00","18:00","06:00","18:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.41f, 0.76f,null,"“喔~内~撒~嘛~咦嘻嘻嘻！”\n" + "“好像被奇怪的家伙盯上了…是错觉吗？”", "06:00","18:00","06:00","18:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.61f, 0.59f,null,"“绘画技巧完全没有进步…”\n" + "“全力以赴就好，比起绘制这个学园的家伙，小千代已经可以称得上杰出了。”", "06:00","18:00","06:00","18:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.67f, 0.36f,null,"“艾斯，我一定会继承你的意志！”", "06:00","18:00","06:00","18:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.66f, 0.51f,null,"“新来的转校生似乎被安排在莲的同桌。”\n" +
                "“喔喔喔喔！！真好命啊！！居然能和那样的美少女朝夕相处！”\n" +
                "“算了吧，你又不是不知道那个家伙…”", "06:00","18:00","06:00","18:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.80f, 0.56f,null,"“听说了吗？传得沸沸扬扬，午夜十二点的学院……”\n" +
                "“噫！快别说了！”", "06:00","18:00","06:00","18:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.98f, 0.61f,null,"“最近啊，校长总是在夜里鬼鬼祟祟的巡视，肯定是想做什么肮脏的事情！”\n" +
                "“不是吧！校长不像是那样的人！”\n" +
                "“那么校长室为什么要装密码锁呢？”", "06:00","18:00","06:00","18:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.47f, 0.97f,null,"“布洛妮娅，昨晚伤的重不重？不要硬撑！”\n" +
                "“布洛妮娅不痛。”\n" +
                "“唉，没想到崩坏已经蔓延到Neta学园了。不知道这次我们还能不能全身而退。”\n" +
                "“布洛妮娅有信心！”", "06:00","18:00","06:00","18:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.43f, 0.80f,null,"“崩..崩坏？怎...怎么可能嘛！只是一些小杂鱼闹事，没什么大不了的~大家不要担心，我们女武神部队会保护好Neta学园的！”", "06:00","18:00","06:00","18:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.76f, 0.94f,null,"“&！&#%@￥#！&(*&^/(ㄒoㄒ)/~~（酷仔！你怎么了酷仔！）”\n" +
                "“&……%%>_<%(我，我好像快不行了...)”\n" +
                "“*&&……&%……%￥￥！（说好今晚要拼刺刀的呢！你不能在这里倒下啊！）”", "06:00","18:00","06:00","18:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.68f, 0.23f,null,"“Genji，我警告你管好自己的嘴！”\n" +
                "“花村的游戏厅已经被拆干净了，我怎么可能再放任你对这里为所欲为！”", "06:00","18:00","06:00","18:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.69f, 0.36f,null,"“…………”\n" +
                "“这里的人好像都会日语，被听出hasaki是我胡乱喊的话……那就很尴尬了……”", "06:00","18:00","06:00","18:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.20f, 0.26f,null,"嗯？你问我有没有重伤患者来过…没有…最近只是处理了一些同学的感冒和擦伤而已。等等，是谁碰上事件了吗？严不严重？", "06:00","18:00","06:00","18:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.36f, 0.91f,null,"咦，关于校长室的事情吗？我知道哟！校长啊……是个兴趣很广泛的人呐，校长室里一定有你要的东西！诶？不是要找签名棒球吗？", "06:00","18:00","06:00","18:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.72f, 0.40f,null,"校长室吗？在主楼的四层，太高了我不想爬。", "06:00","18:00","06:00","18:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.57f, 0.13f,null,"从记者的口中问话，同学你要拿什么珍贵的信息来交换呢？舍得无偿分享，大概就只有枳实那个认真的傻瓜了吧。", "06:00","18:00","06:00","18:00", null);


        //----------------晚上
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.12f, 0.83f,null,"“那边的，从【西风狂诗曲】过来的吗？”\n" +
                "“呵，阁下呢，【鬼泣】吗？似乎又不太像…”\n" +
                "（同名又同族的两个人，为何要在自己的节日里互相伤害……）", "00:00","06:00","18:00","24:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.19f, 0.97f,null,"“你要反复从那边的【哔】女仆手里拿多少廉价糖果？一定是在想姐妹花【哔】什么的吧你这个肮脏的【哔】！”\n" +
                "“才怪呢我只想和蓝头发的【哔】【哔】！红头发的无论怎么看都跟你是一类的【哔】拜托饶我了好吗？”", "00:00","06:00","18:00","24:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.34f, 0.97f,null,"“今天是节日吗？”\n" +
                "“嗯，万圣节。”\n" +
                "“那么离约定的时间近了吗？”\n" +
                "“这次…真的近了！”", "00:00","06:00","18:00","24:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.47f, 0.90f,null,"“今天腿抬的很低，一定是穿了不同的胖次，咦嘻嘻嘻，喔内撒嘛！喔内撒嘛！！！”", "00:00","06:00","18:00","24:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.41f, 0.71f,null,"“御坂美琴：喝……呀……”\n" +
                "“贩卖机：白色。”\n" +
                "“御坂美琴：请、请、请给我乌龙茶，谢、谢谢！”\n" +
                "“贩卖机：五元。”", "00:00","06:00","18:00","24:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.25f, 0.74f,null,"“雷姆雷姆，姐姐已经快派完所有的万圣节糖果了。”\n" +
                "“姐姐姐姐，雷姆觉得那个学长实在拿不了更多了。”", "00:00","06:00","18:00","24:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.12f, 0.64f,null,"“时间比任何事物都要温柔，也比任何事物都残酷——嬉笑吧，什么也感知不到的可怜人。”", "00:00","06:00","18:00","24:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.04f, 0.55f,null,"【火影的男人】和【海贼王的男人】终于相遇了，居然意外地合得来……\n" +
                "“两位同学？喔，拉面店是吗，在那边！”", "00:00","06:00","18:00","24:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.10f, 0.30f,null,"“这里那里都是情侣！本熊明天就把这个现充爆炸的学园炸掉Kuma！”", "00:00","06:00","18:00","24:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.21f, 0.41f,null,"“四处洋溢着【彩色的美梦】，是让人不忍夺走的至宝。”\n" +
                "（少爷还在对【纯黑的噩梦】没有戏份的事耿耿于怀吗？）", "00:00","06:00","18:00","24:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.33f, 0.50f,null,"“如果你还活着多好…”\n" +
                "“笨蛋，能够相见已然是比One piece还要珍贵的财宝了。”", "00:00","06:00","18:00","24:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.56f, 0.64f,null,"“龙儿，今晚会有真龙出现吗？”\n" +
                "“在这里……也许有的吧。”\n" +
                "“等它出现的时候，记得抱紧我，还有，捂住我的眼睛。”\n" +
                "“……傻瓜！”", "00:00","06:00","18:00","24:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.90f, 0.76f,null,"“小忍那家伙到底跑哪去了！”\n" +
                "“我刚刚还在这看到她的，万圣节快到了晚上真是热闹啊！”\n" +
                "“万圣节可能会吸引到别的吸血鬼生物，小忍那家伙别惹什么乱子啊。”", "00:00","06:00","18:00","24:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.71f, 0.55f,null,"“咳咳咳，复、复仇天使暗猫可不怕你这种低级幽灵！”\n" +
                " “ 盯——”\n" +
                "“呜啊，京介，救我！！！”", "00:00","06:00","18:00","24:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.96f, 0.29f,null,"“咦，今天是什么日子？好热闹的样子，我也要来玩！”", "00:00","06:00","18:00","24:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.68f, 0.23f,null,"“岛田，我说你在这里盯了我一个多月了，你到底想怎么样？”\n" +
                "“少废话莱耶斯，你拆游戏厅上瘾了吗？”", "00:00","06:00","18:00","24:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.69f, 0.36f,null,"“为了亲眼见识一下龙之剑，我已经抬头看了一个月多了，这两个人也不蹦跶一下，根本没机会接R上屋顶。”", "00:00","06:00","18:00","24:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.43f, 0.32f,null,"“这里的月亮是完整的，说明了什么呢？”", "00:00","06:00","18:00","24:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.58f, 0.31f,null,"“是夜万圣，次元洞开。”\n" +
                "“阿夏，这个城里人在说什么？还有，“大凶”是在嘲笑我吗……”\n" +
                "“早让你不要来城里读书了。”", "00:00","06:00","18:00","24:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.43f, 0.07f,null,"“598年的时光，就沉淀出这种可笑的品味吗？脱下你的帽子，然后离开本小姐的天台。”\n" +
                "“495岁的扮成Loli也真是有脸，现在求饶的话，我可以少摘下几个你的宝贝玻璃挂坠！”\n" +
                "（这里也是，那里也是，吸血鬼之间就不能好好相处吗？）", "00:00","06:00","18:00","24:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.23f, 0.09f,null,"“喔喔喔！是召唤邪王真眼的魔法阵！”\n" +
                "“又是两个奇怪的人…”\n" +
                "“勇太勇太，你感受到强烈的次元波动了吗！！”\n" +
                "“哈？没……喔，有，有了！Dark Flame Master体内暗炎龙有复苏的迹象！！”", "00:00","06:00","18:00","24:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.25f, 0.12f,null,"“啊…有人在认真的看着啊，能不画了吗？”\n" +
                "“那两位，肯定是被召唤而来超能力者啊！！”\n" +
                "“不是单纯的中二病吗？”\n" +
                "“少废话，召唤阵一定开始起作用了，赶紧完成它！！”", "00:00","06:00","18:00","24:00", null);

        //-----事件
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.48f, 0.19f,"neta://com.moemoe.lalala/event_1.0",null, "00:00","24:00","00:00","24:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_gacha, 0.38f, 0.31f, "neta://com.moemoe.lalala/url_inner_1.0?http://prize.moemoe.la:8000/netaopera/chap1/?pass=" + mPreferMng.getPassEvent() + "&user_id=" + mPreferMng.getUUid(),null, "00:00","24:00","00:00","24:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_gacha, 0.93f, 0.46f, "neta://com.moemoe.lalala/url_inner_1.0?http://prize.moemoe.la:8000/netaopera/chap2/?pass=" + mPreferMng.getPassEvent() + "&user_id=" + mPreferMng.getUUid(),null, "00:00","24:00","00:00","24:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_gacha, 0.48f, 0.33f, "neta://com.moemoe.lalala/url_inner_1.0?http://prize.moemoe.la:8000/netaopera/chap3/?pass=" + mPreferMng.getPassEvent() + "&user_id=" + mPreferMng.getUUid(),null, "00:00","24:00","00:00","24:00", null);
        mMap.addMapMarkView(R.drawable.btn_map_gacha, 0.18f, 0.32f, "neta://com.moemoe.lalala/url_inner_1.0?http://prize.moemoe.la:8000/netaopera/chap5/?pass=" + mPreferMng.getPassEvent() + "&user_id=" + mPreferMng.getUUid(),null, "00:00","24:00","00:00","24:00", null);
    }

    private void hideBtn(){
        mIvCard.setVisibility(View.INVISIBLE);
        mIvBag.setVisibility(View.INVISIBLE);
        mIvMsg.setVisibility(View.INVISIBLE);
        mIvCal.setVisibility(View.INVISIBLE);
        mIvSquare.setVisibility(View.INVISIBLE);
        mIvGal.setVisibility(View.INVISIBLE);
        mIvSign.setVisibility(View.INVISIBLE);
    }

    private void imgOut(){
        ObjectAnimator cardAnimator = ObjectAnimator.ofFloat(mIvCard,"translationY",0,-mIvCard.getHeight()- DensityUtil.dip2px(14)).setDuration(300);
        cardAnimator.setInterpolator(new OvershootInterpolator());
        ObjectAnimator bagAnimator = ObjectAnimator.ofFloat(mIvBag,"translationY",0,-mIvBag.getHeight()- DensityUtil.dip2px(14)).setDuration(300);
        bagAnimator.setInterpolator(new OvershootInterpolator());
        ObjectAnimator msgAnimator = ObjectAnimator.ofFloat(mIvMsg,"translationY",0,-mIvMsg.getHeight()- DensityUtil.dip2px(14)).setDuration(300);
        msgAnimator.setInterpolator(new OvershootInterpolator());
        ObjectAnimator calAnimator = ObjectAnimator.ofFloat(mIvCal,"translationY",0,mIvCal.getHeight()+DensityUtil.dip2px(5)).setDuration(300);
        calAnimator.setInterpolator(new OvershootInterpolator());
        ObjectAnimator squareAnimator = ObjectAnimator.ofFloat(mIvSquare,"translationY",0,mIvSquare.getHeight()+DensityUtil.dip2px(5)).setDuration(300);
        squareAnimator.setInterpolator(new OvershootInterpolator());
        ObjectAnimator galAnimator = ObjectAnimator.ofFloat(mIvGal,"translationY",0,mIvGal.getHeight()).setDuration(300);
        galAnimator.setInterpolator(new OvershootInterpolator());
        ObjectAnimator signAnimator = ObjectAnimator.ofFloat(mIvSign,"translationX",0,mIvSign.getWidth() + DensityUtil.dip2px(14)).setDuration(300);
        signAnimator.setInterpolator(new OvershootInterpolator());
        AnimatorSet set = new AnimatorSet();
        set.play(cardAnimator).with(bagAnimator);
        set.play(bagAnimator).with(msgAnimator);
        set.play(msgAnimator).with(calAnimator);
        set.play(calAnimator).with(squareAnimator);
        set.play(squareAnimator).with(galAnimator);
        set.play(galAnimator).with(signAnimator);
        set.start();
    }

    private void showBtn(){
        mIvCard.setVisibility(View.VISIBLE);
        mIvBag.setVisibility(View.VISIBLE);
        mIvMsg.setVisibility(View.VISIBLE);
        mIvCal.setVisibility(View.VISIBLE);
        mIvSquare.setVisibility(View.VISIBLE);
        mIvGal.setVisibility(View.VISIBLE);
        mIvSign.setVisibility(View.VISIBLE);
    }

    private void imgIn(){
        ObjectAnimator cardAnimator = ObjectAnimator.ofFloat(mIvCard,"translationY",-mIvCard.getHeight()- DensityUtil.dip2px(14),0).setDuration(300);
        cardAnimator.setInterpolator(new OvershootInterpolator());
        ObjectAnimator bagAnimator = ObjectAnimator.ofFloat(mIvBag,"translationY",-mIvBag.getHeight()- DensityUtil.dip2px(14),0).setDuration(300);
        bagAnimator.setInterpolator(new OvershootInterpolator());
        ObjectAnimator msgAnimator = ObjectAnimator.ofFloat(mIvMsg,"translationY",-mIvMsg.getHeight()- DensityUtil.dip2px(14),0).setDuration(300);
        msgAnimator.setInterpolator(new OvershootInterpolator());
        ObjectAnimator calAnimator = ObjectAnimator.ofFloat(mIvCal,"translationY",mIvCal.getHeight()+DensityUtil.dip2px(5),0).setDuration(300);
        calAnimator.setInterpolator(new OvershootInterpolator());
        ObjectAnimator squareAnimator = ObjectAnimator.ofFloat(mIvSquare,"translationY",mIvSquare.getHeight()+DensityUtil.dip2px(5),0).setDuration(300);
        squareAnimator.setInterpolator(new OvershootInterpolator());
        ObjectAnimator galAnimator = ObjectAnimator.ofFloat(mIvGal,"translationY",mIvGal.getHeight(),0).setDuration(300);
        galAnimator.setInterpolator(new OvershootInterpolator());
        ObjectAnimator signAnimator = ObjectAnimator.ofFloat(mIvSign,"translationX",mIvSign.getWidth() + DensityUtil.dip2px(14),0).setDuration(300);
        signAnimator.setInterpolator(new OvershootInterpolator());
        AnimatorSet set = new AnimatorSet();
        set.play(cardAnimator).with(bagAnimator);
        set.play(bagAnimator).with(msgAnimator);
        set.play(msgAnimator).with(squareAnimator);
        set.play(squareAnimator).with(calAnimator);
        set.play(calAnimator).with(galAnimator);
        set.play(galAnimator).with(signAnimator);
        set.start();
    }

    private void checkVersion(){
        if(NetworkUtils.isNetworkAvailable(this) && NetworkUtils.isWifi(this)){
            Otaku.getCommonV2().checkVersion().enqueue(CallbackFactory.getInstance().callback1(new OnNetWorkCallback<String, AppUpdateInfo>() {
                @Override
                public void success(String token, AppUpdateInfo s) {
                    if (s.getUpdateStatus() != 0) {
                        showUpdateDialog(s);
                    }
                }

                @Override
                public void failure(int code,String e) {

                }
            }));
        }
    }

    private void changeSignState(){
        if(mIsSign){
            mIvSign.setImageResource(R.drawable.btn_map_signed);
        }else {
            mIvSign.setImageResource(R.drawable.btn_map_sign);
        }
    }

    private void checkSignState(){
        Otaku.getAccountV2().checkSignToday(mPreferMng.getToken()).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
            @Override
            public void success(String token, String s) {
                mIsSign = true;
                signDay = Integer.valueOf(s);
                changeSignState();
            }

            @Override
            public void failure(String e) {
                if(!TextUtils.isEmpty(e)){
                    try {
                        JSONObject json = new JSONObject(e);
                        signDay = json.optInt("data");
                        mIsSign = false;
                        changeSignState();
                    } catch (JSONException e1) {
                        mIsSign = false;
                        signDay = 0;
                        changeSignState();
                    }
                }else {
                    mIsSign = false;
                    signDay = 0;
                    changeSignState();
                }
            }
        }));
    }

    private void signToday(final SignDialog dialog){
        if(NetworkUtils.checkNetworkAndShowError(this)){
            Otaku.getAccountV2().signToday(mPreferMng.getToken()).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
                @Override
                public void success(String token, String s) {
                    mIsSign = true;
                    signDay = Integer.valueOf(s);
                    changeSignState();
                    dialog.setIsSign(mIsSign)
                            .setSignDay(signDay)
                            .changeSignState();
                    ToastUtil.showCenterToast(MapActivity.this,R.string.label_sign_suc);
                }

                @Override
                public void failure(String e) {
                    if(!TextUtils.isEmpty(e)){
                        try {
                            JSONObject json = new JSONObject(e);
                            int temp = json.optInt("data");
                            if(temp == 0){
                                mIsSign = true;
                                changeSignState();
                                ToastUtil.showCenterToast(MapActivity.this,R.string.label_signed);
                            }else {
                                ToastUtil.showCenterToast(MapActivity.this,R.string.label_sign_fail);
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                            ToastUtil.showCenterToast(MapActivity.this,R.string.label_sign_fail);
                        }
                    }else {
                        ToastUtil.showCenterToast(MapActivity.this,R.string.label_sign_fail);
                    }
                }
            }));
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.iv_bag){
            if(!mIsPressed){
                ToastUtil.showCenterToast(this,R.string.label_can_not_use);
                mIsPressed = true;
            }
        }else if(id == R.id.iv_cal){
            Intent i = new Intent(MapActivity.this,CalendarActivity.class);
            startActivity(i);
        }else if(id == R.id.iv_card){
            if(!PreferenceManager.isLogin(MapActivity.this)){
                goLogin();
            }else {
                Intent i = new Intent(MapActivity.this,PersonalActivity.class);
                startActivity(i);
            }
        }else if(id == R.id.iv_live2d){
            mLive2DLayout.setVisibility(View.VISIBLE);
            mExitLive2D.setVisibility(View.VISIBLE);
            mIvSelectMate.setVisibility(View.VISIBLE);
            mIvSelectFuku.setVisibility(View.VISIBLE);
            mIvSelectLanguage.setVisibility(View.VISIBLE);
            mIvGal.setVisibility(View.GONE);
        }else if(id == R.id.iv_msg){
            if(!PreferenceManager.isLogin(MapActivity.this)){
                goLogin();
            }else {
                Intent i = new Intent(MapActivity.this,MessageActivity.class);
                startActivity(i);
            }
        }else if(id == R.id.iv_square){
            Intent i = new Intent(MapActivity.this,WallBlockActivity.class);
            startActivity(i);
        }else if(id == R.id.tv_exit_live2d){
            mIvGal.setVisibility(View.VISIBLE);
            mLive2DLayout.setVisibility(View.GONE);
            mExitLive2D.setVisibility(View.GONE);
            mIvSelectMate.setVisibility(View.GONE);
            mIvSelectFuku.setVisibility(View.GONE);
            mIvSelectLanguage.setVisibility(View.GONE);
            mIvSoundLoad.setVisibility(View.GONE);
        }else if(id == R.id.iv_select_deskmate){
            Intent i = new Intent(MapActivity.this,SelectMateActivity.class);
            startActivity(i);
        }else if(id == R.id.iv_select_fuku){
            Intent i = new Intent(MapActivity.this,SelectFukuActivity.class);
            startActivityForResult(i,REQ_SELECT_FUKU);
        }else if(id == R.id.iv_select_language){
            ToastUtil.showCenterToast(this,R.string.label_can_not_use);
        }else if(id == R.id.iv_sign){
            if(DialogUtils.checkLoginAndShowDlg(MapActivity.this)){
                SignDialog dialog = new SignDialog(MapActivity.this);
                dialog.setSignDay(signDay);
                dialog.setIsSign(mIsSign);
                dialog.setAnimationEnable(true)
                        .setPositiveListener(new SignDialog.OnPositiveListener() {
                            @Override
                            public void onClick(SignDialog dialog) {
                                if (!mIsSign){
                                    signToday(dialog);
                                }else {
                                    ToastUtil.showCenterToast(MapActivity.this,R.string.label_signed);
                                }
                            }
                        }).show();
            }
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

    private void goLogin(){
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
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
            ToastUtil.showToast(this, R.string.msg_click_twice_to_exit);
            mLastBackTime = currentTime;
            return;
        }else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if(sMusicServiceManager != null){
//            sMusicServiceManager.exit();
//            sMusicServiceManager = null;
//        }
        unbindPlaybackService();

        AppSetting.isRunning = false;
    }
}
