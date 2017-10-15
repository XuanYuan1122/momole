package com.moemoe.lalala.view.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerInviteComponent;
import com.moemoe.lalala.di.modules.InviteModule;
import com.moemoe.lalala.model.entity.InviteUserEntity;
import com.moemoe.lalala.presenter.InviteContract;
import com.moemoe.lalala.presenter.InvitePresenter;
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

public class InviteActivity extends BaseAppCompatActivity implements InviteContract.View{

    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_toolbar_title)
    TextView mTvTitle;
//    @BindView(R.id.tv_menu)
//    TextView mTvMenu;
    @BindView(R.id.tv_invit_num)
    TextView mTvInviteNum;
    @BindView(R.id.tv_copy)
    TextView mTvCopy;
    @BindView(R.id.tv_guize)
    TextView mTvGuiZe;
    @BindView(R.id.list)
    RecyclerView mListDocs;
    @BindView(R.id.tv_name)
    TextView mTvName;

    @Inject
    InvitePresenter mPresenter;

    private InviteAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_invite;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
        DaggerInviteComponent.builder()
                .inviteModule(new InviteModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mTvTitle.setText("我的邀请");
        mTvInviteNum.setText(getIntent().getIntExtra("id",0) + "");
        mTvName.setText(getIntent().getStringExtra("name"));
        mAdapter = new InviteAdapter();
        mListDocs.setLayoutManager(new LinearLayoutManager(this));
        mListDocs.setAdapter(mAdapter);
        mPresenter.loadList();
        mTvCopy.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData mClipData = ClipData.newPlainText("邀请码", getIntent().getIntExtra("id",0) + "");
                cmb.setPrimaryClip(mClipData);
                ToastUtils.showShortToast(InviteActivity.this, getString(R.string.label_level_copy_success));
            }
        });
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
        mIvBack.setVisibility(View.VISIBLE);
        mIvBack.setImageResource(R.drawable.btn_back_black_normal);
        mIvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
//        mTvMenu.setVisibility(View.VISIBLE);
//        ViewUtils.setRightMargins(mTvMenu,(int) getResources().getDimension(R.dimen.x36));
//        mTvMenu.setText("分享邀请");
//        mTvMenu.setTextColor(ContextCompat.getColor(this,R.color.main_cyan));
    }

    @Override
    protected void initData() {

    }


    @Override
    public void onFailure(int code, String msg) {

    }

    @Override
    public void onLoadListSuccess(ArrayList<InviteUserEntity> entities) {
        mAdapter.setList(entities);
    }

    @Override
    public void onGetUserNameSuccess(String name) {

    }

    @Override
    public void onUseNoSuccess() {

    }
}
