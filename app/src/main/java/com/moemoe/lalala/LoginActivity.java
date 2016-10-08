package com.moemoe.lalala;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.annotation.ContentView;
import com.app.annotation.FindView;
import com.app.common.Callback;
import com.app.http.request.UriRequest;
import com.moemoe.lalala.callback.MoeMoeCallback;
import com.moemoe.lalala.data.AuthorInfo;
import com.moemoe.lalala.network.CallbackFactory;
import com.moemoe.lalala.network.OnNetWorkCallback;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.thirdopen.ThirdPartySDKManager;
import com.moemoe.lalala.utils.CountryCode;
import com.moemoe.lalala.utils.EncoderUtils;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.PhoneUtil;
import com.moemoe.lalala.utils.PreferenceManager;
import com.moemoe.lalala.utils.SoftKeyboardUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ToastUtil;
import com.moemoe.lalala.view.KeyboardListenerLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Haru on 2016/4/28 0028.
 */
@ContentView(R.layout.ac_login)
public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "LoginActivity";
    public static final String EXTRA_KEY_FIRST_RUN = "first_run";
    public static final String EXTRA_KEY_SETTING = "setting";
    public static final int RESPONSE_LOGIN_SUCCESS = 3001;

    @FindView(R.id.edt_account_name)
    private AutoCompleteTextView mEdtAccount;
    @FindView(R.id.edt_password)
    private EditText mEdtPassword;
    @FindView(R.id.tv_password_format)
    private View mTvPasswordFormat;
    @FindView(R.id.tv_login)
    private View mTvLogin;
    @FindView(R.id.tv_go_to_main)
    private View mTvGo2Main;
    @FindView(R.id.tv_country_code)
    private TextView mTvCountry;
    @FindView(R.id.rl_account_pack)
    private KeyboardListenerLayout mKeyLayoutAccount;
    @FindView(R.id.rl_sns_pack)
    private KeyboardListenerLayout mKeyLayout;
    @FindView(R.id.ll_sns_login_pack)
    private View mLlSnsPack;
    @FindView(R.id.iv_login_weibo)
    private View mIvWeibom;
    @FindView(R.id.iv_login_wechat)
    private View mIvWechat;
    @FindView(R.id.iv_login_qq)
    private View IvQQ;
    @FindView(R.id.tv_register)
    private View mTvRegister;
    @FindView(R.id.tv_forget_password)
    private View mTvForget;
    @FindView(R.id.iv_pwd_img)
    private ImageView mIvPwd;

    private boolean mIsFirstRun;
    private String mAccount;
    private String mPassword;
    private String mCountryCode;
    private boolean mShouldGoMain = false;

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
//            tintManager.setStatusBarTintResource(R.color.black);
//            getWindow().getDecorView().setFitsSystemWindows(true);
//        }
//    }

    @Override
    protected void initView() {
        mIsFirstRun = mIntent.getBooleanExtra(EXTRA_KEY_FIRST_RUN, false);
        mShouldGoMain = mIntent.getBooleanExtra(EXTRA_KEY_SETTING,false);
        mTvGo2Main.setVisibility(mIsFirstRun ? View.VISIBLE : View.GONE);
        if(!TextUtils.isEmpty(mAccount)){
            mEdtAccount.setText(mAccount);
        }
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
        mTvLogin.setOnClickListener(this);
        mTvGo2Main.setOnClickListener(this);
        mTvRegister.setOnClickListener(this);
        mTvCountry.setOnClickListener(this);
        mTvForget.setOnClickListener(this);
        mIvWeibom.setOnClickListener(this);
        mIvWechat.setOnClickListener(this);
        IvQQ.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.tv_login){
            if (NetworkUtils.isNetworkAvailable(LoginActivity.this)) {
                doLogin();
            }else {
                ToastUtil.showCenterToast(LoginActivity.this, R.string.msg_server_connection);
            }
        }else if(id == R.id.tv_register){
            Intent intent = new Intent(this, PhoneRegisterActivity.class);
            startActivity(intent);
        }else if(id == R.id.tv_forget_password){
            Intent intent = new Intent(this, FindPasswordActivity.class);
            startActivity(intent);
        } else if (id == R.id.tv_go_to_main) {
            Intent intent = new Intent(this, MapActivity.class);
            startActivity(intent);
            finish();
        }else if (id == R.id.tv_country_code){
            showPhoneCountryDialog();
        }else if (id == R.id.iv_login_qq) {
            ThirdPartySDKManager.getInstance(LoginActivity.this).login(ThirdPartySDKManager.CLOUD_TYPE_QQ,mLoginCallback);
        } else if (id == R.id.iv_login_wechat) {
            ThirdPartySDKManager.getInstance(LoginActivity.this).login(ThirdPartySDKManager.CLOUD_TYPE_WECHAT,mLoginCallback);
        } else if (id == R.id.iv_login_weibo) {
            ThirdPartySDKManager.getInstance(LoginActivity.this).login(ThirdPartySDKManager.CLOUD_TYPE_WEIBO,mLoginCallback);
        }
    }

    MoeMoeCallback mLoginCallback = new MoeMoeCallback() {
        @Override
        public void onSuccess() {
            setResult(RESPONSE_LOGIN_SUCCESS);
            if (mShouldGoMain){
                Intent i = new Intent(LoginActivity.this,MapActivity.class);
                startActivity(i);
            }
            setResult(RESPONSE_LOGIN_SUCCESS);
            finish();
        }

        @Override
        public void onFailure() {

        }
    };

    private void doLogin(){
        mTvPasswordFormat.setVisibility(View.GONE);
        mAccount = "+" + mCountryCode + mEdtAccount.getText().toString();
        mPassword = mEdtPassword.getText().toString();
        if (!StringUtils.isEmailFormated(mAccount) && !PhoneUtil.isPhoneFormated(mEdtAccount.getText().toString(), mCountryCode)) {
            ToastUtil.showCenterToast(this, R.string.msg_login_username_form_error);
        } else if (!StringUtils.isLegalPassword(mPassword)) {
            ToastUtil.showCenterToast(this, R.string.msg_login_password_form_error);
            // 密码格式错误
            mEdtPassword.setText("");	// 清空密码
            mPassword = "";
        } else {
            // 密码，邮箱 初步格式检查正确,调用网络线程开始登陆
            SoftKeyboardUtils.dismissSoftKeyboard(LoginActivity.this);
            final AuthorInfo authorInfo = new AuthorInfo();
            authorInfo.setmPhone(mAccount);
            authorInfo.setmPassword(EncoderUtils.MD5(mPassword));
            if(NetworkUtils.isNetworkAvailable(this)){
                createDialog(getResources().getString(R.string.msg_on_login));
                Otaku.getAccountV2().login(LoginActivity.this, mAccount, EncoderUtils.MD5(mPassword)).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
                    @Override
                    public void success(String token, String s) {
                        authorInfo.setmToken(token);
                        PreferenceManager.getInstance(LoginActivity.this).updateAccessTokenTimeToNow();
                        finalizeDialog();
                        try {
                            JSONObject json = new JSONObject(s);
                            int ok = json.optInt("ok");
                            if (ok == Otaku.SERVER_OK) {
                                String id = json.optString("user_id");
                                authorInfo.setmUUid(id);
                                authorInfo.setmDevId(PhoneUtil.getLocaldeviceId(LoginActivity.this));
                                mPreferMng.saveThirdPartyLoginMsg(authorInfo);
                                Otaku.getAccountV2().requestSelfData(token,LoginActivity.this);
                                ToastUtil.showToast(LoginActivity.this, R.string.msg_login_success);
                                setResult(RESPONSE_LOGIN_SUCCESS);
                                if (mShouldGoMain) {
                                    Intent i = new Intent(LoginActivity.this, MapActivity.class);
                                    startActivity(i);
                                }
                                finish();
                            } else {
                                ToastUtil.showToast(LoginActivity.this, R.string.a_server_msg_illegal_pwd);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void failure(String e) {
                        ToastUtil.showToast(LoginActivity.this, R.string.a_server_msg_illegal_pwd);
                        finalizeDialog();
                    }
                }));
//                Otaku.getAccount().login(LoginActivity.this, mAccount, EncoderUtils.MD5(mPassword), new Callback.InterceptCallback<String>() {
//                    @Override
//                    public void onSuccess(String result) {
//                        finalizeDialog();
//                        try {
//                            JSONObject json = new JSONObject(result);
//                            int ok = json.optInt("ok");
//                            if (ok == Otaku.SERVER_OK) {
//                                String id = json.optString("user_id");
//                                authorInfo.setmUUid(id);
//                                authorInfo.setmDevId(PhoneUtil.getLocaldeviceId(LoginActivity.this));
//                                mPreferMng.saveThirdPartyLoginMsg(authorInfo);
//                                ToastUtil.showToast(LoginActivity.this, R.string.msg_login_success);
//                                setResult(RESPONSE_LOGIN_SUCCESS);
//                                if (mShouldGoMain) {
//                                    Intent i = new Intent(LoginActivity.this, MapActivity.class);
//                                    startActivity(i);
//                                }
//                                finish();
//                            } else {
//                                ToastUtil.showToast(LoginActivity.this, R.string.a_server_msg_illegal_pwd);
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable ex, boolean isOnCallback) {
//                        ToastUtil.showToast(LoginActivity.this, R.string.a_server_msg_illegal_pwd);
//                        finalizeDialog();
//                    }
//
//                    @Override
//                    public void onCancelled(CancelledException cex) {
//                        finalizeDialog();
//                    }
//
//                    @Override
//                    public void onFinished() {
//                        finalizeDialog();
//                    }
//
//                    @Override
//                    public void beforeRequest(UriRequest request) throws Throwable {
//
//                    }
//
//                    @Override
//                    public void afterRequest(UriRequest request) throws Throwable {
//                        String token = request.getResponseHeader(Otaku.X_ACCESS_TOKEN);
//                        if (TextUtils.isEmpty(token)) {
//                            token = request.getResponseHeader(Otaku.X_ACCESS_TOKEN.toLowerCase());
//                        }
//                        authorInfo.setmToken(token);
//                        PreferenceManager.getInstance(LoginActivity.this).updateAccessTokenTimeToNow();
//                    }
//                });
            }else {
                ToastUtil.showCenterToast(this,R.string.a_server_msg_connection);
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
                mTvCountry.setText("+" + mCountryCode);
            }
        });
        dialogFragment.show(getSupportFragmentManager(), TAG +" CountryCode");
    }

    private void initPhoneCountry(){
        mCountryCode = PhoneUtil.getLocalCountryCode(this);
        mTvCountry.setText("+"+mCountryCode);
    }
}
