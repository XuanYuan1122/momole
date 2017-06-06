package com.moemoe.lalala.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerSimpleComponent;
import com.moemoe.lalala.di.modules.SimpleModule;
import com.moemoe.lalala.model.entity.AuthorInfo;
import com.moemoe.lalala.presenter.SimpleContract;
import com.moemoe.lalala.presenter.SimplePresenter;
import com.moemoe.lalala.utils.AndroidBug5497Workaround;
import com.moemoe.lalala.utils.CountryCode;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PhoneUtil;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StringUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by yi on 2016/12/1.
 */

public class FindPasswordActivity extends BaseAppCompatActivity implements SimpleContract.View{
    private final String TAG = "ForgetPasswordActivity";
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tv_toolbar_title)
    TextView mTitle;
    @BindView(R.id.tv_tab_phone)
    View mTvTabPhone;
    @BindView(R.id.tv_tab_email)
    View mTvTabEmail;
    @BindView(R.id.tv_to_next)
    View mTvNext;
    @BindView(R.id.ll_phone_number_root)
    View mLlPhoneRoot;
    @BindView(R.id.tv_country_code)
    TextView mTvCountry;
    @BindView(R.id.edt_phone_number)
    EditText mEdtPhone;
    @BindView(R.id.edt_account_name)
    AutoCompleteTextView mEdtEmail;
    @Inject
    SimplePresenter mPresenter;
    private String mCountryCode;
    private String mPhoneCountry;
    private String mAccount;

    private boolean mIsPhone = false;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_find_pwd_check_account;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        AndroidBug5497Workaround.assistActivity(this);
        DaggerSimpleComponent.builder()
                .simpleModule(new SimpleModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mTitle.setText(R.string.label_find_password);
        initPhoneCountry();
        mIsPhone = false;
        change2Tab(true);
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    protected void initListeners() {
        mToolbar.setNavigationOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
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
    }

    /**
     * 变化 手机/邮箱
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
    protected void onDestroy() {
        mPresenter.release();
        super.onDestroy();
    }

    @OnClick({R.id.tv_tab_phone,R.id.tv_tab_email,R.id.tv_country_code,R.id.tv_to_next})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.tv_tab_phone:
                change2Tab(true);
                break;
            case R.id.tv_tab_email:
                change2Tab(false);
                break;
            case R.id.tv_country_code:
                showPhoneCountryDialog();
                break;
            case R.id.tv_to_next:
                go2Next();
                break;
        }
    }

    private void checkNextEnable() {
        if (mIsPhone) {
            mTvNext.setEnabled(PhoneUtil.isPhoneFormated(mEdtPhone.getText().toString(), mCountryCode));
        } else {
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
            e.printStackTrace();
        }
    }

    @Override
    protected void initData() {

    }

    private void go2Next(){
        if(NetworkUtils.checkNetworkAndShowError(this)){
            mAccount = "+" + mCountryCode + mEdtPhone.getText().toString();
            createDialog();
            mPresenter.doRequest(mAccount,0);
        }
    }

    @Override
    public void onSuccess(Object o) {
        finalizeDialog();
        AuthorInfo authorInfo = new AuthorInfo();
        authorInfo.setPhone(mAccount);
        PreferenceUtils.setAuthorInfo(authorInfo);
        Intent intent = new Intent(FindPasswordActivity.this, PhoneStateCheckActivity.class);
        intent.putExtra(PhoneStateCheckActivity.EXTRA_ACTION, PhoneStateCheckActivity.ACTION_FIND_PASSWORD);
        startActivity(intent);
        finish();
    }

    @Override
    public void onFailure(int code,String msg) {
        finalizeDialog();
        ErrorCodeUtils.showErrorMsgByCode(FindPasswordActivity.this,code,msg);
    }
}
