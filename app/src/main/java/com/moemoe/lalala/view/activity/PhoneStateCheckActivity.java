package com.moemoe.lalala.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.igexin.sdk.PushManager;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerPhoneStateComponent;
import com.moemoe.lalala.di.modules.PhoneStateModule;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.AuthorInfo;
import com.moemoe.lalala.model.entity.LoginEntity;
import com.moemoe.lalala.model.entity.LoginResultEntity;
import com.moemoe.lalala.presenter.PhoneStateContract;
import com.moemoe.lalala.presenter.PhoneStatePresenter;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ViewUtils;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by yi on 2016/11/29.
 */

public class PhoneStateCheckActivity extends BaseAppCompatActivity implements PhoneStateContract.View{
    public static final String EXTRA_ACTION = "action";
    public static final String EXTRA_CODE = "code";
    public static final int ACTION_REGISTER = 0;
    public static final int ACTION_CHAGE_PASSWORD = 1;
    public static final int ACTION_FIND_PASSWORD = 2;
    private static final int MSG_RESEND_COLD_TIME = 1001;

    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_toolbar_title)
    TextView mTitle;
    @BindView(R.id.tv_send_phone_code)
    TextView mTvHintMsg;
    @BindView(R.id.edt_phone_code)
    EditText mEdtPhoneCode;
    @BindView(R.id.tv_resend_phone_code)
    TextView mTvResend;
    @BindView(R.id.tv_go_next)
    View mTvNext;
    @Inject
    PhoneStatePresenter mPresenter;
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

        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.ac_phone_code_check;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
//        ImmersionBar.with(this)
//                .statusBarView(R.id.top_view)
//                .statusBarDarkFont(true,0.2f)
//                .transparentNavigationBar()
//                .init();
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
        DaggerPhoneStateComponent.builder()
                .phoneStateModule(new PhoneStateModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mTitle.setText(R.string.label_register);
        mAction = getIntent().getIntExtra(EXTRA_ACTION, ACTION_REGISTER);
        if(mAction == ACTION_REGISTER){
            mTitle.setText(R.string.label_register);
        }else if(mAction == ACTION_FIND_PASSWORD){
            mTitle.setText(R.string.label_find_password);
        }
        final AuthorInfo authorInfo = PreferenceUtils.getAuthorInfo();
        mAccount = authorInfo.getPhone();
        mLabelResend = getString(R.string.label_resend);
        mTvNext.setEnabled(false);
        String code = getString(R.string.msg_register_send_phone_code_to, mAccount);
        SpannableStringBuilder ssb = new SpannableStringBuilder(code);
        ForegroundColorSpan fs = new ForegroundColorSpan(ContextCompat.getColor(this,R.color.main_cyan));
        ssb.setSpan(fs, code.indexOf(mAccount), code.indexOf(mAccount) + mAccount.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTvHintMsg.setText(ssb);
        startResendCold();

    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

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
        final AuthorInfo authorInfo = PreferenceUtils.getAuthorInfo();
        mAccount = authorInfo.getPhone();
        mTvNext.setOnClickListener(new NoDoubleClickListener() {

            @Override
            public void onNoDoubleClick(View v) {
                mCode = mEdtPhoneCode.getText().toString();
                if (StringUtils.isLeagleVCode(mCode)) {
                    if (NetworkUtils.checkNetworkAndShowError(PhoneStateCheckActivity.this)) {
                        createDialog();
                        mPresenter.checkPhoneCode(mAction,authorInfo,mCode);
                    }
                }else {
                    showToast(R.string.msg_vcode_illegal);
                }
            }
        });
    }

    @Override
    protected void initData() {

    }

    private void startResendCold() {
        mTvResend.setEnabled(false);
        mResendCold = 60;
        mHandler.sendEmptyMessageDelayed(MSG_RESEND_COLD_TIME, 1000);
    }
    @Override
    protected void onDestroy() {
        if(mPresenter != null) mPresenter.release();
        mHandler.removeMessages(MSG_RESEND_COLD_TIME);
        super.onDestroy();
    }

    @Override
    public void onFailure(int code,String msg) {
        finalizeDialog();
        ErrorCodeUtils.showErrorMsgByCode(PhoneStateCheckActivity.this,code,msg);
    }

    @Override
    public void onRegisterSuccess(AuthorInfo authorInfo) {
        finalizeDialog();
        LoginEntity entity = new LoginEntity(authorInfo.getPhone(), authorInfo.getPassword(), PushManager.getInstance().getClientid(this) + "@and");
        mPresenter.login(authorInfo,entity);
    }

    @Override
    public void onFindPwdSuccess() {
        finalizeDialog();
        Intent intent = new Intent(PhoneStateCheckActivity.this, ChangePasswordActivity.class);
        intent.putExtra(EXTRA_ACTION, mAction);
        intent.putExtra(EXTRA_CODE, mCode);
        startActivity(intent);
        finish();
    }

    @Override
    public void onLoginSuccess(AuthorInfo info,LoginResultEntity entity) {
        finalizeDialog();
        info.setToken(entity.getToken());
        info.setUserId(entity.getUserId());
        info.setLevel(1);
        info.setUserName(entity.getUserName());
        info.setRcToken(entity.getRcToken());
        info.setCoin(0);
        info.setOpenBag(entity.isOpenBag());
        info.setDeskMateEntities(entity.getDeskMateList());
        info.setInspector(entity.isInspector());
        if(!entity.getHeadPath().startsWith("http")){
            info.setHeadPath(ApiService.URL_QINIU + entity.getHeadPath());
        }else {
            info.setHeadPath(entity.getHeadPath());
        }
        PreferenceUtils.setAuthorInfo(info);
        Intent i = new Intent(PhoneStateCheckActivity.this, MapActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        showToast(R.string.msg_login_success);
        finish();
    }
}
