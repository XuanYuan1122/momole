package com.moemoe.lalala.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.igexin.sdk.PushManager;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerLoginComponent;
import com.moemoe.lalala.di.modules.LoginModule;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.AuthorInfo;
import com.moemoe.lalala.model.entity.LoginEntity;
import com.moemoe.lalala.model.entity.LoginResultEntity;
import com.moemoe.lalala.presenter.LoginContract;
import com.moemoe.lalala.presenter.LoginPresenter;
import com.moemoe.lalala.utils.AndroidBug5497Workaround;
import com.moemoe.lalala.utils.CountryCode;
import com.moemoe.lalala.utils.EncoderUtils;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.PhoneUtil;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.SoftKeyboardUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.widget.view.KeyboardListenerLayout;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.wechat.friends.Wechat;

/**
 * Created by yi on 2016/12/1.
 */

public class LoginActivity extends BaseAppCompatActivity implements LoginContract.View{
    private final String TAG = "LoginActivity";
    public static final String EXTRA_KEY_FIRST_RUN = "first_run";
    public static final String EXTRA_KEY_SETTING = "setting";
    public static final int RESPONSE_LOGIN_SUCCESS = 3001;

    @BindView(R.id.edt_account_name)
    AutoCompleteTextView mEdtAccount;
    @BindView(R.id.edt_password)
    EditText mEdtPassword;
    @BindView(R.id.tv_password_format)
    View mTvPasswordFormat;
    @BindView(R.id.tv_login)
    View mTvLogin;
    @BindView(R.id.tv_go_to_main)
    View mTvGo2Main;
    @BindView(R.id.tv_country_code)
    TextView mTvCountry;
    @BindView(R.id.rl_sns_pack)
    KeyboardListenerLayout mKeyLayout;
    @BindView(R.id.ll_sns_login_pack)
    View mLlSnsPack;
    @BindView(R.id.iv_login_weibo)
    View mIvWeibom;
    @BindView(R.id.iv_login_wechat)
    View mIvWechat;
    @BindView(R.id.iv_login_qq)
    View IvQQ;
    @BindView(R.id.tv_register)
    View mTvRegister;
    @BindView(R.id.tv_forget_password)
    View mTvForget;
    @BindView(R.id.iv_pwd_img)
    ImageView mIvPwd;
    @Inject
    LoginPresenter mPresenter;

    private String mAccount;
    private String mPassword;
    private String mCountryCode;
    private boolean mShouldGoMain = false;
    
