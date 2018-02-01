package com.moemoe.lalala.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerApplyAdminComponent;
import com.moemoe.lalala.di.modules.ApplyAdminModule;
import com.moemoe.lalala.model.entity.CommonRequest;
import com.moemoe.lalala.presenter.ApplyAdminContract;
import com.moemoe.lalala.presenter.ApplyAdminPresenter;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.ViewUtils;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * 管理员申请view
 * Created by yi on 2018/1/23.
 */

public class ApplyAdminActivity extends BaseAppCompatActivity implements ApplyAdminContract.View{

    private static final int LIMIT_CONTENT = 100;

    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_toolbar_title)
    TextView mTvTitle;
    @BindView(R.id.tv_menu)
    TextView mTvDone;
    @BindView(R.id.et_reason)
    EditText mEtReason;

    @Inject
    ApplyAdminPresenter mPresenter;

    public static void startActivity(Context context,String id){
        Intent i = new Intent(context,ApplyAdminActivity.class);
        i.putExtra(UUID,id);
        context.startActivity(i);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ac_apply_admin;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerApplyAdminComponent.builder()
                .applyAdminModule(new ApplyAdminModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
        mEtReason.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Editable editable = mEtReason.getText();
                int len = editable.length();
                if (len > LIMIT_CONTENT) {
                    int selEndIndex = Selection.getSelectionEnd(editable);
                    mEtReason.setText(editable.subSequence(0, LIMIT_CONTENT));
                    editable = mEtReason.getText();
                    int newLen = editable.length();
                    if (selEndIndex > newLen) {
                        selEndIndex = editable.length();
                    }
                    Selection.setSelection(editable, selEndIndex);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {
        mIvBack.setVisibility(View.VISIBLE);
        mIvBack.setImageResource(R.drawable.btn_back_black_normal);
        mIvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                onBackPressed();
            }
        });
        mTvTitle.setText("申请成为管理员");
        ViewUtils.setRightMargins(mTvDone,(int) getResources().getDimension(R.dimen.x36));
        mTvDone.setVisibility(View.VISIBLE);
        mTvDone.setText("提交");
        mTvDone.setTextColor(ContextCompat.getColor(this,R.color.main_cyan));
        mTvDone.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                //TODO 提交申请
                String reason = mEtReason.getText().toString();
                if(!TextUtils.isEmpty(reason)){
                    mPresenter.applyAdmin(new CommonRequest(getIntent().getStringExtra(UUID),reason));
                }else {
                    showToast("理由不能为空");
                }
            }
        });
    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void initData() {

    }

    @Override
    public void onFailure(int code, String msg) {
        ErrorCodeUtils.showErrorMsgByCode(this,code,msg);
    }

    @Override
    public void onApplyAdminSuccess() {
        showToast("申请成功");
        finish();
    }

    @Override
    protected void onDestroy() {
        if(mPresenter != null) mPresenter.release();
        super.onDestroy();
    }
}
