package com.moemoe.lalala.view.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.igexin.sdk.PushManager;
import com.mob.MobSDK;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.app.AppStatusConstant;
import com.moemoe.lalala.app.AppStatusManager;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.broadcast.PushIntentService;
import com.moemoe.lalala.broadcast.PushService;
import com.moemoe.lalala.di.components.DaggerSimpleComponent;
import com.moemoe.lalala.di.modules.SimpleModule;
import com.moemoe.lalala.model.entity.SplashEntity;
import com.moemoe.lalala.presenter.SimpleContract;
import com.moemoe.lalala.presenter.SimplePresenter;
import com.moemoe.lalala.utils.EncoderUtils;
import com.moemoe.lalala.utils.GreenDaoManager;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StorageUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * 闪屏界面
 * Created by yi on 2016/11/27.
 */

public class SplashActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks,SimpleContract.View{

    @BindView(R.id.iv_splash)
    ImageView splashImg;
    @BindView(R.id.tv_skip)
    TextView mTvSkip;

    @Inject
    SimplePresenter mPresenter;
    private static final int PERMISSON_REQ = 1;
    //打开应用跳转页
    private String mSchema;
    /**
     * 需要进行检测的权限数组
     */
    protected String[] needPermissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AppStatusManager.getInstance().setAppStatus(AppStatusConstant.STATUS_NORMAL); //进入应用初始化设置成未登录状态
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_splash);
        ButterKnife.bind(SplashActivity.this);
        splashImg.setImageResource(R.drawable.splash);
    }

    @Override
    protected void onStart() {
        super.onStart();
        requestCodePermissions();
    }

    @Override
    public void onBackPressed() {
        //do nothing
    }

    private void goToMain(){
        Intent intent = getIntent();
        if(intent != null){
            mSchema = intent.getStringExtra("schema");
        }
        Intent i = new Intent(this,MapActivity.class);
        if(!TextUtils.isEmpty(mSchema)){
            i.putExtra("schema",mSchema);
        }
        startActivity(i);
        finish();
    }

    private void goToMengXin(){
        Intent intent = getIntent();
        if(intent != null){
            mSchema = intent.getStringExtra("schema");
        }
        Intent i = new Intent(this,MengXinActivity.class);
        if(!TextUtils.isEmpty(mSchema)){
            i.putExtra("schema",mSchema);
        }
        startActivity(i);
        finish();
    }

    private void init(){
        DaggerSimpleComponent.builder()
                .simpleModule(new SimpleModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        AppSetting.initDeviceInfo(getApplicationContext());
        StorageUtils.initialStorageDir(getApplicationContext());
        EncoderUtils.init(getApplicationContext());
        IntentUtils.init(getApplicationContext());
        MobSDK.init(this,"10dc41b1eb3b4","d342f319b6d67e143d36519a38b37f0e");

        PushManager.getInstance().initialize(this.getApplicationContext(), PushService.class);
        PushManager.getInstance().registerPushIntentService(getApplicationContext(), PushIntentService.class);
        Calendar today = Calendar.getInstance();
        Calendar last = Calendar.getInstance();

        long lastTime = PreferenceUtils.getsLastLauncherTime(this);
        last.setTimeInMillis(lastTime);
        if (today.get(Calendar.YEAR) == last.get(Calendar.YEAR) && today.get(Calendar.MONTH) == last.get(Calendar.MONTH)
                && today.get(Calendar.DAY_OF_MONTH) == last.get(Calendar.DAY_OF_MONTH)) {
            AppSetting.isFirstLauncherToday = false;
            AppSetting.isShowBackSchoolAll = PreferenceUtils.getAllBackSchool(this);
        } else if (last.before(today)) {
            AppSetting.isFirstLauncherToday = true;
            AppSetting.isShowBackSchoolAll = false;
            GreenDaoManager.getInstance().getSession().getJuQingNormalEventDao().deleteAll();
            PreferenceUtils.setAllBackSchool(this,false);
            PreferenceUtils.setBackSchoolDialog(this,false);
        }

        long lastTime1 = PreferenceUtils.getLastEventTime(this);
        last.setTimeInMillis(lastTime1);
        AppSetting.isEnterEventToday = today.get(Calendar.YEAR) == last.get(Calendar.YEAR) && today.get(Calendar.MONTH) == last.get(Calendar.MONTH)
                && today.get(Calendar.DAY_OF_MONTH) == last.get(Calendar.DAY_OF_MONTH);
        PreferenceUtils.setsLastLauncherTime(this,System.currentTimeMillis());

        AppSetting.IS_DOWNLOAD_LOW_IN_3G = PreferenceUtils.getLowIn3G(this);
        mPresenter.doRequest(null,10);
        if(NetworkUtils.isNetworkAvailable(this)){
            mPresenter.doRequest(PushManager.getInstance().getClientid(this) + "@and",4);
            if(PreferenceUtils.getHaveGameFuku(this)){
                mPresenter.doRequest("bh3rd",9);
            }
        }
        splashImg.setImageResource(R.drawable.splash);
        ArrayList<SplashEntity> entities = (ArrayList<SplashEntity>) GreenDaoManager.getInstance().getSession().getSplashEntityDao().loadAll();
        if(entities != null && entities.size() > 0){
            Collections.shuffle(entities);
            final SplashEntity entity = entities.get(0);
            mCurTime = entity.getShowSeconds();
            Glide.with(this)
                    .load(new File(StorageUtils.getSplashRootPath(),entity.getImagePath().substring(entity.getImagePath().lastIndexOf("/") + 1)))
                    .into(splashImg);
            splashImg.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    mHandler.removeCallbacks(timeRun);
                    Intent i = new Intent(SplashActivity.this,MapActivity.class);
                    i.putExtra("schema",entity.getTargetUrl());
                    startActivity(i);
                    finish();
                   // IntentUtils.toActivityFromUri(SplashActivity.this,Uri.parse(entity.getTargetUrl()),v);
                }
            });
            if(entity.getSkip()){
                mTvSkip.setVisibility(View.VISIBLE);
            }else {
                mTvSkip.setVisibility(View.GONE);
            }
            mTvSkip.setText("点击跳过  " + mCurTime);
            mTvSkip.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    if(PreferenceUtils.isAppFirstLaunch(SplashActivity.this)){
                        goToMengXin();
                    }else {
                        goToMain();
                    }
                }
            });
        }
        mHandler.post(timeRun);
    }

    private Handler mHandler = new Handler();
    private int mCurTime = 2;
    private Runnable timeRun = new Runnable() {
        @Override
        public void run() {
            if(mCurTime == 1){
                mCurTime--;
                mTvSkip.setText("点击跳过  " + mCurTime);
                mHandler.removeCallbacks(this);
                if(PreferenceUtils.isAppFirstLaunch(SplashActivity.this)){
                    goToMengXin();
                }else {
                    goToMain();
                }
            }else {
                mHandler.postDelayed(this,1000);
                mCurTime--;
                mTvSkip.setText("点击跳过  " + mCurTime);
            }
        }
    };

    @AfterPermissionGranted(PERMISSON_REQ)
    private void requestCodePermissions(){
        if(!EasyPermissions.hasPermissions(this,needPermissions)){
            EasyPermissions.requestPermissions(this,"应用需要这些权限",PERMISSON_REQ,needPermissions);
        }else {
            init();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        recreate();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        showMissingPermissionDialog();
    }

    private void showMissingPermissionDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("当前应用缺少必要权限。请点击\"设置\"-\"权限\"-打开所需权限。");
        //拒绝，退出应用
        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(SplashActivity.this,"“(´・ω・`)那个人好奇怪，不给权限还想找资源…”\n" +
                                "\n" +
                                "“就是说啊，也不知道怎么想的…눈_눈”",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
        builder.setPositiveButton("设置",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startAppSetting();
                    }
                });
        builder.setCancelable(false);
        builder.show();
    }

    private void startAppSetting(){
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    @Override
    public void onSuccess(Object o) {

    }

    @Override
    public void onFailure(int code,String msg) {

    }

    @Override
    protected void onDestroy() {
        if(mPresenter != null)mPresenter.release();
        super.onDestroy();
    }
}
