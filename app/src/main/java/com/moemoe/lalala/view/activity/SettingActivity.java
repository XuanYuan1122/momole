package com.moemoe.lalala.view.activity;

import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerSettingComponent;
import com.moemoe.lalala.di.modules.SettingModule;
import com.moemoe.lalala.dialog.AlertDialog;
import com.moemoe.lalala.greendao.gen.ChatContentDbEntityDao;
import com.moemoe.lalala.greendao.gen.ChatUserEntityDao;
import com.moemoe.lalala.greendao.gen.GroupUserEntityDao;
import com.moemoe.lalala.greendao.gen.PrivateMessageItemEntityDao;
import com.moemoe.lalala.model.entity.AppUpdateEntity;
import com.moemoe.lalala.presenter.SettingContract;
import com.moemoe.lalala.presenter.SettingPresenter;
import com.moemoe.lalala.utils.AlertDialogUtil;
import com.moemoe.lalala.utils.CommonLoadingTask;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.GreenDaoManager;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StringUtils;

import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.wechat.friends.Wechat;

/**
 * Created by yi on 2016/12/1.
 */

public class SettingActivity extends BaseAppCompatActivity implements View.OnClickListener,SettingContract.View{

    public static final int REQUEST_SETTING_LOGIN = 3000;
    public static final int REQUEST_SETTING_LOGOUT = 3100;
    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_toolbar_title)
    TextView mTvTitle;
    private Handler mHander;

    private boolean mIsLogin;
    @Inject
    SettingPresenter mPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_setting;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mHander = new Handler();
        DaggerSettingComponent.builder()
                .settingModule(new SettingModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mTvTitle.setText(R.string.label_setting);
        mIsLogin = PreferenceUtils.isLogin(this);
        if(mIsLogin){
            ViewStub personViewStub = (ViewStub) findViewById(R.id.stub_set_person);
            View personSettingView = personViewStub.inflate();
            initPersonSetting(personSettingView);
        }
        ViewStub systemViewStub = (ViewStub) findViewById(R.id.stub_set_system);
        View systemSettingView = systemViewStub.inflate();
        initSyetemSetting(systemSettingView);

        TextView logTv = (TextView) findViewById(R.id.set_log_out);
        logTv.setOnClickListener(this);
        if(mIsLogin){
            logTv.setText(R.string.label_log_out);
        } else {
            logTv.setText(R.string.label_login);
            logTv.setBackgroundResource(R.drawable.bg_setting_login);
            logTv.setTextColor(ContextCompat.getColor(this,R.color.txt_white_can_disable));
        }
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    protected void onDestroy() {
        mPresenter.release();
        super.onDestroy();
    }

    private void initPersonSetting(View parent){
        initStyleView(parent, R.id.set_change_psw);
    }

    private void initSyetemSetting(View parent){
        initStyleView(parent, R.id.set_3g_pic);
        initStyleView(parent, R.id.set_label);
        initStyleView(parent, R.id.set_version_update);
        initStyleView(parent, R.id.set_clear_cache);
        initStyleView(parent, R.id.set_about);
    }

    private void initStyleView(View parent, int resId){
        View v = parent.findViewById(resId);
        v.setOnClickListener(this);
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
        } else if(id == R.id.set_label){
            funNameTv.setText(R.string.label_simple_label);
            funIndicateIv.setImageResource(R.drawable.select_btn_3g);
            funIndicateIv.setSelected(PreferenceUtils.getSimpleLabel(this));
        }else if(id == R.id.set_version_update){
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
    protected void initListeners() {
        mIvBack.setVisibility(View.VISIBLE);
        mIvBack.setImageResource(R.drawable.btn_back_black_normal);
        mIvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.set_select_deskmate){
            selectActor();
        } else if(id == R.id.set_change_psw){
            Intent intent = new Intent(this, ChangePasswordActivity.class);
            intent.putExtra(PhoneStateCheckActivity.EXTRA_ACTION, PhoneStateCheckActivity.ACTION_CHAGE_PASSWORD);
            startActivity(intent);
        } else if(id == R.id.set_3g_pic){
            ImageView funIndicateIv = getFunIndicateIv(SettingActivity.this.findViewById(R.id.set_3g_pic));
            funIndicateIv.setSelected(!funIndicateIv.isSelected());
            AppSetting.IS_DOWNLOAD_LOW_IN_3G = funIndicateIv.isSelected();
            PreferenceUtils.setLowIn3G(SettingActivity.this,AppSetting.IS_DOWNLOAD_LOW_IN_3G);
        }else if(id == R.id.set_label){
            ImageView funIndicateIv = getFunIndicateIv(SettingActivity.this.findViewById(R.id.set_label));
            funIndicateIv.setSelected(!funIndicateIv.isSelected());
            AppSetting.SUB_TAG = funIndicateIv.isSelected();
            PreferenceUtils.setSimpleLabel(SettingActivity.this,funIndicateIv.isSelected());
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
        }
    }

    private void checkVersion(){
        if(NetworkUtils.isNetworkAvailable(this) && NetworkUtils.isWifi(this)){
            mPresenter.checkVersion();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_SETTING_LOGIN){
            finish();
        }
    }

    /**
     * 登出
     */
    private void logout(){
        mPresenter.logout();
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
                long size = FileUtil.getCacheSize(SettingActivity.this);
                if(size == 0){
                } else {
                    clearCacheTv.setText(StringUtils.getFileSizeString(size));
                    clearCacheTv.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * 清理缓存
     */
    private void clearCache(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.a_dlg_title);
        builder.setMessage(R.string.a_dlg_clear_cache);
        builder.setPositiveButton(R.string.label_clear, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                new CommonLoadingTask(SettingActivity.this, new CommonLoadingTask.TaskCallback() {

                    @Override
                    public Object processDataInBackground() {
                        return FileUtil.clearAllCache(SettingActivity.this);
                    }

                    @Override
                    public void handleData(Object o) {
                        TextView funDetailTv = getFunDetailTv(SettingActivity.this.findViewById(R.id.set_clear_cache));
                        funDetailTv.setText("");
                        funDetailTv.setVisibility(View.INVISIBLE);
                        showToast(R.string.msg_clear_cache_succ_with_size);
                    }

                }, null).execute();
            }
        });
        builder.setNegativeButton(R.string.label_cancel, null);
        try {
            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

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
                    MapActivity.updateApkName = "neta-update" + new Date().getTime() + ".apk";
                    request.setDestinationInExternalFilesDir(SettingActivity.this, null, MapActivity.updateApkName);
                    MapActivity.mUpdateDownloadId = downloadManager.enqueue(request);
                } catch (Throwable t) {
                    t.printStackTrace();
                    showToast(R.string.label_error_storage);
                }
            }
        });
        alertDialogUtil.showDialog();
    }

    @Override
    public void logoutSuccess() {
        //清除数据库相关私信信息
        //私信列表
        try {
            if(isThirdParty(PreferenceUtils.getAuthorInfo().getPlatform())){
                Platform p = ShareSDK.getPlatform(PreferenceUtils.getAuthorInfo().getPlatform());
                if(p.isAuthValid()){
                    p.removeAccount();
                }
            }
            PreferenceUtils.clearAuthorInfo();
            PrivateMessageItemEntityDao privateMessageItemEntityDao = GreenDaoManager.getInstance().getSession().getPrivateMessageItemEntityDao();
            privateMessageItemEntityDao.deleteAll();
            ChatContentDbEntityDao chatContentDbEntityDao = GreenDaoManager.getInstance().getSession().getChatContentDbEntityDao();
            chatContentDbEntityDao.deleteAll();
            GroupUserEntityDao groupUserEntityDao = GreenDaoManager.getInstance().getSession().getGroupUserEntityDao();
            groupUserEntityDao.deleteAll();
            ChatUserEntityDao chatUserEntityDao = GreenDaoManager.getInstance().getSession().getChatUserEntityDao();
            chatUserEntityDao.deleteAll();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            setResult(REQUEST_SETTING_LOGOUT);
            finish();
        }
    }

    public boolean isThirdParty(String platform){
        return platform != null && (platform.equals(cn.sharesdk.tencent.qq.QQ.NAME) || platform.equals(Wechat.NAME) || platform.equals(SinaWeibo.NAME));
    }

    @Override
    public void modifySecretFail(int type) {

    }

    @Override
    public void noUpdate() {
        showToast("已经是最新版本了");
    }

    @Override
    public void shieldUserFail() {

    }

    @Override
    public void onFailure(int code,String msg) {
        ErrorCodeUtils.showErrorMsgByCode(SettingActivity.this,code,msg);
    }
}
