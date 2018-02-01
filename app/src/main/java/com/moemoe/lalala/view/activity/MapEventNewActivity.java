package com.moemoe.lalala.view.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;

import com.moemoe.lalala.di.components.DaggerJuQIngChatComponent;
import com.moemoe.lalala.di.modules.JuQingChatModule;
import com.moemoe.lalala.event.EventDoneEvent;
import com.moemoe.lalala.model.entity.JuQingMapShowEntity;
import com.moemoe.lalala.model.entity.JuQingNormalEvent;
import com.moemoe.lalala.presenter.JuQIngChatContract;
import com.moemoe.lalala.presenter.JuQingChatPresenter;
import com.moemoe.lalala.utils.AlertDialogUtil;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.DownLoadUtils;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.GreenDaoManager;
import com.moemoe.lalala.utils.JuQingUtil;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.TextAppearOneControl;
import com.moemoe.lalala.view.widget.netamenu.CenterMenuFragment;
import com.moemoe.lalala.view.widget.netamenu.MenuItem;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.http.Path;

/**
 *
 * Created by yi on 2017/10/12.
 */

public class MapEventNewActivity extends BaseAppCompatActivity implements JuQIngChatContract.View{

    @BindView(R.id.iv_bg)
    ImageView mIvBg;
    @BindView(R.id.iv_cg)
    ImageView mIvCg;
    @BindView(R.id.iv_pose)
    ImageView mIvPose;
    @BindView(R.id.iv_face)
    ImageView mIvFace;
    @BindView(R.id.iv_extra)
    ImageView mIvExtra;
    @BindView(R.id.iv_skip)
    ImageView mIvSkip;
    @BindView(R.id.iv_item)
    ImageView mIvItem;
    @BindView(R.id.tv_text)
    TextView mTvText;
    @BindView(R.id.tv_name)
    TextView mTvName;
    @BindView(R.id.fl_click)
    View mRoot;
    @BindView(R.id.iv_download)
    ImageView mIvDownBg;
    @BindView(R.id.ll_progress)
    View mDownRoot;
    @BindView(R.id.tv_progress)
    TextView mTvProgress;
    @BindView(R.id.progress)
    ProgressBar mBar;

    @Inject
    JuQingChatPresenter mPresenter;

    private MediaPlayer mBgmPlayer;
    private MediaPlayer mVolPlayer;
    private ArrayList<JuQingMapShowEntity> eventList;
    private String mId;
    private int mCurIndex;
    private boolean mIsOut;
    private String mCurCg= "";
    private Drawable mCurCgDrawable = null;
    private String mCurBgm = "";
    private String mCurBg = "";
    private Drawable mCurBgDrawable = null;
    private String mCurPose = "";
    private Drawable mCurPoseDrawable = null;
    private String mCurFace = "";
    private Drawable mCurFaceDrawable = null;
    private String mCurExtra = "";
    private Drawable mCurExtraDrawable = null;
    private boolean mPoseOut;
    private HashMap<String,String> notDown;
    private Drawable mCurItemDrawable = null;
    private Disposable mDownloadDisposable;
    private Disposable mdecrptDisposable;
    private boolean isError;


