package com.moemoe.lalala.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerChangePwdComponent;
import com.moemoe.lalala.di.modules.ChangePwdModule;
import com.moemoe.lalala.model.entity.AuthorInfo;
import com.moemoe.lalala.presenter.ChangePasswordContract;
import com.moemoe.lalala.presenter.ChangePasswordPresenter;
import com.moemoe.lalala.utils.EncoderUtils;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StringUtils;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * 找回密码
 * Created by yi on 2016/11/28.
 */

public class ChangePasswordActivity extends BaseAppCompatActivity implements ChangePasswordContract.View{

    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_toolbar_title)
    TextView mTitle;
    @BindView(R.id.edt_password_1)
    EditText mEdtPassword1;
    @BindView(R.id.edt_password_2)
    EditText mEdtPassword2;
    @BindView(R.id.tv_do)
    TextView mTvDo;
    @BindView(R.id.tv_password_format)
    TextView mTvPasswordFormat;
    @BindView(R.id.cb_show_password)
    CheckBox mCbShowPwd;

    @Inject
    ChangePasswordPresenter mPresenter;
    private int mAction;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_change_password;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        Intent i = getIntent();
        if(i == null){
            finish();
            return;
        }
        DaggerChangePwdComponent.builder()
                .changePwdModule(new ChangePwdModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        ImmersionBar.with(this)
                .statusBarView(R.id.top_view)
                .statusBarDarkFont(true,0.2f)
                .init();
        mAction = i.getIntExtra(PhoneStateCheckActivity.EXTRA_ACTION,PhoneStateCheckActivity.ACTION_CHAGE_PASSWORD);
        if(mAction == PhoneStateCheckActivity.ACTION_CHAGE_PASSWORD){
            mTitle.setText(R.string.label_change_password);
            // 旧密码，新密码
            mEdtPassword1.setHint(R.string.label_hint_old_password);
            mEdtPassword2.setHint(R.string.label_hint_new_password);
            mTvDo.setText(R.string.label_change_password);
        }else if(mAction == PhoneStateCheckActivity.ACTION_FIND_PASSWORD){
            mTitle.setText(R.string.label_reset_password);
            // 新密码，确认密码
            mEdtPassword1.setHint(R.string.label_hint_new_password);
            mEdtPassword2.setHint(R.string.label_hint_again_password);
            mTvDo.setText(R.string.label_reset_password);
        }
        mCbShowPwd.setChecked(false);
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {
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
    protected void onDestroy() {
        if(mPresenter != null)mPresenter.release();
        super.onDestroy();
    }

    @Override
    protected void initListeners() {
        mEdtPassword1.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkGoEnable();
            }
        });
        mEdtPassword2.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkGoEnable();

            }
        });
        mCbShowPwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int type = isChecked ? (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)
                        : (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                mEdtPassword1.setInputType(type);
                mEdtPassword2.setInputType(type);
            }
        });
        mTvDo.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                final String pwd1 = mEdtPassword1.getText().toString();
                final String pwd2 = mEdtPassword2.getText().toString();
                if(!NetworkUtils.isNetworkAvailable(ChangePasswordActivity.this)){
                    showToast(R.string.msg_connection);
                    return;
                }
                if (!StringUtils.isLegalPassword(pwd1) || !StringUtils.isLegalPassword(pwd2)) {
                    showToast(R.string.msg_login_password_form_error);
                    mTvPasswordFormat.setVisibility(View.VISIBLE);
                    mTvDo.setEnabled(false);
                    return;
                }
                if (mAction == PhoneStateCheckActivity.ACTION_CHAGE_PASSWORD) {
                    createDialog();
                    mPresenter.changePassword(EncoderUtils.MD5(pwd1),EncoderUtils.MD5(pwd2));
                }else if (mAction == PhoneStateCheckActivity.ACTION_FIND_PASSWORD) {
                    // 旧密码，新密码
                    if (!pwd1.equals(pwd2)) {
                        // 密码不同
                        showToast(R.string.msg_password_2_not_same);
                        mTvDo.setEnabled(false);
                    }else {
                        createDialog();
                        mPresenter.resetPwdByCode(PreferenceUtils.getAuthorInfo().getPhone(),EncoderUtils.MD5(pwd2));
                    }
                }
            }
        });
    }

    @Override
    protected void initData() {

    }

    private void checkGoEnable(){
        String pwd1 = mEdtPassword1.getText().toString();
        String pwd2 = mEdtPassword2.getText().toString();
        mTvDo.setEnabled(!TextUtils.isEmpty(pwd1) && !TextUtils.isEmpty(pwd2));
    }

    @Override
    public void onChangeSuccess() {
        finalizeDialog();
        AuthorInfo authorInfo = PreferenceUtils.getAuthorInfo();
        String pwd2 = mEdtPassword2.getText().toString();
        authorInfo.setPassword(EncoderUtils.MD5(pwd2));
        PreferenceUtils.setAuthorInfo(authorInfo);
        if (mAction == PhoneStateCheckActivity.ACTION_CHAGE_PASSWORD) {
            showToast(R.string.msg_change_password_success);
        }else if (mAction == PhoneStateCheckActivity.ACTION_FIND_PASSWORD) {
            showToast(R.string.msg_set_password_success);
        }
        finish();
    }

    @Override
    public void onFailure(int code,String msg) {
        finalizeDialog();
        ErrorCodeUtils.showErrorMsgByCode(ChangePasswordActivity.this,code,msg);
    }
}
