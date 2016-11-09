package com.moemoe.lalala;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.Utils;
import com.app.annotation.ContentView;
import com.app.annotation.FindView;
import com.moemoe.lalala.app.AlertDialog;
import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.data.AppUpdateInfo;
import com.moemoe.lalala.network.CallbackFactory;
import com.moemoe.lalala.network.OnNetWorkCallback;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.utils.CommonLoadingTask;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.ToastUtil;
import com.moemoe.lalala.view.NoDoubleClickListener;

/**
 * Created by Haru on 2016/5/1 0001.
 */
@ContentView(R.layout.ac_setting)
public class SettingActivity extends BaseActivity implements View.OnClickListener{

    public static final int REQUEST_SETTING_LOGIN = 3000;
    public static final int REQUEST_SETTING_LOGOUT = 3100;
    @FindView(R.id.toolbar)
    private Toolbar mToolbar;
    @FindView(R.id.tv_toolbar_title)
    private TextView mTvTitle;
    private SettingActivity mActivity;
    private Handler mHander;

    /**
     * 是否登录完成
     */
    private boolean mIsLogin;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            // 透明状态栏
//            getWindow().addFlags(
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            // 透明导航栏
//            getWindow().addFlags(
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            SystemBarTintManager tintManager = new SystemBarTintManager(this);
//            tintManager.setStatusBarTintEnabled(true);
//            // 设置状态栏的颜色
//            tintManager.setStatusBarTintResource(R.color.main_title_cyan);
//            getWindow().getDecorView().setFitsSystemWindows(true);
//        }
//    }

    @Override
    protected void initView() {
        mActivity = this;
        mHander = new Handler();
        mToolbar.setNavigationOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mTvTitle.setText(R.string.label_setting);
        mIsLogin = mPreferMng.isLogin(this);
        if(mIsLogin){
            ViewStub personViewStub = (ViewStub) findViewById(R.id.stub_set_person);
            View personSettingView = personViewStub.inflate();
            personViewStub = null;
            initPersonSetting(personSettingView);
        } else {

        }
        ViewStub systemViewStub = (ViewStub) findViewById(R.id.stub_set_system);
        View systemSettingView = systemViewStub.inflate();
        systemViewStub = null;
        initSyetemSetting(systemSettingView);

        TextView logTv = (TextView) findViewById(R.id.set_log_out);
        logTv.setOnClickListener(this);
        if(mIsLogin){
            logTv.setText(R.string.label_log_out);
        } else {
            logTv.setText(R.string.label_login);
            logTv.setBackgroundResource(R.drawable.bg_setting_login);
            logTv.setTextColor(getResources().getColor(R.color.txt_white_can_disable));
        }

        View tvAddRecommand = findViewById(R.id.tv_add_recommand_doc);
        if (AppSetting.IS_EDITOR_VERSION) {
            tvAddRecommand.setVisibility(View.VISIBLE);
            tvAddRecommand.setOnClickListener(this);
        } else {
            tvAddRecommand.setVisibility(View.GONE);
        }
    }

    private void initPersonSetting(View parent){
       // initStyleView(parent, R.id.set_select_deskmate);
        initStyleView(parent, R.id.set_change_psw);
    }

    private void initSyetemSetting(View parent){
        initStyleView(parent, R.id.set_3g_pic);
        initStyleView(parent, R.id.set_version_update);
        initStyleView(parent, R.id.set_clear_cache);
        initStyleView(parent, R.id.set_about);
    }

    private void initStyleView(View parent, int resId){
        View v = parent.findViewById(resId);
        v.setOnClickListener(mActivity);
        TextView funNameTv = getFunNameTv(v);
        TextView funDetailTv = getFunDetailTv(v);
        ImageView funIndicateIv = getFunIndicateIv(v);
        int id = v.getId();
        if(id == R.id.set_select_deskmate){
            funNameTv.setText(R.string.label_select_deskmate);
        } else if(id == R.id.set_change_psw){
            funNameTv.setText(R.string.label_change_password);
        } else if(id == R.id.set_3g_pic){
            funNameTv.setText(R.string.label_3g_pic);
            funIndicateIv.setImageResource(R.drawable.select_btn_3g);
            funIndicateIv.setSelected(AppSetting.IS_DOWNLOAD_LOW_IN_3G);
        } else if(id == R.id.set_version_update){
            funNameTv.setText(R.string.label_version_update);
        } else if(id == R.id.set_clear_cache){
            funNameTv.setText(R.string.label_clear_cache);
            initClearCacheDetailTv(funDetailTv);
        } else if(id == R.id.set_about){
            funNameTv.setText(R.string.label_about);
        }
    }

    private TextView getFunNameTv(View v){
        return (TextView) v.findViewById(R.id.tv_function_name);
    }

    private TextView getFunDetailTv(View v){
        return (TextView) v.findViewById(R.id.tv_function_detail);
    }

