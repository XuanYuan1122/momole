package com.moemoe.lalala.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerVideoExamineComponent;
import com.moemoe.lalala.di.modules.VideoExamineModule;
import com.moemoe.lalala.model.entity.StreamFileEntity;
import com.moemoe.lalala.model.entity.SubmissionItemEntity;
import com.moemoe.lalala.presenter.VideoExamineContract;
import com.moemoe.lalala.presenter.VideoExaminePresenter;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.adapter.SubmissionAdapter;
import com.moemoe.lalala.view.adapter.VideoExamineAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

/**
 *
 * Created by yi on 2017/4/27.
 */

public class VideoExamineActivity extends BaseAppCompatActivity implements VideoExamineContract.View{

    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_toolbar_title)
    TextView mTitle;
    @BindView(R.id.rv_list)
    PullAndLoadView mListDocs;

    @Inject
    VideoExaminePresenter mPresenter;
    private VideoExamineAdapter mAdapter;
    private boolean isLoading = false;

    public static void startActivity(Context context){
        Intent i = new Intent(context,VideoExamineActivity.class);
        context.startActivity(i);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ac_bar_list;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
        DaggerVideoExamineComponent.builder()
                .videoExamineModule(new VideoExamineModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mTitle.setText("视频审核");
        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mAdapter = new VideoExamineAdapter();
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        mListDocs.setLayoutManager(new LinearLayoutManager(this));
        mListDocs.setLoadMoreEnabled(false);
        mListDocs.getRecyclerView().setHasFixedSize(true);
        mListDocs.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                isLoading = true;
                mPresenter.loadExamineList(mAdapter.getItemCount());
            }

            @Override
            public void onRefresh() {
                isLoading = true;
                mPresenter.loadExamineList(0);
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
        isLoading = true;
        mPresenter.loadExamineList(0);
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
    public void onLoadExamineListSuccess(ArrayList<StreamFileEntity> entities, boolean isPull) {
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
