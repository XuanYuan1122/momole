package com.moemoe.lalala.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerFukuComponent;
import com.moemoe.lalala.di.modules.FukuModule;
import com.moemoe.lalala.model.entity.Live2dModelEntity;
import com.moemoe.lalala.presenter.FukuContract;
import com.moemoe.lalala.presenter.FukuPresenter;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.adapter.FukuSelectAdapter;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

/**
 *
 * Created by yi on 2016/12/2.
 */

public class SelectFukuActivity extends BaseAppCompatActivity implements FukuContract.View{

    public static final int RES_OK = 200;

    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_menu)
    TextView mTvDone;
    @BindView(R.id.tv_toolbar_title)
    TextView mTvTitle;
    @BindView(R.id.rv_list)
    PullAndLoadView mListDocs;

    @Inject
    FukuPresenter mPresenter;

    private String mModel;
    private FukuSelectAdapter mAdapter;
    private boolean isLoading = false;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_bar_list;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
//        ImmersionBar.with(this)
//                .statusBarView(R.id.top_view)
//                .statusBarDarkFont(true,0.2f)
//                .transparentNavigationBar()
//                .init();
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
        DaggerFukuComponent.builder()
                .fukuModule(new FukuModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mModel = PreferenceUtils.getSelectFuku(this);
        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mListDocs.getRecyclerView().setHasFixedSize(true);
        mAdapter = new FukuSelectAdapter(mModel);
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        mListDocs.setLayoutManager(new LinearLayoutManager(this));
        mListDocs.setLoadMoreEnabled(false);
    }

    @Override
    protected void onDestroy() {
        if(mPresenter != null) mPresenter.release();
        super.onDestroy();
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {
        mTvTitle.setText(getString(R.string.label_select_fuku));
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
        mTvDone.setVisibility(View.VISIBLE);
        ViewUtils.setRightMargins(mTvDone, DensityUtil.dip2px(this,18));
        mTvDone.setText(getString(R.string.label_done));
        mTvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                PreferenceUtils.saveSelectFuku(SelectFukuActivity.this,mModel);
                i.putExtra("model",mModel);
                setResult(RES_OK,i);
                finish();
            }
        });
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Live2dModelEntity entity = mAdapter.getItem(position);
                if(entity.isHave()){
                    mModel = entity.getLocalPath();
                    mAdapter.setModel(mModel);
                    mAdapter.notifyDataSetChanged();
                }else {
                    showToast("还没有获得");
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mListDocs.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                isLoading = true;
            }

            @Override
            public void onRefresh() {
                isLoading = true;
                mPresenter.getFukuList();
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }

            @Override
            public boolean hasLoadedAllItems() {
                return false;
            }
        });
        mPresenter.getFukuList();
    }

    @Override
    protected void initData() {
    }

    @Override
    public void onFailure(int code, String msg) {

    }

    @Override
    public void getFukuListSuccess(ArrayList<Live2dModelEntity> list) {
        isLoading = false;
        mListDocs.setComplete();
        mAdapter.setList(list);
    }
}