    private ImageView getFunIndicateIv(View v){
        return (ImageView) v.findViewById(R.id.iv_indicate_img);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.set_select_deskmate){
            selectActor();
        } else if(id == R.id.set_change_psw){
            Intent intent = new Intent(this, ChangePasswordActivity.class);
            intent.putExtra(PhoneStateCheckActivity.EXTRA_ACTION, PhoneStateCheckActivity.ACTION_CHAGE_PASSWORD);
            startActivity(intent);
        } else if(id == R.id.set_3g_pic){
            ImageView funIndicateIv = getFunIndicateIv(mActivity.findViewById(R.id.set_3g_pic));
            funIndicateIv.setSelected(!funIndicateIv.isSelected());
            AppSetting.IS_DOWNLOAD_LOW_IN_3G = funIndicateIv.isSelected();
            mPreferMng.setLowIn3G(AppSetting.IS_DOWNLOAD_LOW_IN_3G);
        } else if(id == R.id.set_version_update){
            checkVersion();
        } else if(id == R.id.set_clear_cache){
            clearCache();
        } else if(id == R.id.set_about){
            Intent intent = new Intent(this,  AboutActivity.class);
            startActivity(intent);
        } else if(id == R.id.set_log_out){
            if(mIsLogin){
                logout();
            } else {
                Intent i = new Intent(this, LoginActivity.class);
                i.putExtra(LoginActivity.EXTRA_KEY_SETTING,false);
                startActivityForResult(i, REQUEST_SETTING_LOGIN);
            }
        } else if (id == R.id.tv_add_recommand_doc) {
            addRecommandDoc();
        }
    }

    private void checkVersion(){
        if(NetworkUtils.isNetworkAvailable(this) && NetworkUtils.isWifi(this)){
            Otaku.getCommonV2().checkVersion().enqueue(CallbackFactory.getInstance().callback1(new OnNetWorkCallback<String, AppUpdateInfo>() {
                @Override
                public void success(String token, AppUpdateInfo s) {
                    if (s.getUpdateStatus() != 0) {
                        showUpdateDialog(s);
                    } else {
                        ToastUtil.showCenterToast(SettingActivity.this,R.string.label_last_version);
                    }
                }

                @Override
                public void failure(int code,String e) {

                }
            }));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_SETTING_LOGIN){
            finish();
        }
    }

    private void addRecommandDoc() {
        // 添加推荐doc
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("添加推荐帖子");
        final EditText editText = (EditText) LayoutInflater.from(mActivity).inflate(R.layout.c_only_edittext, null);
        editText.setHint("请输入推荐帖子UUID集合，以英文,附分隔;全删全插方式");
        builder.setView(editText);

        builder.setPositiveButton(R.string.a_dlg_ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String uuids = editText.getText().toString();
                if (!TextUtils.isEmpty(uuids)) {
                    addRecommandDocAPI(uuids);
                } else {
                    ToastUtil.showToast(mActivity, "没有输入uuid，无效");
                }
            }
        });

        builder.create().show();
    }

    private void addRecommandDocAPI(String uuids) {
        if (!TextUtils.isEmpty(uuids)) {
            String[] res = uuids.split(",");
            if (res != null && res.length > 0) {
                Otaku.getCommonV2().modifyRecommandDoc(mPreferMng.getToken(), res).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
                    @Override
                    public void success(String token, String s) {
                        ToastUtil.showToast(mActivity, "修改推荐帖子池成功");
                    }

                    @Override
                    public void failure(String e) {

                    }
                }));
            }
        }
    }

    /**
     * 登出
     */
    private void logout(){
        Otaku.getAccountV2().logout(this).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
            @Override
            public void success(String token, String s) {
                MapActivity.sChangeLogin = true;
            }

            @Override
            public void failure(String e) {

            }
        }));
        setResult(REQUEST_SETTING_LOGOUT);
        finish();
    }

    /**
     * 选择同桌
     */
    private void selectActor(){
    }
    /**
     * 获取缓存总大小
     */
    private void initClearCacheDetailTv(final TextView clearCacheTv){
        mHander.post(new Runnable() {
            @Override
            public void run() {
               // long allPictureSize = FileUtil.getAllPictureSize(mActivity);
                //if (allPictureSize == 0) {
               // } else {
                    //clearCacheTv.setText(StringUtils.getFileSizeString(allPictureSize));
                    //clearCacheTv.setVisibility(View.VISIBLE);
               // }
            }
        });
    }

    /**
     * 清理缓存
     */
    private void clearCache(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(R.string.a_dlg_title);
        builder.setMessage(R.string.a_dlg_clear_cache);
        builder.setPositiveButton(R.string.label_clear, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                new CommonLoadingTask(mActivity, new CommonLoadingTask.TaskCallback() {

                    @Override
                    public void processDataInBackground() {
                        Utils.image().clearCacheFiles();
                    }

                    @Override
                    public void handleData() {
                        ToastUtil.showToast(SettingActivity.this,R.string.msg_clear_cache_succ_with_size);
                    }

                }, null).execute();
            }
        });
        builder.setNegativeButton(R.string.a_dlg_cancel, null);
        try {
            builder.show();
        } catch (Exception e) {
        }

    }
}