    @Override
    protected int getLayoutId() {
        return R.layout.ac_login;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ShareSDK.initSDK(this);
        AndroidBug5497Workaround.assistActivity(this);
        DaggerLoginComponent.builder()
                .loginModule(new LoginModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        boolean mIsFirstRun = false;
        if(getIntent() != null){
            mIsFirstRun = getIntent().getBooleanExtra(EXTRA_KEY_FIRST_RUN, false);
            mShouldGoMain = getIntent().getBooleanExtra(EXTRA_KEY_SETTING,false);
        }
        mTvGo2Main.setVisibility(mIsFirstRun ? View.VISIBLE : View.GONE);
        if(!TextUtils.isEmpty(mAccount)){
            mEdtAccount.setText(mAccount);
        }
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    protected void initListeners() {
        mEdtAccount.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mEdtPassword.requestFocus();
            }
        });
        mEdtAccount.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mAccount = mEdtAccount.getText().toString().trim();
                mTvLogin.setEnabled(!TextUtils.isEmpty(mAccount) && !TextUtils.isEmpty(mPassword));
            }
        });
        mEdtPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    mIvPwd.setSelected(true);
                }else {
                    mIvPwd.setSelected(false);
                }
            }
        });
        mEdtPassword.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mPassword = mEdtPassword.getText().toString().trim();
                mTvLogin.setEnabled(!TextUtils.isEmpty(mAccount) && !TextUtils.isEmpty(mPassword));
            }
        });
        mKeyLayout.setOnkbdStateListener(new KeyboardListenerLayout.onKeyboardChangeListener() {

            @Override
            public void onKeyBoardStateChange(int state) {
                if (state == KeyboardListenerLayout.KEYBOARD_STATE_SHOW) {
                    mLlSnsPack.setVisibility(View.GONE);
                } else if (state == KeyboardListenerLayout.KEYBOARD_STATE_HIDE) {
                    mLlSnsPack.setVisibility(View.VISIBLE);
                }
            }
        });
        initPhoneCountry();
        mEdtAccount.setText(mAccount);
        mTvLogin.setEnabled(!TextUtils.isEmpty(mAccount) && !TextUtils.isEmpty(mPassword));
    }

    @OnClick({R.id.tv_login,R.id.tv_register,R.id.tv_forget_password,R.id.tv_go_to_main,R.id.tv_country_code,R.id.iv_login_qq,R.id.iv_login_wechat,R.id.iv_login_weibo})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.tv_login:
                if (NetworkUtils.isNetworkAvailable(LoginActivity.this)) {
                    doLogin();
                }else {
                    showToast(R.string.msg_connection);
                }
                break;
            case R.id.tv_register:
                Intent intent = new Intent(this, PhoneRegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_forget_password:
                Intent intent1 = new Intent(this, FindPasswordActivity.class);
                startActivity(intent1);
                break;
            case R.id.tv_go_to_main:
                Intent intent2 = new Intent(this, MapActivity.class);
                startActivity(intent2);
                finish();
                break;
            case R.id.tv_country_code:
                showPhoneCountryDialog();
                break;
            case R.id.iv_login_qq:
                createDialog(getResources().getString(R.string.msg_on_login));
                mPresenter.loginThird(cn.sharesdk.tencent.qq.QQ.NAME,PushManager.getInstance().getClientid(this) + "@and");
                break;
            case R.id.iv_login_wechat:
                createDialog(getResources().getString(R.string.msg_on_login));
                mPresenter.loginThird(Wechat.NAME,PushManager.getInstance().getClientid(this) + "@and");
                break;
            case R.id.iv_login_weibo:
                createDialog(getResources().getString(R.string.msg_on_login));
                mPresenter.loginThird(SinaWeibo.NAME,PushManager.getInstance().getClientid(this) + "@and");
                break;
        }
    }

    private void doLogin(){
        mTvPasswordFormat.setVisibility(View.GONE);
        mAccount = "+" + mCountryCode + mEdtAccount.getText().toString();
        mPassword = mEdtPassword.getText().toString();
        if (!StringUtils.isEmailFormated(mAccount) && !PhoneUtil.isPhoneFormated(mEdtAccount.getText().toString(), mCountryCode)) {
            showToast( R.string.msg_login_username_form_error);
        } else if (!StringUtils.isLegalPassword(mPassword)) {
            showToast(R.string.msg_login_password_form_error);
            // 密码格式错误
            mEdtPassword.setText("");	// 清空密码
            mPassword = "";
        } else {
            // 密码，邮箱 初步格式检查正确,调用网络线程开始登陆
            SoftKeyboardUtils.dismissSoftKeyboard(LoginActivity.this);
            if(NetworkUtils.checkNetworkAndShowError(this)){
                createDialog(getResources().getString(R.string.msg_on_login));
                LoginEntity bean = new LoginEntity(mAccount,EncoderUtils.MD5(mPassword), PushManager.getInstance().getClientid(this) + "@and");
                mPresenter.login(bean);
            }
        }
    }

    private void showPhoneCountryDialog(){
        PhoneRegisterActivity.PhoneCountryDialogFragment dialogFragment = new PhoneRegisterActivity.PhoneCountryDialogFragment();
        dialogFragment.setOnCountryCodeSelectListener(new PhoneRegisterActivity.CountryCodeSelectListener()
        {
            @Override
            public void onItemSelected(CountryCode countryCode) {
                mCountryCode = countryCode.getCode();
                String code = "+" + mCountryCode;
                mTvCountry.setText(code);
            }
        });
        dialogFragment.show(getSupportFragmentManager(), TAG +" CountryCode");
    }

    private void initPhoneCountry(){
        mCountryCode = PhoneUtil.getLocalCountryCode(this);
        String code = "+" + mCountryCode;
        mTvCountry.setText(code);
    }


    @Override
    protected void initData() {

    }

    @Override
    public void onLoginSuccess(LoginResultEntity entity) {
        finalizeDialog();
        AuthorInfo authorInfo = new AuthorInfo();
        authorInfo.setPhone(mAccount);
        authorInfo.setPassword(EncoderUtils.MD5(mPassword));
        authorInfo.setToken(entity.getToken());
        authorInfo.setUserId(entity.getUserId());
        if(!entity.getHeadPath().startsWith("http")){
            authorInfo.setHeadPath(ApiService.URL_QINIU + entity.getHeadPath());
        }else {
            authorInfo.setHeadPath(entity.getHeadPath());
        }
        authorInfo.setCoin(entity.getCoin());
        authorInfo.setUserName(entity.getUserName());
        authorInfo.setLevel(entity.getLevel());
        authorInfo.setOpenBag(entity.isOpenBag());
        authorInfo.setPlatform("neta");
        finalizeDialog();
        PreferenceUtils.setAuthorInfo(authorInfo);
        showToast(R.string.msg_login_success);
        setResult(RESPONSE_LOGIN_SUCCESS);
        if (mShouldGoMain) {
            Intent i = new Intent(LoginActivity.this, MapActivity.class);
            startActivity(i);
        }
        finish();
    }

    @Override
    public void onLoginThirdSuccess(String id) {
        finalizeDialog();
        showToast(R.string.msg_login_success);
        setResult(RESPONSE_LOGIN_SUCCESS);
        if (mShouldGoMain){
            Intent i = new Intent(LoginActivity.this,MapActivity.class);
            startActivity(i);
        }
        setResult(RESPONSE_LOGIN_SUCCESS);
        finish();
    }

    @Override
    public void onFailure(int code,String msg) {
        finalizeDialog();
        ErrorCodeUtils.showErrorMsgByCode(LoginActivity.this,code,msg);
    }

    @Override
    protected void onDestroy() {
        mPresenter.release();
        super.onDestroy();
    }
}
