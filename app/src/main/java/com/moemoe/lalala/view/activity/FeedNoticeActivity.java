package com.moemoe.lalala.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerFeedNoticeComponent;
import com.moemoe.lalala.di.modules.FeedNoticeModule;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.FeedNoticeEntity;
import com.moemoe.lalala.model.entity.FeedRecommendUserEntity;
import com.moemoe.lalala.presenter.FeedNoticeContract;
import com.moemoe.lalala.presenter.FeedNoticePresenter;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.adapter.FeedNoticeAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

/**
 *
 * Created by yi on 2018/1/18.
 */

public class FeedNoticeActivity extends BaseAppCompatActivity implements FeedNoticeContract.View{

    @Override
    public void onFailure(int code, String msg) {
        mIsLoading = false;
        mListDocs.setComplete();
    }

    public static void startActivity(Context context){
        Intent i = new Intent(context,FeedNoticeActivity.class);
        context.startActivity(i);
    }

    @BindView(R.id.iv_back)
    View mIvBack;
    @BindView(R.id.tv_toolbar_title)
    TextView mTitle;
    @BindView(R.id.rv_list)
    PullAndLoadView mListDocs;

    @Inject
    FeedNoticePresenter mPresenter;

    private FeedNoticeAdapter mAdapter;
    private boolean mIsLoading = false;
    private long followTime;
    private long notifyTime;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_bar_list;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerFeedNoticeComponent.builder()
                .feedNoticeModule(new FeedNoticeModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));

        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mListDocs.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new FeedNoticeAdapter();
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        mListDocs.setLoadMoreEnabled(false);
        mListDocs.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                mIsLoading = true;
                if(mAdapter.getItemCount() == 0){
                    followTime = notifyTime = 0;
                    mPresenter.loadFeedNoticeList("NOTIFY",followTime,notifyTime);
                }else {
                    mPresenter.loadFeedNoticeList("NOTIFY",followTime,notifyTime);
                }
            }

            @Override
            public void onRefresh() {
                mIsLoading = true;
                followTime = notifyTime = 0;
                mPresenter.loadFeedNoticeList("NOTIFY",followTime,notifyTime);
            }

            @Override
            public boolean isLoading() {
                return mIsLoading;
            }

            @Override
            public boolean hasLoadedAllItems() {
                return false;
            }
        });
        mIsLoading = true;
        mPresenter.loadFeedNoticeList("NOTIFY",followTime,notifyTime);
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {
        mIvBack.setVisibility(View.VISIBLE);
        mIvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mTitle.setTextColor(ContextCompat.getColor(this,R.color.main_cyan));
        mTitle.setText("通知");
    }

    @Override
    protected void onDestroy() {
        if(mPresenter != null) mPresenter.release();
        super.onDestroy();
    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void initData() {

    }

    @Override
    public void onLoadFeedNoticeListSuccess(ArrayList<FeedNoticeEntity> entities, boolean isPull) {
        mIsLoading = false;
        mListDocs.setComplete();
        getNextTime(entities);
        if(entities.size() >= ApiService.LENGHT){
            mListDocs.setLoadMoreEnabled(true);
        }else {
            mListDocs.setLoadMoreEnabled(false);
        }
        if(isPull){
            mAdapter.setList(entities);
        }else {
            mAdapter.addList(entities);
        }
    }

    private void getNextTime(ArrayList<FeedNoticeEntity> entities){
        boolean followSet = false;
        boolean notifySet = false;

        for(int i = entities.size() - 1;i >= 0;i--){
            FeedNoticeEntity entity = entities.get(i);
            if("doc".equals(entity.getNotifyType()) || "dynamic".equals(entity.getNotifyType())){
                if(!followSet){
                    followTime = entity.getTimestamp();
                }
                followSet = true;
            }else {
                if(!notifySet){
                    notifyTime = entity.getTimestamp();
                }
                notifySet = true;
            }
            if(followSet && notifySet){
                break;
            }
        }
    }

    @Override
    public void onLikeDynamicSuccess(boolean isLike, int position) {

    }

    @Override
    public void onLoadRecommendUserListSuccess(ArrayList<FeedRecommendUserEntity> entities) {

    }

    @Override
    public void onFollowUserSuccess(boolean isFollow, int position) {

    }
}
