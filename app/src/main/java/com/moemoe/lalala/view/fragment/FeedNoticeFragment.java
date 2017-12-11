package com.moemoe.lalala.view.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerFeedNoticeComponent;
import com.moemoe.lalala.di.modules.FeedModule;
import com.moemoe.lalala.di.modules.FeedNoticeModule;
import com.moemoe.lalala.model.entity.BannerEntity;
import com.moemoe.lalala.model.entity.Comment24Entity;
import com.moemoe.lalala.model.entity.DiscoverEntity;
import com.moemoe.lalala.model.entity.FeedNoticeEntity;
import com.moemoe.lalala.model.entity.NewDynamicEntity;
import com.moemoe.lalala.model.entity.ShowFolderEntity;
import com.moemoe.lalala.model.entity.SimpleUserEntity;
import com.moemoe.lalala.model.entity.XianChongEntity;
import com.moemoe.lalala.presenter.FeedNoticeContract;
import com.moemoe.lalala.presenter.FeedNoticePresenter;
import com.moemoe.lalala.utils.BannerImageLoader;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.activity.DynamicActivity;
import com.moemoe.lalala.view.adapter.FeedNoticeAdapter;
import com.moemoe.lalala.view.adapter.XianChongListAdapter;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

/**
 *
 * Created by yi on 2017/9/4.
 */

public class FeedNoticeFragment extends BaseFragment implements FeedNoticeContract.View{

    @BindView(R.id.list)
    PullAndLoadView mListDocs;

    @Inject
    FeedNoticePresenter mPresenter;

    private FeedNoticeAdapter mAdapter;
    private boolean isLoading = false;

    public static FeedNoticeFragment newInstance(){
        return new FeedNoticeFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_onepull;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerFeedNoticeComponent.builder()
                .feedNoticeModule(new FeedNoticeModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mListDocs.setLoadMoreEnabled(true);
        mListDocs.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter= new FeedNoticeAdapter();
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        mListDocs.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                isLoading = true;
                if(mAdapter.getItemCount() == 0){
                    mPresenter.loadFeedNoticeList(0);
                }else {
                    mPresenter.loadFeedNoticeList(mAdapter.getItem(mAdapter.getItemCount() - 1).getTimestamp());
                }
            }

            @Override
            public void onRefresh() {
                isLoading = true;
                mPresenter.loadFeedNoticeList(0);
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
        mPresenter.loadFeedNoticeList(0);
    }

    @Override
    public void onFailure(int code, String msg) {
        isLoading = false;
        mListDocs.setComplete();
    }

    public void release(){
        if(mPresenter != null) mPresenter.release();
        super.release();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onLoadFeedNoticeListSuccess(ArrayList<FeedNoticeEntity> entities, boolean isPull) {
        isLoading = false;
        mListDocs.setComplete();
        if(isPull){
            mAdapter.setList(entities);
        }else {
            mAdapter.addList(entities);
        }
    }
}