    @Override
    protected int getLayoutId() {
        return R.layout.ac_map_event_new;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerJuQIngChatComponent.builder()
                .juQingChatModule(new JuQingChatModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mId = getIntent().getStringExtra("id");
        if(TextUtils.isEmpty(mId)){
            finish();
            return;
        }
        eventList = JuQingUtil.getMapEventShow(mId);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.leftMargin = (int) - getResources().getDimension(R.dimen.x10);
        lp.rightMargin = (int) - getResources().getDimension(R.dimen.x10);
        mIvBg.setLayoutParams(lp);
        mIvCg.setLayoutParams(lp);
        TextAppearOneControl.getInstance().setTextView(mTvText);
        if(eventList != null && eventList.size() > 0){
            //下载资源到本地
           // ArrayList<String> needDown = new ArrayList<>();
            final HashSet<String> needDown = new HashSet<>();
            notDown = new HashMap<>();
            for (JuQingMapShowEntity entity : eventList){
                if(!"无".equals(entity.getPose().getFile()) && !FileUtil.isExists(entity.getPose().getLocalPath())){
                    needDown.add(entity.getPose().getFile());
                }
                if(!"无".equals(entity.getPose().getFile())) notDown.put(entity.getPose().getLocalPath(),entity.getPose().getMd5());

                if(!"无".equals(entity.getFace().getFile()) && !FileUtil.isExists(entity.getFace().getLocalPath())){
                    needDown.add(entity.getFace().getFile());
                }
                if(!"无".equals(entity.getFace().getFile())) notDown.put(entity.getFace().getLocalPath(),entity.getFace().getMd5());

                if(!"无".equals(entity.getExtra().getFile()) && !FileUtil.isExists(entity.getExtra().getLocalPath())){
                    needDown.add(entity.getExtra().getFile());
                }
                if(!"无".equals(entity.getExtra().getFile())) notDown.put(entity.getExtra().getLocalPath(),entity.getExtra().getMd5());

                if(!"无".equals(entity.getVol().getFile()) && !FileUtil.isExists(entity.getVol().getLocalPath())){
                    needDown.add(entity.getVol().getFile());
                }
                if(!"无".equals(entity.getVol().getFile())) notDown.put(entity.getVol().getLocalPath(),entity.getVol().getMd5());

                if(!"无".equals(entity.getBgm().getFile()) && !FileUtil.isExists(entity.getBgm().getLocalPath())){
                    needDown.add(entity.getBgm().getFile());
                }
                if(!"无".equals(entity.getBgm().getFile())) notDown.put(entity.getBgm().getLocalPath(),entity.getBgm().getMd5());

                if(!"无".equals(entity.getCg().getFile()) && !FileUtil.isExists(entity.getCg().getLocalPath())){
                    needDown.add(entity.getCg().getFile());
                }
                if(!"无".equals(entity.getCg().getFile())) notDown.put(entity.getCg().getLocalPath(),entity.getCg().getMd5());

                if(!"无".equals(entity.getBg().getFile()) && !FileUtil.isExists(entity.getBg().getLocalPath())){
                    needDown.add(entity.getBg().getFile());
                }
                if(!"无".equals(entity.getBg().getFile())) notDown.put(entity.getBg().getLocalPath(),entity.getBg().getMd5());

                if(!"无".equals(entity.getItem().getFile()) && !FileUtil.isExists(entity.getItem().getLocalPath())){
                    needDown.add(entity.getItem().getFile());
                }
                if(!"无".equals(entity.getItem().getFile())) notDown.put(entity.getItem().getLocalPath(),entity.getItem().getMd5());
            }
            mIvDownBg.setVisibility(View.VISIBLE);
            mDownRoot.setVisibility(View.VISIBLE);
            mIvSkip.setVisibility(View.GONE);
            mIvDownBg.setImageResource(R.drawable.download_animation);
            final AnimationDrawable  animationDrawable = (AnimationDrawable) mIvDownBg.getDrawable();
            animationDrawable.start();
            mTvProgress.setText("正在加载资源 " + 0 + "/" + notDown.size());
            mBar.setMax(notDown.size());
            mBar.setProgress(0);
            if(needDown.size() > 0){
                JuQingUtil.downLoadFiles(this, needDown, new Observer<String>() {

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDownloadDisposable = d;
                    }

                    @Override
                    public void onNext(@NonNull String s) {
                        if(mBar != null)mBar.setProgress(mBar.getProgress() + 1);
                        if(mTvProgress != null) mTvProgress.setText("正在加载资源 " + mBar.getProgress() + "/" + notDown.size());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        isError = true;
                        showToast("资源出错或者存储空间不足，请重试");
                        finish();
                    }

                    @Override
                    public void onComplete() {
                        if(!isFinishing())decryptFile(notDown,animationDrawable);
                    }
                });
            }else {
                decryptFile(notDown,animationDrawable);
            }
        }else {
            finish();
        }
        mIvSkip.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                final AlertDialogUtil alertDialogUtil = AlertDialogUtil.getInstance();
                alertDialogUtil.createPromptNormalDialog(MapEventNewActivity.this,"是否跳过剧情?");
                alertDialogUtil.setOnClickListener(new AlertDialogUtil.OnClickListener() {
                    @Override
                    public void CancelOnClick() {
                        alertDialogUtil.dismissDialog();
                    }

                    @Override
                    public void ConfirmOnClick() {
                        mCurIndex = -1;
                        mPresenter.doneJuQing(mId);
                        alertDialogUtil.dismissDialog();
                    }
                });
                alertDialogUtil.showDialog();
            }
        });
    }

    private void decryptFile(final HashMap<String,String> set, final AnimationDrawable animationDrawable){
        try {
            DownLoadUtils.initalize_dencryptkey("kira");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Observable.fromIterable(set.keySet())
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<String, ObservableSource<File>>() {
                    @Override
                    public ObservableSource<File> apply(@NonNull final String s) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<File>() {
                            @Override
                            public void subscribe(@NonNull ObservableEmitter<File> e) throws Exception {
                                try{
                                    File file = new File(s);
                                    String md5 = set.get(s);
                                    if(md5.length() < 32){
                                        int n = 32 - md5.length();
                                        for(int i = 0;i < n;i++){
                                            md5 = "0" + md5;
                                        }
                                    }
                                    if(md5.equals(StringUtils.getFileMD5(file))){
                                        byte[] res = new byte[(int) file.length()];
                                        FileInputStream fileIn = new FileInputStream(file);
                                        fileIn.read(res);
                                        fileIn.close();
                                        byte[] news = DownLoadUtils.decrypt(res);
                                        File f2 = new File(s.substring(0,s.lastIndexOf(".")));
                                        FileOutputStream out = new FileOutputStream(f2);
                                        out.write(news);
                                        out.close();
                                        e.onNext(f2);
                                        e.onComplete();
                                    }else {
                                        e.onError(new Throwable("资源校验出错"));
                                    }
                                }catch (Exception er){
                                    e.onError(er);
                                }
                            }
                        });
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<File>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mdecrptDisposable = d;
                    }

                    @Override
                    public void onNext(@NonNull File o) {
                        if(mBar != null)mBar.setProgress(mBar.getProgress() + 1);
                        if(mTvProgress != null) mTvProgress.setText("正在加载资源 " + mBar.getProgress() + "/" + notDown.size());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        isError = true;
                        showToast("资源校验出错，请重试");
                        finish();
                    }

                    @Override
                    public void onComplete() {
                        if(mIvDownBg != null) mIvDownBg.setVisibility(View.GONE);
                        if(mDownRoot != null) mDownRoot.setVisibility(View.GONE);
                        if(mIvSkip != null) mIvSkip.setVisibility(View.VISIBLE);
                        if(animationDrawable != null) animationDrawable.stop();
                        if(mIvDownBg != null) mIvDownBg.setImageDrawable(null);
                        mCurIndex = 0;
                        if(mRoot != null) {
                            mRoot.setOnClickListener(new NoDoubleClickListener(100) {
                                @Override
                                public void onNoDoubleClick(View v) {
                                    clickEvent();
                                }
                            });
                            showEvent();
                        }
                    }
                });
    }

    private void clickEvent(){
        if(mCurIndex != -1){
            JuQingMapShowEntity entity =  eventList.get(mCurIndex);
            if(!TextAppearOneControl.getInstance().isFinish()){
                TextAppearOneControl.getInstance().setShowAll(true);
            }else {
                if(entity.isShowCg()){
                    imgIn();
                    showCharacter(entity);
                    entity.setShowCg(false);
                    eventList.get(mCurIndex).setShowCg(false);
                }else {
                    if(entity.getChoice().size() > 0){
                        if (entity.getChoice().size() == 1){
                            for(int index : entity.getChoice().values()){
                                mCurIndex = index;
                                showEvent();
                            }
                        }else {
                            showMenu(entity.getChoice());
                        }
                    }else {
                        mCurIndex = -1;
                        mPresenter.doneJuQing(mId);
                    }
                }
            }
        }
    }

    private void showMenu(LinkedHashMap<String,Integer> map){
        final ArrayList<MenuItem> items = new ArrayList<>();
        for(String name : map.keySet()){
            MenuItem item = new MenuItem(map.get(name), name);
            items.add(item);
        }
        CenterMenuFragment fragment = new CenterMenuFragment();
        fragment.setCancelable(false);
        fragment.setMenuItems(items);
        fragment.setMenuType(CenterMenuFragment.TYPE_VERTICAL);
        fragment.setmClickListener(new CenterMenuFragment.MenuItemClickListener() {
            @Override
            public void OnMenuItemClick(int itemId) {
                mCurIndex = itemId;
                if(mCurIndex != -1){
                    showEvent();
                }
            }
        });
        fragment.show(getSupportFragmentManager(), "MapEventMenu");
    }

    private void showEvent(){
        //显示剧情
        JuQingMapShowEntity entity =  eventList.get(mCurIndex);
        //bg
        if(!"无".equals(entity.getBg().getFile())){
            //File file = new File(entity.getBg().getLocalPath().substring(0,entity.getBg().getLocalPath().lastIndexOf(".")));
            if(isExistImg(entity.getBg().getLocalPath().substring(0,entity.getBg().getLocalPath().lastIndexOf(".")))){
                if(!entity.getBg().getLocalPath().equals(mCurBg)){
                    Drawable drawable = Drawable.createFromPath(entity.getBg().getLocalPath().substring(0,entity.getBg().getLocalPath().lastIndexOf(".")));
                    mIvBg.setImageDrawable(drawable);
//                    if(mCurBgDrawable == null){
//                        Glide.with(this)
//                                .load(file)
//                                .dontAnimate()
//                                .into(new GlideDrawableImageViewTarget(mIvBg){
//                                    @Override
//                                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
//                                        super.onResourceReady(resource, animation);
//                                        mCurBgDrawable = resource;
//                                    }
//                                });
//                    }else {
//                        Glide.with(this)
//                                .load(file)
//                                .placeholder(mCurBgDrawable)
//                                .dontAnimate()
//                                .into(new GlideDrawableImageViewTarget(mIvBg){
//                                    @Override
//                                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
//                                        super.onResourceReady(resource, animation);
//                                        mCurBgDrawable = resource;
//                                    }
//                                });
//                    }
                    mCurBg = entity.getBg().getLocalPath();
                }
                if("shock".equals(entity.getBg().getEffect())){
                    Animation animation = AnimationUtils.loadAnimation(this, R.anim.shake);
                    animation.setStartOffset(100);
                    mIvBg.startAnimation(animation);
                }
            }else {
                error();
            }
        }else {
            mIvBg.setImageDrawable(null);
            mCurBg = "";
            mCurBgDrawable = null;
        }
        //cg
        if(!"无".equals(entity.getCg().getFile())){
           // File file = new File(entity.getCg().getLocalPath().substring(0,entity.getCg().getLocalPath().lastIndexOf(".")));
            if(isExistImg(entity.getCg().getLocalPath().substring(0,entity.getCg().getLocalPath().lastIndexOf(".")))){
                if(!entity.getCg().getLocalPath().equals(mCurCg)){
                    Drawable drawable = Drawable.createFromPath(entity.getCg().getLocalPath().substring(0,entity.getCg().getLocalPath().lastIndexOf(".")));
                    mIvCg.setImageDrawable(drawable);
//                    if(mCurCgDrawable == null){
//                        Glide.with(this)
//                                .load(file)
//                                .dontAnimate()
//                                .into(new GlideDrawableImageViewTarget(mIvCg){
//                                    @Override
//                                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
//                                        super.onResourceReady(resource, animation);
//                                        mCurCgDrawable = resource;
//                                    }
//                                });
//                    }else {
//                        Glide.with(this)
//                                .load(file)
//                                .placeholder(mCurCgDrawable)
//                                .dontAnimate()
//                                .into(new GlideDrawableImageViewTarget(mIvCg){
//                                    @Override
//                                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
//                                        super.onResourceReady(resource, animation);
//                                        mCurCgDrawable = resource;
//                                    }
//                                });
//                    }
                    eventList.get(mCurIndex).setShowCg(true);
                    entity.setShowCg(true);
                    imgOut();
                    mCurCg = entity.getCg().getLocalPath();
                }
                if("shock".equals(entity.getCg().getEffect())){
                    Animation animation = AnimationUtils.loadAnimation(this, R.anim.shake);
                    animation.setStartOffset(100);
                    mIvCg.startAnimation(animation);
                }
            }else {
                error();
            }
        }else {
            mIvCg.setImageDrawable(null);
            mCurCg = "";
            mCurCgDrawable = null;
        }
        //人物等
        if(!entity.isShowCg()){
            showCharacter(entity);
        }
        //bgm
        if(!TextUtils.isEmpty(entity.getBgm().getLocalPath())){
            if(!mCurBgm.equals(entity.getBgm().getLocalPath())){
                if(mBgmPlayer != null){
                    if(mBgmPlayer.isPlaying()) {
                        mBgmPlayer.release();
                    }
                    mBgmPlayer = null;
                }
                mBgmPlayer = new MediaPlayer();
                try {
                    mBgmPlayer.setDataSource(entity.getBgm().getLocalPath().substring(0,entity.getBgm().getLocalPath().lastIndexOf(".")));
                    mBgmPlayer.prepareAsync();    // 异步准备，不会阻碍主线程
                    mBgmPlayer.setLooping(true);
                    // 当准备好时
                    mBgmPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mBgmPlayer.setLooping(true);
                            mBgmPlayer.start();
                        }
                    });
                    // 当播放完成时
                    mBgmPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            if(mBgmPlayer.isLooping()){
                                mBgmPlayer.setLooping(true);
                                mBgmPlayer.start();
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mCurBgm = entity.getBgm().getLocalPath();
            }
        }else {
            if(mBgmPlayer != null){
                if(mBgmPlayer.isPlaying()) {
                    mBgmPlayer.release();
                }
                mBgmPlayer = null;
            }
        }
    }

    private void showCharacter(final JuQingMapShowEntity entity){
        if(!"无".equals(entity.getPose().getFile())){
           // File file = new File(entity.getPose().getLocalPath().substring(0,entity.getPose().getLocalPath().lastIndexOf(".")));
            if(isExistImg(entity.getPose().getLocalPath().substring(0,entity.getPose().getLocalPath().lastIndexOf(".")))){
                if(!mCurPose.equals(entity.getPose().getLocalPath())){
                    Drawable drawable = Drawable.createFromPath(entity.getPose().getLocalPath().substring(0,entity.getPose().getLocalPath().lastIndexOf(".")));
                    mIvPose.setImageDrawable(drawable);
//                    if(mCurPoseDrawable == null){
//                        Glide.with(this)
//                                .load(file)
//                                .dontAnimate()
//                                .into(new GlideDrawableImageViewTarget(mIvPose){
//                                    @Override
//                                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
//                                        super.onResourceReady(resource, animation);
//                                        mCurPoseDrawable = resource;
//                                    }
//                                });
//                    }else {
//                        Glide.with(this)
//                                .load(file)
//                                .placeholder(mCurPoseDrawable)
//                                .dontAnimate()
//                                .into(new GlideDrawableImageViewTarget(mIvPose){
//                                    @Override
//                                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
//                                        super.onResourceReady(resource, animation);
//                                        mCurPoseDrawable = resource;
//                                    }
//                                });
//                    }
                    mCurPose = entity.getPose().getLocalPath();
                }
            }else {
                error();
            }
        }else {
            mIvPose.setImageDrawable(null);
            mCurPose = "";
            mCurPoseDrawable = null;

        }
        if(!"无".equals(entity.getFace().getFile())){
         //   File file = new File(entity.getFace().getLocalPath().substring(0,entity.getFace().getLocalPath().lastIndexOf(".")));
            if(isExistImg(entity.getFace().getLocalPath().substring(0,entity.getFace().getLocalPath().lastIndexOf(".")))){
                if(!mCurFace.equals(entity.getFace().getLocalPath())){
                    Drawable drawable = Drawable.createFromPath(entity.getFace().getLocalPath().substring(0,entity.getFace().getLocalPath().lastIndexOf(".")));
                    mIvFace.setImageDrawable(drawable);
//                    if(mCurFaceDrawable == null){
//                        Glide.with(this)
//                                .load(file)
//                                .dontAnimate()
//                                .into(new GlideDrawableImageViewTarget(mIvFace){
//                                    @Override
//                                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
//                                        super.onResourceReady(resource, animation);
//                                        mCurFaceDrawable = resource;
//                                    }
//                                });
//                    }else {
//                        Glide.with(this)
//                                .load(file)
//                                .placeholder(mCurFaceDrawable)
//                                .dontAnimate()
//                                .into(new GlideDrawableImageViewTarget(mIvFace){
//                                    @Override
//                                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
//                                        super.onResourceReady(resource, animation);
//                                        mCurFaceDrawable = resource;
//                                    }
//                                });
//                    }
                    mCurFace = entity.getFace().getLocalPath();
                }
            }else {
                error();
            }
        }else {
            mIvFace.setImageDrawable(null);
            mCurFace = "";
            mCurFaceDrawable = null;
        }
        if(!"无".equals(entity.getExtra().getFile())){
           // File file = new File(entity.getExtra().getLocalPath().substring(0,entity.getExtra().getLocalPath().lastIndexOf(".")));
            if(isExistImg(entity.getExtra().getLocalPath().substring(0,entity.getExtra().getLocalPath().lastIndexOf(".")))){
                if(!mCurExtra.equals(entity.getExtra().getLocalPath())){
                    Drawable drawable = Drawable.createFromPath(entity.getExtra().getLocalPath().substring(0,entity.getExtra().getLocalPath().lastIndexOf(".")));
                    mIvExtra.setImageDrawable(drawable);
//                    if(mCurExtraDrawable == null){
//                        Glide.with(this)
//                                .load(file)
//                                .dontAnimate()
//                                .into(new GlideDrawableImageViewTarget(mIvExtra){
//                                    @Override
//                                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
//                                        super.onResourceReady(resource, animation);
//                                        mCurExtraDrawable = resource;
//                                    }
//                                });
//                    }else {
//                        Glide.with(this)
//                                .load(file)
//                                .placeholder(mCurExtraDrawable)
//                                .dontAnimate()
//                                .into(new GlideDrawableImageViewTarget(mIvExtra){
//                                    @Override
//                                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
//                                        super.onResourceReady(resource, animation);
//                                        mCurExtraDrawable = resource;
//                                    }
//                                });
//                    }
                    mCurExtra = entity.getExtra().getLocalPath();
                }
            }else {
                error();
            }
        }else {
            mIvExtra.setImageDrawable(null);
            mCurExtra = "";
            mCurExtraDrawable = null;
        }
        Observable.timer(100, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull Long aLong) {

                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        if("shock".equals(entity.getPose().getEffect())){
                            shock();
                        }else if("movein".equals(entity.getPose().getEffect())){
                            movein();
                        }else if("moveout".equals(entity.getPose().getEffect())){
                            moveout();
                        }else if("soakin".equals(entity.getPose().getEffect())){
                            soakin();
                        }else if("soakout".equals(entity.getPose().getEffect())){
                            soakout();
                        }else{
                            normal();
                        }
                    }
                });
        if(!"无".equals(entity.getItem().getFile())){
            //File file = new File(entity.getItem().getLocalPath().substring(0,entity.getItem().getLocalPath().lastIndexOf(".")));
            if(isExistImg(entity.getItem().getLocalPath().substring(0,entity.getItem().getLocalPath().lastIndexOf(".")))){
                Drawable drawable = Drawable.createFromPath(entity.getItem().getLocalPath().substring(0,entity.getItem().getLocalPath().lastIndexOf(".")));
                mIvItem.setImageDrawable(drawable);
//                if(mCurItemDrawable == null){
//                    Glide.with(this)
//                            .load(file)
//                            .dontAnimate()
//                            .into(new GlideDrawableImageViewTarget(mIvItem){
//                                @Override
//                                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
//                                    super.onResourceReady(resource, animation);
//                                    mCurItemDrawable = resource;
//                                }
//                            });
//                }else {
//                    Glide.with(this)
//                            .load(file)
//                            .placeholder(mCurItemDrawable)
//                            .dontAnimate()
//                            .into(new GlideDrawableImageViewTarget(mIvItem){
//                                @Override
//                                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
//                                    super.onResourceReady(resource, animation);
//                                    mCurItemDrawable = resource;
//                                }
//                            });
//                }
                if("shock".equals(entity.getItem().getEffect())){
                    Animation animation = AnimationUtils.loadAnimation(this, R.anim.shake);
                    animation.setStartOffset(100);
                    mIvItem.startAnimation(animation);
                }
            }else {
                error();
            }
        }else {
            mIvItem.setImageDrawable(null);
            mCurItemDrawable = null;
        }
        if("我".equals(entity.getName())){
            mTvName.setText(PreferenceUtils.getAuthorInfo().getUserName());
        }else {
            mTvName.setText(entity.getName());
        }
        if(TextUtils.isEmpty(entity.getName())){
            mTvName.setVisibility(View.INVISIBLE);
        }else {
            mTvName.setVisibility(View.VISIBLE);
        }
        if(TextUtils.isEmpty(entity.getTalk().getText())){
            mTvText.setVisibility(View.INVISIBLE);
        }else {
            mTvText.setVisibility(View.VISIBLE);
        }
        //mTvText.setText(entity.getTalk().getText());
        TextAppearOneControl.getInstance().setTextAndStart(entity.getTalk().getText());
        if("shock".equals(entity.getTalk().getEffect())){
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.shake);
            animation.setStartOffset(100);
            if(mTvText.getVisibility() == View.VISIBLE) mTvText.startAnimation(animation);
            if(mTvName.getVisibility() == View.VISIBLE) mTvName.startAnimation(animation);
        }
        if(!TextUtils.isEmpty(entity.getVol().getLocalPath())){
            if(mVolPlayer != null){
                if(mVolPlayer.isPlaying()) {
                    mVolPlayer.release();
                }
                mVolPlayer = null;
            }
            mVolPlayer = new MediaPlayer();
            try {
                mVolPlayer.setDataSource(entity.getVol().getLocalPath().substring(0,entity.getVol().getLocalPath().lastIndexOf(".")));
                mVolPlayer.prepareAsync();    // 异步准备，不会阻碍主线程
                // 当准备好时
                mVolPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mVolPlayer.setLooping(false);
                        mVolPlayer.start();
                    }
                });
                // 当播放完成时
                mVolPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mVolPlayer.release();
                        mVolPlayer = null;
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            if(mVolPlayer != null) mVolPlayer.release();
            mVolPlayer = null;
        }
    }

    private void error(){
        showToast("资源已被删除或加载错误");
        finish();
    }

    private void imgOut(){
        mIsOut = true;
        if(mPoseOut){
            ObjectAnimator phoneAnimator = ObjectAnimator.ofFloat(mTvName,"translationY",0,mTvName.getHeight() + mTvText.getHeight() + getResources().getDimension(R.dimen.y52) - (int)getResources().getDimension(R.dimen.y50)).setDuration(500);
            phoneAnimator.setInterpolator(new LinearInterpolator());
            ObjectAnimator signAnimator = ObjectAnimator.ofFloat(mTvText,"translationY",0,mTvText.getHeight() + getResources().getDimension(R.dimen.y52)).setDuration(500);
            signAnimator.setInterpolator(new LinearInterpolator());
            AnimatorSet set = new AnimatorSet();
            set.play(phoneAnimator).with(signAnimator);
            set.start();
        }else {
            ObjectAnimator cardAnimator = ObjectAnimator.ofFloat(mIvPose,"translationX",0,mIvPose.getWidth()).setDuration(500);
            cardAnimator.setInterpolator(new LinearInterpolator());
            ObjectAnimator bagAnimator = ObjectAnimator.ofFloat(mIvFace,"translationX",0,mIvFace.getWidth()).setDuration(500);
            bagAnimator.setInterpolator(new LinearInterpolator());
            ObjectAnimator squareAnimator = ObjectAnimator.ofFloat(mIvExtra,"translationX",0,mIvExtra.getWidth()).setDuration(500);
            squareAnimator.setInterpolator(new LinearInterpolator());
            ObjectAnimator phoneAnimator = ObjectAnimator.ofFloat(mTvName,"translationY",0,mTvName.getHeight() + mTvText.getHeight() + getResources().getDimension(R.dimen.y35) + getResources().getDimension(R.dimen.y14)).setDuration(500);
            phoneAnimator.setInterpolator(new LinearInterpolator());
            ObjectAnimator signAnimator = ObjectAnimator.ofFloat(mTvText,"translationY",0,mTvText.getHeight() + getResources().getDimension(R.dimen.y35)).setDuration(500);
            signAnimator.setInterpolator(new LinearInterpolator());
            AnimatorSet set = new AnimatorSet();
            set.play(cardAnimator).with(bagAnimator);
            set.play(bagAnimator).with(squareAnimator);
            set.play(squareAnimator).with(phoneAnimator);
            set.play(phoneAnimator).with(signAnimator);
            set.start();
            mPoseOut = true;
        }
    }

    private void imgIn(){
        mIsOut = false;
        if(mPoseOut){
            ObjectAnimator cardAnimator = ObjectAnimator.ofFloat(mIvPose,"translationX",mIvPose.getWidth(),0).setDuration(500);
            cardAnimator.setInterpolator(new LinearInterpolator());
            ObjectAnimator bagAnimator = ObjectAnimator.ofFloat(mIvFace,"translationX",mIvFace.getWidth(),0).setDuration(500);
            bagAnimator.setInterpolator(new LinearInterpolator());
            ObjectAnimator squareAnimator = ObjectAnimator.ofFloat(mIvExtra,"translationX",mIvExtra.getWidth(),0).setDuration(500);
            squareAnimator.setInterpolator(new LinearInterpolator());
            ObjectAnimator phoneAnimator = ObjectAnimator.ofFloat(mTvName,"translationY",mTvName.getHeight() + mTvText.getHeight() + getResources().getDimension(R.dimen.y52) - (int)getResources().getDimension(R.dimen.y50),0).setDuration(500);
            phoneAnimator.setInterpolator(new LinearInterpolator());
            ObjectAnimator signAnimator = ObjectAnimator.ofFloat(mTvText,"translationY",mTvText.getHeight() + getResources().getDimension(R.dimen.y52),0).setDuration(500);
            signAnimator.setInterpolator(new LinearInterpolator());
            AnimatorSet set = new AnimatorSet();
            set.play(cardAnimator).with(bagAnimator);
            set.play(bagAnimator).with(squareAnimator);
            set.play(squareAnimator).with(phoneAnimator);
            set.play(phoneAnimator).with(signAnimator);
            set.start();
            mPoseOut = false;
        }else {
            ObjectAnimator phoneAnimator = ObjectAnimator.ofFloat(mTvName,"translationY",mTvName.getHeight() + mTvText.getHeight() + getResources().getDimension(R.dimen.y52)  - (int)getResources().getDimension(R.dimen.y50),0).setDuration(500);
            phoneAnimator.setInterpolator(new LinearInterpolator());
            ObjectAnimator signAnimator = ObjectAnimator.ofFloat(mTvText,"translationY",mTvText.getHeight() + getResources().getDimension(R.dimen.y52),0).setDuration(500);
            signAnimator.setInterpolator(new LinearInterpolator());
            AnimatorSet set = new AnimatorSet();
            set.play(phoneAnimator).with(signAnimator);
            set.start();
        }

    }

    private boolean isExistImg(String path){
        return FileUtil.isExists(path);
    }

    private void shock(){//动画shock
        ObjectAnimator cardAnimator = ObjectAnimator.ofFloat(mIvPose,"translationX",0,10,0).setDuration(100);
        cardAnimator.setInterpolator(new LinearInterpolator());
        cardAnimator.setRepeatCount(5);
        ObjectAnimator bagAnimator = ObjectAnimator.ofFloat(mIvFace,"translationX",0,10,0).setDuration(100);
        bagAnimator.setInterpolator(new LinearInterpolator());
        bagAnimator.setRepeatCount(5);
        ObjectAnimator squareAnimator = ObjectAnimator.ofFloat(mIvExtra,"translationX",0,10,0).setDuration(100);
        squareAnimator.setInterpolator(new LinearInterpolator());
        squareAnimator.setRepeatCount(5);
        AnimatorSet set = new AnimatorSet();
        set.play(cardAnimator).with(bagAnimator);
        set.play(bagAnimator).with(squareAnimator);
        set.start();
    }

    //effect":"shock,movein,moveout,soakin,soakout"

    private void movein(){//movein
        mPoseOut = false;
        ObjectAnimator cardAnimator = ObjectAnimator.ofFloat(mIvPose,"translationX",mIvPose.getWidth(),0).setDuration(1000);
        cardAnimator.setInterpolator(new LinearInterpolator());
        ObjectAnimator bagAnimator = ObjectAnimator.ofFloat(mIvFace,"translationX",mIvFace.getWidth(),0).setDuration(1000);
        bagAnimator.setInterpolator(new LinearInterpolator());
        ObjectAnimator squareAnimator = ObjectAnimator.ofFloat(mIvExtra,"translationX",mIvExtra.getWidth(),0).setDuration(1000);
        squareAnimator.setInterpolator(new LinearInterpolator());
        AnimatorSet set = new AnimatorSet();
        set.play(cardAnimator).with(bagAnimator);
        set.play(bagAnimator).with(squareAnimator);
        set.start();
    }

    private void moveout(){//moveout
        mPoseOut = true;
        ObjectAnimator cardAnimator = ObjectAnimator.ofFloat(mIvPose,"translationX",0,mIvPose.getWidth()).setDuration(1000);
        cardAnimator.setInterpolator(new LinearInterpolator());
        ObjectAnimator bagAnimator = ObjectAnimator.ofFloat(mIvFace,"translationX",0,mIvFace.getWidth()).setDuration(1000);
        bagAnimator.setInterpolator(new LinearInterpolator());
        ObjectAnimator squareAnimator = ObjectAnimator.ofFloat(mIvExtra,"translationX",0,mIvExtra.getWidth()).setDuration(1000);
        squareAnimator.setInterpolator(new LinearInterpolator());
        AnimatorSet set = new AnimatorSet();
        set.play(cardAnimator).with(bagAnimator);
        set.play(bagAnimator).with(squareAnimator);
        set.start();
    }

    private void soakin(){//soakin
        ObjectAnimator cardAnimator = ObjectAnimator.ofFloat(mIvPose,"alpha",0,1).setDuration(3000);
        cardAnimator.setInterpolator(new LinearInterpolator());
        ObjectAnimator bagAnimator = ObjectAnimator.ofFloat(mIvFace,"alpha",0,1).setDuration(3000);
        bagAnimator.setInterpolator(new LinearInterpolator());
        ObjectAnimator squareAnimator = ObjectAnimator.ofFloat(mIvExtra,"alpha",0,1).setDuration(3000);
        squareAnimator.setInterpolator(new LinearInterpolator());
        AnimatorSet set = new AnimatorSet();
        set.play(cardAnimator).with(bagAnimator);
        set.play(bagAnimator).with(squareAnimator);
        set.start();
    }

    private void soakout(){//soakout
        ObjectAnimator cardAnimator = ObjectAnimator.ofFloat(mIvPose,"alpha",1,0).setDuration(3000);
        cardAnimator.setInterpolator(new LinearInterpolator());
        ObjectAnimator bagAnimator = ObjectAnimator.ofFloat(mIvFace,"alpha",1,0).setDuration(3000);
        bagAnimator.setInterpolator(new LinearInterpolator());
        ObjectAnimator squareAnimator = ObjectAnimator.ofFloat(mIvExtra,"alpha",1,0).setDuration(3000);
        squareAnimator.setInterpolator(new LinearInterpolator());
        AnimatorSet set = new AnimatorSet();
        set.play(cardAnimator).with(bagAnimator);
        set.play(bagAnimator).with(squareAnimator);
        set.start();
    }

    private void normal(){
        mIvPose.setAlpha(1.0f);
        mIvFace.setAlpha(1.0f);
        mIvExtra.setAlpha(1.0f);
        if(mIsOut){
            mIvPose.setTranslationX(0);
            mIvFace.setTranslationX(0);
            mIvExtra.setTranslationX(0);
        }
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

    @Override
    public void onFailure(int code, String msg) {

    }

    @Override
    protected void onDestroy() {
        TextAppearOneControl.getInstance().release();
        if(notDown != null){
            for(String str : notDown.keySet()){
                if(isError){
                    FileUtil.deleteFile(str);
                }
                FileUtil.deleteFile(str.substring(0,str.lastIndexOf(".")));
            }
        }
        if(mPresenter != null) mPresenter.release();
        if(mBgmPlayer != null){
            mBgmPlayer.release();
            mBgmPlayer = null;
        }
        if(mVolPlayer != null){
            mVolPlayer.release();
            mVolPlayer = null;
        }
        if(mDownloadDisposable != null){
            if(!mDownloadDisposable.isDisposed()){
                mDownloadDisposable.dispose();
            }
        }
        if(mdecrptDisposable != null){
            if(!mdecrptDisposable.isDisposed()){
                mdecrptDisposable.dispose();
            }
        }
        super.onDestroy();
    }

    @Override
    public void onDoneSuccess(long time) {
        if(3 != JuQingUtil.getLevel(mId)){
            JuQingUtil.saveJuQingDone(mId,time);
        }else {
            JuQingUtil.saveJuQingNormal(new JuQingNormalEvent(mId));
        }
        EventBus.getDefault().post(new EventDoneEvent("map",""));
        finish();
    }
}
