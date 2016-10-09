package com.moemoe.lalala;

import android.Manifest;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.Utils;
import com.app.annotation.ContentView;
import com.app.annotation.FindView;
import com.app.common.Callback;
import com.app.view.DbManager;
import com.igexin.sdk.PushManager;
import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.data.AuthorInfo;
import com.moemoe.lalala.data.PersonBean;
import com.moemoe.lalala.network.CallbackFactory;
import com.moemoe.lalala.network.OnNetWorkCallback;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.thirdopen.ThirdPartySDKManager;
import com.moemoe.lalala.utils.EncoderUtils;
import com.moemoe.lalala.utils.IConstants;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.PermissionsChecker;
import com.moemoe.lalala.utils.PreferenceManager;
import com.moemoe.lalala.utils.StorageUtils;
import com.moemoe.lalala.utils.ToastUtil;
import com.moemoe.lalala.utils.UnCaughtException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.util.Calendar;

/**
 * Created by Haru on 2016/4/26 0026.
 */
@ContentView(R.layout.ac_splash)
public class SplashActivity extends BaseActivity {



    private static final int REQUEST_CODE = 0; // 请求码
    private String mSchema;

    // 所需的全部权限
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.GET_ACCOUNTS
    };


    @FindView(R.id.iv_start_pic)
    private ImageView mIvWelcome;
    private PermissionsChecker mPermissionsChecker; // 权限检测器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showDebugInfo();

    }

    private void goToMain(){
        Intent i = new Intent(this,MapActivity.class);
        if(!TextUtils.isEmpty(mSchema)){
            i.putExtra("schema",mSchema);
        }
        startActivity(i);
        finish();
    }

    private void goToGuide(){
        //Intent intent = new Intent(this, GalGameActivity.class);
        Intent intent = new Intent(this, GuideActivity.class);
        intent.putExtra(GuideActivity.EXTRA_HAVE_CHOSE,"true");
        intent.putExtra(GuideActivity.EXTRA_BY_USER,"false");
        startActivity(intent);
        finish();
    }

    @Override
    protected void initView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        InputStream is = getResources().openRawResource(R.raw.splash);
        mIvWelcome.setImageBitmap(BitmapFactory.decodeStream(is));
        mPermissionsChecker = new PermissionsChecker(this);
        // 缺少权限时, 进入权限配置页面
        if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
            startPermissionsActivity();
        }
 //       ThirdPartySDKManager.getInstance(this).init();
//        File cacheDirectory = getApplicationContext().getCacheDir();
//        if(AppSetting.isDebug){
//            Otaku.setup(IConstants.debugBaseUrl,cacheDirectory);
//        }else {
//            Otaku.setup(IConstants.baseUrl, cacheDirectory);
//        }
//        StorageUtils.initialStorageDir(this);
//        EncoderUtils.init(this);
//        IntentUtils.init(this);
//        AppSetting.initDeviceInfo(this);
//        PushManager.getInstance().initialize(this.getApplicationContext());
//        MoemoeApplication.sDaoConfig = new DbManager.DaoConfig()
//                .setDbName("netaInfo")
//                .setDbVersion(1)
//                .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
//                    @Override
//                    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
//
//                    }
//                });
        initPreferenceInfo();
        if(mIntent != null){
            mSchema = mIntent.getStringExtra("schema");
        }
        Calendar today = Calendar.getInstance();
        Calendar last = Calendar.getInstance();
        long lastTime = mPreferMng.getsLastLauncherTime();
        last.setTimeInMillis(lastTime);
        if (today.get(Calendar.YEAR) == last.get(Calendar.YEAR) && today.get(Calendar.MONTH) == last.get(Calendar.MONTH)
                && today.get(Calendar.DAY_OF_MONTH) == last.get(Calendar.DAY_OF_MONTH)) {
            AppSetting.isFirstLauncherToday = false;
        } else if (last.before(today)) {
            AppSetting.isFirstLauncherToday = true;
        }
        long lastTime1 = mPreferMng.getsLastEventTime();
        last.setTimeInMillis(lastTime1);
        if (today.get(Calendar.YEAR) == last.get(Calendar.YEAR) && today.get(Calendar.MONTH) == last.get(Calendar.MONTH)
                && today.get(Calendar.DAY_OF_MONTH) == last.get(Calendar.DAY_OF_MONTH)) {
            AppSetting.isEnterEventToday = true;
        }else {
            AppSetting.isEnterEventToday = false;
        }
        mPreferMng.setsLastLauncherTime(System.currentTimeMillis());
        AppSetting.IS_DOWNLOAD_LOW_IN_3G = mPreferMng.getLowIn3G();
        if(NetworkUtils.isNetworkAvailable(this)){
            tryLoginFirst(null);//自动登录
        }
        if(mPreferInfo.isAppFirstLaunch() || mPreferInfo.isVersion2FirstLaunch()){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //初始化首页数据
                    goToGuide();
                }
            }, 1000);
        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //初始化首页数据
                    goToMain();
                }
            }, 1000);
        }
    }

    private void initPreferenceInfo() {
        mPreferMng = PreferenceManager.getInstance(getApplicationContext());
        mPreferInfo = PreferenceManager.initPreferenceInfo();
    }

    private void showDebugInfo(){
        String info = "";
        if(AppSetting.isDebug){
            info += "DEBUG_";
        }
        if (!TextUtils.isEmpty(info)) {
            info += "VERSION";
            Toast.makeText(this, info, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {

    }


    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 拒绝时, 关闭页面, 缺少主要权限, 无法运行
        if (requestCode == REQUEST_CODE && resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
