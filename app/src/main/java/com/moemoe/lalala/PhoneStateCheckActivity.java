package com.moemoe.lalala;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.WindowManager;
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
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.PhoneUtil;
import com.moemoe.lalala.utils.PreferenceManager;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ToastUtil;
import com.moemoe.lalala.view.NoDoubleClickListener;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Haru on 2016/4/29 0029.
 */
@ContentView(R.layout.ac_phone_code_check)
public class PhoneStateCheckActivity extends BaseActivity{
    public static final String EXTRA_ACTION = "action";
    public static final String EXTRA_CODE = "code";
    public static final int ACTION_REGISTER = 0;
    public static final int ACTION_CHAGE_PASSWORD = 1;
    public static final int ACTION_FIND_PASSWORD = 2;
    private static final int MSG_RESEND_COLD_TIME = 1001;

    @FindView(R.id.toolbar)
    private Toolbar mToolbar;
    @FindView(R.id.tv_toolbar_title)
    private TextView mTitle;
    @FindView(R.id.tv_send_phone_code)
    private TextView mTvHintMsg;
    @FindView(R.id.edt_phone_code)
    private EditText mEdtPhoneCode;
    @FindView(R.id.tv_resend_phone_code)
    private TextView mTvResend;
    @FindView(R.id.tv_go_next)
    private View mTvNext;

    private int mAction;
    private String mLabelResend;
    private int mResendCold;
    private String mAccount;
    private String mCode;

