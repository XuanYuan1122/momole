package com.moemoe.lalala.view.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerLive2dNoramlComponent;
import com.moemoe.lalala.di.modules.Live2dModule;
import com.moemoe.lalala.di.modules.Live2dNormalModule;
import com.moemoe.lalala.galgame.FileManager;
import com.moemoe.lalala.galgame.Live2DDefine;
import com.moemoe.lalala.galgame.Live2DManager;
import com.moemoe.lalala.galgame.Live2DView;
import com.moemoe.lalala.galgame.SoundManager;
import com.moemoe.lalala.presenter.LIve2dNormalContract;
import com.moemoe.lalala.presenter.Live2dNoramalPresenter;
import com.moemoe.lalala.utils.AlertDialogUtil;
import com.moemoe.lalala.utils.ArchiverManager;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.IArchiverListener;
import com.moemoe.lalala.utils.StorageUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 *
 * Created by yi on 2017/11/2.
 */

public class Live2dNormalActivity extends BaseAppCompatActivity implements LIve2dNormalContract.View{

    @BindView(R.id.container)
    FrameLayout containerRoot;
    @BindView(R.id.iv_sound_load)
    ImageView mIvSoundLoad;
    @BindView(R.id.iv_download)
    ImageView mUnzipBg;
    @BindView(R.id.ll_progress)
    View mProgressRoot;
    @BindView(R.id.tv_progress)
    TextView mTvProgress;
    @BindView(R.id.progress)
    ProgressBar mProgress;
    @BindView(R.id.tv_score)
    TextView mTvScore;

    @Inject
    Live2dNoramalPresenter mPresenter;

    private Live2DManager live2DMgr;
    private Live2DView mLive2dView;
    private ObjectAnimator mSoundLoadAnim;
    private String id;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_live2d_normal;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerLive2dNoramlComponent.builder()
                .live2dNormalModule(new Live2dNormalModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        id = getIntent().getStringExtra(UUID);
        boolean show = getIntent().getBooleanExtra("show_ping_fen",true);
        if(show){
            mTvScore.setVisibility(View.VISIBLE);
        }else {
            mTvScore.setVisibility(View.GONE);
        }
        if(TextUtils.isEmpty(id)){
            finish();
        }
        SoundManager.init(this);
        FileManager.init(this);

        mUnzipBg.setVisibility(View.VISIBLE);
        mProgressRoot.setVisibility(View.VISIBLE);
        if(FileUtil.isExists(StorageUtils.getLive2dRootPath() + id)){
            FileUtil.deleteDir(StorageUtils.getLive2dRootPath() + id);
        }

        ArchiverManager.getInstance().doUnArchiver(StorageUtils.getLive2dRootPath() + id + ".zip", StorageUtils.getLive2dRootPath() + id, "", new IArchiverListener() {
            @Override
            public void onStartArchiver() {

            }

            @Override
            public void onProgressArchiver(int current, int total) {
                mProgress.setProgress(current + 1);
                mProgress.setMax(total);
                mTvProgress.setText("正在加载资源 " + current + "/" + total);
            }

            @Override
            public void onEndArchiver() {

            }

            @Override
            public void onFail(String msg) {
                showToast("资源加载失败,请重试");
                FileUtil.deleteDir(StorageUtils.getLive2dRootPath() + id);
                finish();
            }

            @Override
            public void onComplete() {
                mUnzipBg.setVisibility(View.GONE);
                mProgressRoot.setVisibility(View.GONE);
                init();
            }
        });
    }

    private void init(){
        String model = StorageUtils.getLive2dRootPath() + id + "/" + Live2DDefine.MODEL_DEFAULT;
        live2DMgr = new Live2DManager(model,true);
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
                if(!isFinishing())
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mSoundLoadAnim != null) {
                                mSoundLoadAnim.end();
                                mSoundLoadAnim = null;
                            }
                            if(mIvSoundLoad != null) mIvSoundLoad.setVisibility(View.GONE);
                        }
                    });
            }
        });
        mLive2dView = live2DMgr.createView(this);
        containerRoot.addView(mLive2dView, 0, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    private void soundLoading(){
        mSoundLoadAnim = ObjectAnimator.ofFloat(mIvSoundLoad,"alpha",0.2f,1f).setDuration(300);
        mSoundLoadAnim.setInterpolator(new LinearInterpolator());
        mSoundLoadAnim.setRepeatMode(ValueAnimator.REVERSE);
        mSoundLoadAnim.setRepeatCount(ValueAnimator.INFINITE);
        mSoundLoadAnim.start();
    }

    @Override
    protected void onDestroy() {
        if(mPresenter != null) mPresenter.release();
        FileUtil.deleteDir(StorageUtils.getLive2dRootPath() + id);
        super.onDestroy();
        SoundManager.release();
        FileManager.release();
    }

    @OnClick({R.id.tv_score,R.id.rl_root_1,R.id.tv_bizhi})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.tv_score:
                final AlertDialogUtil alertDialogUtil = AlertDialogUtil.getInstance();
                alertDialogUtil.createPingFenDialog(Live2dNormalActivity.this,"提交");
                alertDialogUtil.setOnClickListener(new AlertDialogUtil.OnClickListener() {
                    @Override
                    public void CancelOnClick() {

                    }

                    @Override
                    public void ConfirmOnClick() {
                        alertDialogUtil.dismissDialog();
                        mPresenter.pingfenLive2d((int) alertDialogUtil.getScore(),id);
                    }
                });
                alertDialogUtil.showDialog();
                break;
            case R.id.rl_root_1:
                finish();
                break;
            case R.id.tv_bizhi:
                showToast("正在开发");
                break;
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
        showToast("操作失败");
    }

    @Override
    public void onSuccess() {
        showToast("操作成功");
    }
}
