package com.moemoe.lalala;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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
import com.app.common.util.LogUtil;
import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.data.AppUpdateInfo;
import com.moemoe.lalala.galgame.FileManager;
import com.moemoe.lalala.galgame.Live2DManager;
import com.moemoe.lalala.galgame.Live2DView;
import com.moemoe.lalala.galgame.SoundManager;
import com.moemoe.lalala.music.IOnServiceConnectComplete;
import com.moemoe.lalala.music.MusicServiceManager;
import com.moemoe.lalala.network.CallbackFactory;
import com.moemoe.lalala.network.OnNetWorkCallback;
import com.moemoe.lalala.utils.AlertDialogUtil;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.PreferenceManager;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ToastUtil;
import com.moemoe.lalala.view.dialog.SignDialog;
import com.moemoe.lalala.view.map.MapLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

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
    public static MusicServiceManager sMusicServiceManager = null;
    private boolean mIsOut = false;
    private boolean mIsPressed = false;
    private boolean mIsChanged = false;
    private boolean mIsFirstIn = false;
    private ObjectAnimator mSoundLoadAnim;
    private boolean mIsSign = false;
    private int signDay;
    private int mMapState = MAP_SCHOOL;

    @Override
    protected void initView() {
        mIsFirstIn = true;
        AppSetting.isRunning = true;
        if(mIntent != null){
            mSchema = mIntent.getStringExtra("schema");
        }
        sMusicServiceManager = new MusicServiceManager(this);
        sMusicServiceManager.connectService();
        sMusicServiceManager.setOnServiceConnectComplete(new IOnServiceConnectComplete() {
            @Override
            public void onServiceConnectComplete(IMediaService service) {
                LogUtil.i("连接完毕");
            }
        });
        if(mPreferMng == null){
            mPreferMng = PreferenceManager.getInstance(this);
        }
        mMap.setMapResource(R.drawable.map_school);
        initMap();
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
        mMap.addMapMarkView(R.drawable.btn_map_baojian, 0.26f, 0.30f, "neta://com.moemoe.lalala/department_1.0?uuid=10f8433e-5f80-11e6-b42a-d0a637eac7d7&name=保健室", null,2, null);
        mMap.addMapMarkView(R.drawable.btn_map_xiaomaibu, 0.30f, 0.54f, "neta://com.moemoe.lalala/department_1.0?uuid=1dd2dba6-5f80-11e6-ad3c-d0a637eac7d7&name=小卖部", null,2, null);
        mMap.addMapMarkView(R.drawable.btn_map_girls, 0.53f, 0.44f, "neta://com.moemoe.lalala/department_1.0?uuid=cfda0aa2-5f7f-11e6-b844-d0a637eac7d7&name=女性部", null,2, null);
        mMap.addMapMarkView(R.drawable.btn_map_news, 0.57f, 0.20f, "neta://com.moemoe.lalala/department_1.0?uuid=26f9831a-5f7f-11e6-8f94-d0a637eac7d7&name=新闻部", null,2, null);
        mMap.addMapMarkView(R.drawable.btn_map_sport, 0.75f, 0.25f, "neta://com.moemoe.lalala/department_1.0?uuid=393341d4-5f7f-11e6-a5af-d0a637eac7d7&name=体育馆", null, 2,null);
        mMap.addMapMarkView(R.drawable.btn_map_lib, 0.82f, 0.42f, "neta://com.moemoe.lalala/department_1.0?uuid=97e18352-5f7f-11e6-ae04-d0a637eac7d7&name=图书馆", null,2, null);
        mMap.addMapMarkView(R.drawable.btn_map_video, 0.83f, 0.66f, "neta://com.moemoe.lalala/department_1.0?uuid=a77f9006-5f7f-11e6-ae2c-d0a637eac7d7&name=影音部",null, 2, null);
        mMap.addMapMarkView(R.drawable.btn_map_picture, 0.66f, 0.64f, "neta://com.moemoe.lalala/department_1.0?uuid=be39718c-5f7f-11e6-81f9-d0a637eac7d7&name=美图部",null, 2, null);
        mMap.addMapMarkView(R.drawable.btn_map_game, 0.57f, 0.78f, "neta://com.moemoe.lalala/department_1.0?uuid=e255f9d4-5f7f-11e6-8a65-d0a637eac7d7&name=游戏部",null, 2, null);
        mMap.addMapMarkView(R.drawable.btn_map_trash, 0.63f, 0.39f, "neta://com.moemoe.lalala/garbage_1.0",null, 2, null);
        mMap.addMapMarkView(R.drawable.btn_map_gacha, 0.19f, 0.64f, "neta://com.moemoe.lalala/url_inner_1.0?http://prize.moemoe.la:8000/prize/index/",getString(R.string.label_lotter), 2, null);
        //mMap.addMapMarkView(R.drawable.btn_map_tudou, 0.85f, 0.15f, "neta://com.moemoe.lalala/department_1.0?uuid=2b30d006-75b1-11e6-ba67-e0576405f084&name=土豆映像季",null,  null);
        mMap.addMapMarkView(R.drawable.btn_map_trash_normal, 0.05f, 0.42f, "neta://com.moemoe.lalala/calui_1.0?uuid=6f90946e-7500-11e6-ba28-e0576405f084&name=anitoys",null, 2, null);
        mMap.addMapMarkView(R.drawable.btn_map_trash_normal, 0.16f, 0.56f, "neta://com.moemoe.lalala/calui_1.0?uuid=8b504a9a-7500-11e6-9642-e0576405f084&name=ALTER",null,2,  null);
        mMap.addMapMarkView(R.drawable.btn_map_trash_normal, 0.27f, 0.60f, "neta://com.moemoe.lalala/calui_1.0?uuid=9c183758-7500-11e6-b599-e0576405f084&name=animate",null,2,  null);
        mMap.addMapMarkView(R.drawable.btn_map_trash_normal, 0.26f, 0.66f, "neta://com.moemoe.lalala/calui_1.0?uuid=a727b466-7500-11e6-a06a-e0576405f084&name=天闻角川",null, 2, null);
        mMap.addMapMarkView(R.drawable.btn_map_trash_normal, 0.32f, 0.70f, "neta://com.moemoe.lalala/calui_1.0?uuid=b422d3fe-7500-11e6-955c-e0576405f084&name=轻漫画",null, 2, null);
        mMap.addMapMarkView(R.drawable.btn_map_trash_normal, 0.37f, 0.68f, "neta://com.moemoe.lalala/calui_1.0?uuid=be7a3728-7500-11e6-bdd7-e0576405f084&name=玛莎多拉",null, 2, null);
        mMap.addMapMarkView(R.drawable.btn_map_trash_normal, 0.32f, 0.76f, "neta://com.moemoe.lalala/calui_1.0?uuid=c7e1acf8-7500-11e6-a813-e0576405f084&name=口水三国",null, 2, null);
        mMap.addMapMarkView(R.drawable.btn_map_trash_normal, 0.09f, 0.56f, "neta://com.moemoe.lalala/calui_1.0?uuid=904a0f34-75b0-11e6-af78-e0576405f084&name=艾漫",null,2,  null);
        mMap.addMapMarkView(R.drawable.btn_map_trash_normal, 0.54f, 0.97f, "neta://com.moemoe.lalala/tag_1.0?8ed86824-b1d9-4755-bdcb-e88ff4ff1be6",null, 2, null);
        mMap.addMapMarkView(R.drawable.btn_map_trash_normal, 0.46f, 0.84f, "neta://com.moemoe.lalala/tag_1.0?d9a5a9d8-bdff-434e-b2cd-fef5b5b4911e",null, 2, null);
        mMap.addMapMarkView(R.drawable.btn_map_trash_normal, 0.52f, 0.82f, "neta://com.moemoe.lalala/tag_1.0?eda25f80-75b0-11e6-bf1c-e0576405f084",null, 2, null);
        mMap.addMapMarkView(R.drawable.btn_map_trash_normal, 0.63f, 0.85f, "neta://com.moemoe.lalala/tag_1.0?e182cc94-f681-4098-844b-7c7d548c719e",null, 2, null);
        mMap.addMapMarkView(R.drawable.btn_map_trash_normal, 0.68f, 0.94f, "neta://com.moemoe.lalala/tag_1.0?a7557872-c1ad-40fc-bba0-a9f517215e8c",null, 2, null);
        mMap.addMapMarkView(R.drawable.btn_map_trash_normal, 0.33f, 0.82f, "neta://com.moemoe.lalala/doc_1.0?3a61f262-75b3-11e6-a766-e0576405f084",null, 2, null);
        //mMap.addMapMarkView(R.drawable.map_plot, 0.18f, 0.80f, "neta://com.moemoe.lalala/plot_1.0?by_user=true&have_chose=false&plot=bg_plot_5,bg_plot_6",null,  null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.36f, 0.03f,null,"“他们是樱木军团：水户洋平，等等——”\n" + "“谁特么的是等等！！”",  2,null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.40f, 0.04f,null,"“日本著名中锋赤木刚宪定下的目标是——”\n" + "“得分！篮板！死库水！”",2,  null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.30f, 0.08f,null,"“好好练习的话，哥哥也是有可能去甲子园的！”\n" + "“挤挤电车的话，我随时都可以去。”",2,  null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.31f, 0.15f,null,"“隔壁薯片半价啦！！”\n" + "“所以不穿女装就不能买吗你们这些混蛋！！！！”", 2, null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.11f, 0.29f,null,"“为什么补考三次还不让我通过？”\n" + "“这一切都是命运石之门的选择。”", 2, null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.61f, 0.31f,null,"“和我签订契约成为马猴烧酒吧！”\n" + "“学姐：……”", 2, null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.76f, 0.37f,null,"“不能逃避、不能逃避、不能逃避！”\n" + "“碇真嗣，你这个笨蛋！”",2,  null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.88f, 0.37f,null,"“坂本ですが？”", 2, null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.90f, 0.27f,null,"“海贼王的男人和火影的男人——擦身而过了。”", 2, null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.95f, 0.54f,null,"“今天的风儿…好喧嚣啊…”\n" + "“嗯，让一下。”", 2, null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.71f, 0.55f,null,"“吾名为复仇天使暗猫，否定一切非二次元事物。诅咒面基、诅咒情侣、诅咒这世上的一切，给所有现充们施以破坏的铁锤吧，此乃吾生最大诅咒，噢不，最大邀请函！切身体会吧！”\n" + "“……别、别这样……我加入就是了……”", 2, null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.86f, 0.74f,null,"“喔哈哟！Ritsu~紬前辈！”\n" + "“喔哈哟！Mio~梓喵！”\n" + "（心疼呆唯一秒。）", 2, null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.03f, 0.50f,null,"“Leader，不让开的话就连你的【哔—】一起炸掉！”\n" + "“不要想要【哔—】的话就试试啊你这个抖s八嘎！”", 2, null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.05f, 0.95f,null,"“如果见死不救的话，那还不如一起死了算了。”\n" + "“这就是桐人考试不及格还怪我不给桐人抄的理由？”", 2, null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.44f, 0.50f,null,"“你可是比蛋糕还美味千百倍的猎物！”\n" + "“（真是糟糕的台词）行行行…我不跟你抢。”", 2, null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.43f, 0.32f,null,"“身为老师，最欣慰的莫过于，自己在犹豫中给予教诲，学生们却得到了明确的答案。”\n" + "……\n" + "……\n" + "……",2,  null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.93f, 0.72f,null,"“SOS团招募外星人、未来人、超能力者！！”\n" + "“团、团长……┬＿┬”", 2, null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.20f,0.79f,null,"“人们想得到什么，应该先思考能够付出什么！”\n" + "（什么嘛，原来你不会炼手办啊…）", 2, null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.19f, 0.70f,null,"“被漆黑烈焰吞噬殆尽吧！”\n" + "“就是因为不想再被人知道是中二病才转学来这里的你就忘了吗！”", 2, null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.34f, 0.87f,null,"“多么强壮的躯体…这哪位英灵的雕塑？”\n" + "“不清楚，可我能感受到他散发出的强大哲学气息。”", 2, null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.41f, 0.76f,null,"“喔~内~撒~嘛~咦嘻嘻嘻！”\n" + "“好像被奇怪的家伙盯上了…是错觉吗？”", 2, null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.61f, 0.59f,null,"“绘画技巧完全没有进步…”\n" + "“全力以赴就好，比起绘制这个学园的家伙，小千代已经可以称得上杰出了。”", 2, null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.67f, 0.36f,null,"“艾斯，我一定会继承你的意志！”", 2, null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.34f, 0.97f,null,"“离十年后的八月还有多久？”\n" +
                "“在这里的话，就不用等那一天的到来了。”\n" +
                "（也不会到来…）", 1, null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.66f, 0.51f,null,"“新来的转校生似乎被安排在莲的同桌。”\n" +
                "“喔喔喔喔！！真好命啊！！居然能和那样的美少女朝夕相处！”\n" +
                "“算了吧，你又不是不知道那个家伙…”", 0, null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.80f, 0.56f,null,"“听说了吗？传得沸沸扬扬，午夜十二点的学院……”\n" +
                "“噫！快别说了！”", 0, null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.98f, 0.61f,null,"“最近啊，校长总是在夜里鬼鬼祟祟的巡视，肯定是想做什么肮脏的事情！”\n" +
                "“不是吧！校长不像是那样的人！”\n" +
                "“那么校长室为什么要装密码锁呢？”", 0, null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.33f, 0.43f,null,"根据以往的套路，这个垃圾桶一样大有问题…\n" +
                "尤其是，旁边还站着一位老绅士。\n" +
                "嗯，大有问题。", 2, null);
        mMap.addMapMarkView(R.drawable.btn_map_click, 0.48f, 0.19f,"neta://com.moemoe.lalala/event_1.0",null, 1, null);
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
            Otaku.getCommonV2().checkVersion().enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
                @Override
                public void success(String token, String s) {
                    AppUpdateInfo info = new AppUpdateInfo();
                    info.readFromJsonContent(s);
                    if (info.updateStatus != 0) {
                        showUpdateDialog(info);
                    }
                }

                @Override
                public void failure(String e) {

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
        if(sMusicServiceManager != null){
            sMusicServiceManager.exit();
            sMusicServiceManager = null;
        }
        AppSetting.isRunning = false;
    }
}
