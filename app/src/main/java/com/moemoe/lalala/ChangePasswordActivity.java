package com.moemoe.lalala;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.app.annotation.ContentView;
import com.app.annotation.FindView;
import com.app.common.Callback;
import com.app.http.request.UriRequest;
import com.moemoe.lalala.data.AuthorInfo;
import com.moemoe.lalala.network.CallbackFactory;
import com.moemoe.lalala.network.OnNetWorkCallback;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.utils.EncoderUtils;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.PreferenceManager;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ToastUtil;
import com.moemoe.lalala.view.NoDoubleClickListener;

import org.json.JSONObject;

/**
 * Created by Haru on 2016/4/29 0029.
 */
@ContentView(R.layout.ac_change_password)
public class ChangePasswordActivity extends BaseActivity {

    @FindView(R.id.toolbar)
    private Toolbar mToolbar;
    @FindView(R.id.tv_toolbar_title)
    private TextView mTitle;
    @FindView(R.id.edt_password_1)
    private EditText mEdtPassword1;
    @FindView(R.id.edt_password_2)
    private EditText mEdtPassword2;
    @FindView(R.id.tv_do)
    private TextView mTvDo;
    @FindView(R.id.tv_password_format)
    private TextView mTvPasswordFormat;
    @FindView(R.id.cb_show_password)
    private CheckBox mCbShowPwd;

    private int mAction;
    private String mCode;

    @Override
    protected void initView() {
        mAction = mIntent.getIntExtra(PhoneStateCheckActivity.EXTRA_ACTION, PhoneStateCheckActivity.ACTION_CHAGE_PASSWORD);
        mCode = mIntent.getStringExtra(PhoneStateCheckActivity.EXTRA_CODE);
        mToolbar.setNavigationOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        if(mAction == PhoneStateCheckActivity.ACTION_CHAGE_PASSWORD){
            mTitle.setText(R.string.label_change_password);
        }else if(mAction == PhoneStateCheckActivity.ACTION_FIND_PASSWORD){
            mTitle.setText(R.string.label_reset_password);
        }

        if(mAction == PhoneStateCheckActivity.ACTION_CHAGE_PASSWORD){
            // 旧密码，新密码
            mEdtPassword1.setHint(R.string.label_hint_old_password);
            mEdtPassword2.setHint(R.string.label_hint_new_password);
            mTvDo.setText(R.string.label_change_password);
        }else{
            // 新密码，确认密码
            mEdtPassword1.setHint(R.string.label_hint_new_password);
            mEdtPassword2.setHint(R.string.label_hint_again_password);
            mTvDo.setText(R.string.label_reset_password);
        }

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
        mCbShowPwd.setChecked(false);
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
                    ToastUtil.showCenterToast(ChangePasswordActivity.this,R.string.a_server_msg_connection);
                    return;
                }

                if (!StringUtils.isLegalPassword(pwd1) || !StringUtils.isLegalPassword(pwd2)) {
                    ToastUtil.showCenterToast(ChangePasswordActivity.this, R.string.msg_login_password_form_error);
                    mTvPasswordFormat.setVisibility(View.VISIBLE);
                    mTvDo.setEnabled(false);
                    return;
                }

                if (mAction == PhoneStateCheckActivity.ACTION_CHAGE_PASSWORD) {
                    final AuthorInfo authorInfo = mPreferMng.getThirdPartyLoginMsg();
                    createDialog();
                    Otaku.getAccountV2().changePassword(authorInfo.getmToken(), EncoderUtils.MD5(pwd2), EncoderUtils.MD5(pwd1)).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
                        @Override
                        public void success(String token, String s) {
                            finalizeDialog();
                            authorInfo.setmPassword(EncoderUtils.MD5(pwd2));
                            mPreferMng.saveThirdPartyLoginMsg(authorInfo);
                            ToastUtil.showToast(ChangePasswordActivity.this, R.string.msg_change_password_success);
                            finish();
                        }

                        @Override
                        public void failure(String e) {
                            finalizeDialog();
                            ToastUtil.showToast(ChangePasswordActivity.this, R.string.msg_change_pwd_fail);
                        }
                    }));
                } else if (mAction == PhoneStateCheckActivity.ACTION_FIND_PASSWORD) {
                    final AuthorInfo authorInfo = mPreferMng.getThirdPartyLoginMsg();
                    // 旧密码，新密码
                    if (!pwd1.equals(pwd2)) {
                        // 密码不同
                        ToastUtil.showCenterToast(ChangePasswordActivity.this, R.string.msg_password_2_not_same);
                        mTvDo.setEnabled(false);
                        return;
                    } else {
                        createDialog();
                        Otaku.getAccountV2().resetPwdByCode(authorInfo.getmToken(), authorInfo.getmPhone(), EncoderUtils.MD5(pwd2), mCode).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
                            @Override
                            public void success(String token, String s) {
                                finalizeDialog();
                                authorInfo.setmPassword(EncoderUtils.MD5(pwd2));
                                mPreferMng.saveThirdPartyLoginMsg(authorInfo);
                                ToastUtil.showToast(ChangePasswordActivity.this, R.string.msg_set_password_success);
                                finish();
                            }

                            @Override
                            public void failure(String e) {
                                finalizeDialog();
                                ToastUtil.showToast(ChangePasswordActivity.this, R.string.msg_set_password_fail);
                            }
                        }));
                    }
                }
            }
        });
    }


    private void checkGoEnable(){
        String pwd1 = mEdtPassword1.getText().toString();
        String pwd2 = mEdtPassword2.getText().toString();

        mTvDo.setEnabled(!TextUtils.isEmpty(pwd1) && !TextUtils.isEmpty(pwd2));

    }
}
