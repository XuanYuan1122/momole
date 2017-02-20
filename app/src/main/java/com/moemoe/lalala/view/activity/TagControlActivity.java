package com.moemoe.lalala.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplicationLike;
import com.moemoe.lalala.di.components.DaggerSimpleComponent;
import com.moemoe.lalala.di.modules.SimpleModule;
import com.moemoe.lalala.model.entity.DelTagEntity;
import com.moemoe.lalala.model.entity.DocTagEntity;
import com.moemoe.lalala.presenter.SimpleContract;
import com.moemoe.lalala.presenter.SimplePresenter;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.view.adapter.PersonListAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by yi on 2017/2/10.
 */

public class TagControlActivity extends BaseAppCompatActivity implements SimpleContract.View{

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tv_menu)
    TextView mTvDone;
    @BindView(R.id.rv_list)
    PullAndLoadView mRvList;
    @BindView(R.id.tv_toolbar_title)
    TextView mTvTitle;
    @Inject
    SimplePresenter mPresenter;
    private PersonListAdapter mAdapter;
    private ArrayList<DocTagEntity> mDocTags;
    private ArrayList<String> mDelId;
    private String mDocId;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_select_normal;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerSimpleComponent.builder()
                .simpleModule(new SimpleModule(this))
                .netComponent(MoeMoeApplicationLike.getInstance().getNetComponent())
                .build()
                .inject(this);
        mDocTags = getIntent().getParcelableArrayListExtra("tags");
        mDocId = getIntent().getStringExtra(UUID);
        if(mDocTags == null || TextUtils.isEmpty(mDocId)){
            finish();
            return;
        }
        mDelId = new ArrayList<>();
        mTvTitle.setText("管理标签");
        mRvList.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mAdapter = new PersonListAdapter(this,8);
        mRvList.getRecyclerView().setAdapter(mAdapter);
        mAdapter.setData(mDocTags);
        mRvList.getRecyclerView().setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        mRvList.setLayoutManager(layoutManager);
        mRvList.isLoadMoreEnabled(false);
        mRvList.getSwipeRefreshLayout().setEnabled(false);
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    protected void initListeners() {
        mToolbar.setNavigationOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if(mDelId.size() > 0){
                    DialogUtils.showAbandonModifyDlg(TagControlActivity.this);
                }else {
                    finish();
                }
            }
        });
        mTvDone.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                DelTagEntity entity = new DelTagEntity(mDocId,mDelId);
                createDialog();
                mPresenter.doRequest(entity,7);
            }
        });
    }

    public void delTag(int position){
        if(mDocTags.size() > 1){
            DocTagEntity entity = mDocTags.remove(position);
            mDelId.add(entity.getId());
            mAdapter.removeData(position);
            mAdapter.notifyDataSetChanged();
        }else {
            showToast("至少要有一个标签");
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onSuccess(Object o) {
        finalizeDialog();
        showToast("删除成功");
        Intent i = new Intent();
        i.putParcelableArrayListExtra("tags",mDocTags);
        setResult(RESULT_OK,i);
        finish();
    }

    @Override
    public void onFailure(int code, String msg) {
        finalizeDialog();
        ErrorCodeUtils.showErrorMsgByCode(this,code,msg);
    }
}
