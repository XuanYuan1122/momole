package com.moemoe.lalala.view.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerInviteComponent;
import com.moemoe.lalala.di.modules.InviteModule;
import com.moemoe.lalala.model.entity.InviteUserEntity;
import com.moemoe.lalala.presenter.InviteContract;
import com.moemoe.lalala.presenter.InvitePresenter;
import com.moemoe.lalala.utils.AlertDialogUtil;
import com.moemoe.lalala.utils.AndroidBug5497Workaround;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.ToastUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.adapter.InviteAdapter;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

/**
 *
 * Created by yi on 2016/12/1.
 */

public class InviteAddActivity extends BaseAppCompatActivity implements InviteContract.View{

    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_toolbar_title)
    TextView mTvTitle;
    @BindView(R.id.tv_menu)
    TextView mTvMenu;
    @BindView(R.id.et_num)
    EditText mEtNum;
    @BindView(R.id.tv_left_menu)
    TextView mTvLeft;

    @Inject
    InvitePresenter mPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_invite_add;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
        AndroidBug5497Workaround.assistActivity(this);
        DaggerInviteComponent.builder()
                .inviteModule(new InviteModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mTvTitle.setText("填写邀请码");
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    protected void onDestroy() {
        if(mPresenter != null)mPresenter.release();
        super.onDestroy();
    }

    @Override
    protected void initListeners() {
        mTvMenu.setVisibility(View.VISIBLE);
        ViewUtils.setRightMargins(mTvMenu,(int) getResources().getDimension(R.dimen.x36));
        mTvMenu.setText("确定");
        mTvMenu.setTextColor(ContextCompat.getColor(this,R.color.main_cyan));
        int type = getIntent().getIntExtra("type",0);
        if(type == 0){
            mTvLeft.setVisibility(View.VISIBLE);
            ViewUtils.setLeftMargins(mTvLeft, DensityUtil.dip2px(this,18));
            mTvLeft.setText("跳过");
            mTvLeft.setTextColor(ContextCompat.getColor(this,R.color.main_cyan));
            mTvLeft.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    setResult(RESULT_OK);
                    finish();
                }
            });
        }else if(type == 1){
            mIvBack.setVisibility(View.VISIBLE);
            mIvBack.setImageResource(R.drawable.btn_back_black_normal);
            mIvBack.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    setResult(RESULT_OK);
                    finish();
                }
            });
        }
        mTvMenu.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                String no = mEtNum.getText().toString();
                if(!TextUtils.isEmpty(no)){
                    mPresenter.getUserName(no);
                }else {
                    showToast("邀请码不能为空");
                }
            }
        });
    }

    @Override
    protected void initData() {

    }


    @Override
    public void onFailure(int code, String msg) {
        ErrorCodeUtils.showErrorMsgByCode(this,code,msg);
    }

    @Override
    public void onLoadListSuccess(ArrayList<InviteUserEntity> entities) {

    }

    @Override
    public void onGetUserNameSuccess(String name) {
        final AlertDialogUtil dialogUtil = AlertDialogUtil.getInstance();
        dialogUtil.createPromptNormalDialog(this,"确认对方ID:" + name);
        dialogUtil.setOnClickListener(new AlertDialogUtil.OnClickListener() {
            @Override
            public void CancelOnClick() {
                dialogUtil.dismissDialog();
            }

            @Override
            public void ConfirmOnClick() {
                mPresenter.useInviteNo(mEtNum.getText().toString());
                dialogUtil.dismissDialog();
            }
        });
        dialogUtil.showDialog();
    }

    @Override
    public void onUseNoSuccess() {
        showToast("接收邀请成功");
        setResult(RESULT_OK);
        finish();
    }
}
