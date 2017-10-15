package com.moemoe.lalala.view.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerSubmissionHistoryComponent;
import com.moemoe.lalala.di.modules.SubmissionHistoryModule;
import com.moemoe.lalala.model.entity.SubmissionItemEntity;
import com.moemoe.lalala.presenter.SubmissionHistoryContract;
import com.moemoe.lalala.presenter.SubmissionHistoryPresenter;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.adapter.SubmissionAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

/**
 *
 * Created by yi on 2017/4/27.
 */

public class SubmissionHistoryActivity extends BaseAppCompatActivity implements SubmissionHistoryContract.View{

    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_toolbar_title)
    TextView mTitle;
    @BindView(R.id.rv_list)
    PullAndLoadView mListDocs;
    @Inject
    SubmissionHistoryPresenter mPresenter;
    private SubmissionAdapter mAdapter;
    private boolean isLoading = false;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_bar_list;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
        DaggerSubmissionHistoryComponent.builder()
                .submissionHistoryModule(new SubmissionHistoryModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mTitle.setText("投稿箱");
        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mAdapter = new SubmissionAdapter();
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        mListDocs.setLayoutManager(new LinearLayoutManager(this));
        mListDocs.setLoadMoreEnabled(false);
        mListDocs.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                isLoading = true;
                mPresenter.loadSubmissionList(mAdapter.getItemCount());
            }

            @Override
            public void onRefresh() {
                isLoading = true;
                mPresenter.loadSubmissionList(0);
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
        mPresenter.loadSubmissionList(0);
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
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onFailure(int code,String msg) {
        isLoading = false;
        mListDocs.setComplete();
    }

    @Override
    protected void onDestroy() {
        if(mPresenter != null)mPresenter.release();
        super.onDestroy();
    }

    @Override
    public void onLoadSubmissionListSuccess(ArrayList<SubmissionItemEntity> entities, boolean isPull) {
        isLoading = false;
        mListDocs.setComplete();
        if(entities.size() == 0){
            mListDocs.setLoadMoreEnabled(false);
        }else {
            mListDocs.setLoadMoreEnabled(true);
        }
        if(isPull){
            mAdapter.setList(entities);
        }else {
            mAdapter.addList(entities);
        }
    }
}