    private Handler mHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            int what = msg.what;
            if (what == MSG_RESEND_COLD_TIME) {
                mResendCold--;
                if (mResendCold > 0) {
                    mTvResend.setText(mLabelResend + "(" + mResendCold + ")");
                    mHandler.sendEmptyMessageDelayed(MSG_RESEND_COLD_TIME, 1000);
                } else if (mResendCold == 0) {
                    mTvResend.setText(mLabelResend);
                    mTvResend.setEnabled(true);
                }
            }

        };
    };

    @Override
    protected void initView() {
        mToolbar.setNavigationOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mTitle.setText(R.string.label_register);
        mAction = mIntent.getIntExtra(EXTRA_ACTION, ACTION_REGISTER);
        if(mAction == ACTION_REGISTER){
            mTitle.setText(R.string.label_register);
        }else if(mAction == ACTION_FIND_PASSWORD){
            mTitle.setText(R.string.label_find_password);
        }
        final AuthorInfo authorInfo = mPreferMng.getThirdPartyLoginMsg();
        mAccount = authorInfo.getmPhone();
        mLabelResend = getString(R.string.label_resend);
        mTvNext.setEnabled(false);
        String code = getString(R.string.msg_register_send_phone_code_to, mAccount);
        SpannableStringBuilder ssb = new SpannableStringBuilder(code);
        ForegroundColorSpan fs = new ForegroundColorSpan(getResources().getColor(R.color.main_title_cyan));
        ssb.setSpan(fs, code.indexOf(mAccount), code.indexOf(mAccount) + mAccount.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTvHintMsg.setText(ssb);
        startResendCold();

        mEdtPhoneCode.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mCode = mEdtPhoneCode.getText().toString();
                mTvNext.setEnabled(!TextUtils.isEmpty(mCode));

            }
        });

        mTvNext.setOnClickListener(new NoDoubleClickListener() {

            @Override
            public void onNoDoubleClick(View v) {
                mCode = mEdtPhoneCode.getText().toString();
                if (StringUtils.isLeagleVCode(mCode)) {
                    if (NetworkUtils.isNetworkAvailable(PhoneStateCheckActivity.this)) {
                        createDialog();
                        Otaku.getAccountV2().checkPhoneCode(mAction, authorInfo, mCode).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
                            @Override
                            public void success(String token, String s) {
                                if (mAction == ACTION_REGISTER) {
                                    Otaku.getAccountV2().login(PhoneStateCheckActivity.this, authorInfo.getmPhone(), authorInfo.getmPassword()).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
                                        @Override
                                        public void success(String token, String s) {
                                            authorInfo.setmToken(token);
                                            PreferenceManager.getInstance(PhoneStateCheckActivity.this).updateAccessTokenTimeToNow();
                                            finalizeDialog();
                                            try {
                                                JSONObject json = new JSONObject(s);
                                                int ok = json.optInt("ok");
                                                if (ok == Otaku.SERVER_OK) {
                                                    String id = json.optString("user_id");
                                                    authorInfo.setmUUid(id);
                                                    authorInfo.setmDevId(PhoneUtil.getLocaldeviceId(getApplicationContext()));
                                                    authorInfo.setLevel(1);
                                                    mPreferMng.saveThirdPartyLoginMsg(authorInfo);
                                                    Otaku.getAccountV2().requestSelfData(token,PhoneStateCheckActivity.this);
                                                    Intent i = new Intent(PhoneStateCheckActivity.this, MapActivity.class);
                                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    startActivity(i);
                                                    ToastUtil.showToast(PhoneStateCheckActivity.this, R.string.msg_login_success);
                                                    finish();
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void failure(String e) {
                                            finalizeDialog();
                                        }
                                    }));
                                } else if (mAction == ACTION_FIND_PASSWORD) {
                                    finalizeDialog();
                                    Intent intent = new Intent(PhoneStateCheckActivity.this, ChangePasswordActivity.class);
                                    intent.putExtra(EXTRA_ACTION, mAction);
                                    intent.putExtra(EXTRA_CODE, mCode);
                                    startActivity(intent);
                                    finish();
                                }
                            }

                            @Override
                            public void failure(String e) {
                                finalizeDialog();
                                ToastUtil.showToast(PhoneStateCheckActivity.this, R.string.msg_check_vcode_error);

                            }
                        }));
                    } else {
                        ToastUtil.showCenterToast(PhoneStateCheckActivity.this, R.string.msg_server_connection);
                    }
                } else {
                    ToastUtil.showToast(PhoneStateCheckActivity.this, R.string.msg_vcode_illegal);
                }
            }
        });

        mTvResend.setOnClickListener(new NoDoubleClickListener() {

            @Override
            public void onNoDoubleClick(View v) {
                if(NetworkUtils.isNetworkAvailable(PhoneStateCheckActivity.this)){
                if(mAction == ACTION_REGISTER){
                    Otaku.getAccountV2().register(mAccount).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
                        @Override
                        public void success(String token, String s) {

                        }

                        @Override
                        public void failure(String e) {
                            ToastUtil.showToast(PhoneStateCheckActivity.this,R.string.msg_register_failed);
                        }
                    }));
//                        Otaku.getAccount().register(mAccount, new Callback.CommonCallback<String>() {
//                            @Override
//                            public void onSuccess(String result) {
//                            }
//
//                            @Override
//                            public void onError(Throwable ex, boolean isOnCallback) {
//                                ToastUtil.showToast(PhoneStateCheckActivity.this,R.string.msg_register_failed);
//                            }
//
//                            @Override
//                            public void onCancelled(CancelledException cex) {
//
//                            }
//
//                            @Override
//                            public void onFinished() {
//
//                            }
//                        });
                }else if(mAction == ACTION_FIND_PASSWORD){
                    Otaku.getAccountV2().requestCode4ResetPwd(mAccount).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
                        @Override
                        public void success(String token, String s) {

                        }

                        @Override
                        public void failure(String e) {
                            ToastUtil.showToast(PhoneStateCheckActivity.this, R.string.msg_request_vcode_error);
                        }
                    }));
//
//                    Otaku.getAccount().requestCode4ResetPwd(mAccount, new Callback.CommonCallback<String>() {
//                        @Override
//                        public void onSuccess(String result) {
//
//                        }
//
//                        @Override
//                        public void onError(Throwable ex, boolean isOnCallback) {
//                            ToastUtil.showToast(PhoneStateCheckActivity.this, R.string.msg_request_vcode_error);
//                        }
//
//                        @Override
//                        public void onCancelled(CancelledException cex) {
//
//                        }
//
//                        @Override
//                        public void onFinished() {
//
//                        }
//                    });
                }
                    startResendCold();
                }else {
                    ToastUtil.showCenterToast(PhoneStateCheckActivity.this, R.string.msg_server_connection);
                }

            }
        });
    }



    private void startResendCold() {
        mTvResend.setEnabled(false);
        mResendCold = 60;
        mHandler.sendEmptyMessageDelayed(MSG_RESEND_COLD_TIME, 1000);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(MSG_RESEND_COLD_TIME);
    }

}
