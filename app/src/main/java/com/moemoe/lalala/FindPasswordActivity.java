package com.moemoe.lalala;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.app.annotation.ContentView;
import com.app.annotation.FindView;
import com.app.common.Callback;
import com.moemoe.lalala.data.AuthorInfo;
import com.moemoe.lalala.network.CallbackFactory;
import com.moemoe.lalala.network.OnNetWorkCallback;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.utils.CountryCode;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.PhoneUtil;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ToastUtil;
import com.moemoe.lalala.view.NoDoubleClickListener;

import org.json.JSONObject;

/**
 * Created by Haru on 2016/4/29 0029.
 */
@ContentView(R.layout.ac_find_pwd_check_account)
public class FindPasswordActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "ForgetPasswordActivity";

    @FindView(R.id.toolbar)
    private Toolbar mToolbar;
    @FindView(R.id.tv_toolbar_title)
    private TextView mTitle;
    @FindView(R.id.tv_tab_phone)
    private View mTvTabPhone;
    @FindView(R.id.tv_tab_email)
    private View mTvTabEmail;
    @FindView(R.id.tv_to_next)
    private View mTvNext;
    @FindView(R.id.ll_phone_number_root)
    private View mLlPhoneRoot;
    @FindView(R.id.tv_country_code)
    private TextView mTvCountry;
    @FindView(R.id.edt_phone_number)
    private EditText mEdtPhone;
    @FindView(R.id.edt_account_name)
    private AutoCompleteTextView mEdtEmail;

    private String mCountryCode;
    private String mPhoneCountry;
    private String mAccount;

    private boolean mIsPhone = false;

    @Override
    protected void initView() {
        mToolbar.setNavigationOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mTitle.setText(R.string.label_find_password);
        initPhoneCountry();
        mEdtPhone.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mAccount = s.toString();
                checkNextEnable();

            }
        });
        mEdtEmail.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mAccount = s.toString();
                checkNextEnable();
                StringUtils.onEmailAutoCompleteTvTextChanged(FindPasswordActivity.this, mEdtEmail, s.toString(), false, null);
            }
        });
        mTvTabEmail.setOnClickListener(this);
        mTvTabPhone.setOnClickListener(this);
        mTvCountry.setOnClickListener(this);
        mTvNext.setOnClickListener(this);
        mIsPhone = false;
        change2Tab(true);
    }

    /**
     * 变化 手机/邮箱
     * @param isPhone
     */
    private void change2Tab(boolean isPhone){
        if(mIsPhone != isPhone){
            mIsPhone = isPhone;
            mTvTabEmail.setSelected(!mIsPhone);
            mTvTabPhone.setSelected(mIsPhone);
            mLlPhoneRoot.setVisibility(mIsPhone ? View.VISIBLE : View.GONE);
            mEdtEmail.setVisibility(!mIsPhone ? View.VISIBLE : View.GONE);
            checkNextEnable();
            if(mIsPhone){
                mEdtPhone.requestFocus();
            }else{
                mEdtEmail.requestFocus();
            }

        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.tv_tab_phone){
            change2Tab(true);
        }else if(id == R.id.tv_tab_email){
            change2Tab(false);
        }else if(id == R.id.tv_country_code){
            showPhoneCountryDialog();
        }else if(id == R.id.tv_to_next){
            go2Next();
        }
    }

    private void go2Next(){
        if(!NetworkUtils.isNetworkAvailable(this)){
            ToastUtil.showCenterToast(this,R.string.a_server_msg_connection);
            return;
        }
        mAccount = "+" + mCountryCode + mEdtPhone.getText().toString();
        createDialog();
        Otaku.getAccountV2().requestCode4ResetPwd(mAccount).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
            @Override
            public void success(String token, String s) {
                finalizeDialog();
                AuthorInfo authorInfo = new AuthorInfo();
                authorInfo.setmPhone(mAccount);
                mPreferMng.saveThirdPartyLoginMsg(authorInfo);
                Intent intent = new Intent(FindPasswordActivity.this, PhoneStateCheckActivity.class);
                intent.putExtra(PhoneStateCheckActivity.EXTRA_ACTION, PhoneStateCheckActivity.ACTION_FIND_PASSWORD);
                startActivity(intent);
                finish();
            }

            @Override
            public void failure(String e) {
                finalizeDialog();
                ToastUtil.showToast(FindPasswordActivity.this, R.string.msg_request_vcode_error);
            }
        }));
    }

    private void checkNextEnable(){
        if(mIsPhone){
            mTvNext.setEnabled(PhoneUtil.isPhoneFormated(mEdtPhone.getText().toString(), mCountryCode));
        }else{
            mTvNext.setEnabled(StringUtils.isEmailFormated(mEdtEmail.getText().toString()));
        }
    }

    private void showPhoneCountryDialog(){
        PhoneRegisterActivity.PhoneCountryDialogFragment dialogFragment = new PhoneRegisterActivity.PhoneCountryDialogFragment();
        dialogFragment.setOnCountryCodeSelectListener(new PhoneRegisterActivity.CountryCodeSelectListener()
        {
            @Override
            public void onItemSelected(CountryCode countryCode) {
                mCountryCode = countryCode.getCode();
                mPhoneCountry = countryCode.getCountry();
                mTvCountry.setText(mPhoneCountry + "(+" + mCountryCode + ")");
            }
        });
        dialogFragment.show(getSupportFragmentManager(), TAG +" CountryCode");
    }

    private void initPhoneCountry(){
        try {
            mPhoneCountry = PhoneUtil.getLocalDisplayStr(this);
            mCountryCode = PhoneUtil.getLocalCountryCode(this);
        } catch (IllegalStateException e) {
        }
    }
}
