package com.moemoe.lalala.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerFeedComponent;
import com.moemoe.lalala.di.components.DaggerOldDocComponent;
import com.moemoe.lalala.di.modules.FeedModule;
import com.moemoe.lalala.di.modules.OldDocModule;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.BannerEntity;
import com.moemoe.lalala.model.entity.Comment24Entity;
import com.moemoe.lalala.model.entity.DocResponse;
import com.moemoe.lalala.model.entity.NewDynamicEntity;
import com.moemoe.lalala.model.entity.ShowFolderEntity;
import com.moemoe.lalala.model.entity.XianChongEntity;
import com.moemoe.lalala.presenter.FeedContract;
import com.moemoe.lalala.presenter.FeedPresenter;
import com.moemoe.lalala.presenter.OldDocContract;
import com.moemoe.lalala.presenter.OldDocPresenter;
import com.moemoe.lalala.utils.MenuVItemDecoration;
import com.moemoe.lalala.view.activity.DynamicActivity;
import com.moemoe.lalala.view.activity.NewDocDetailActivity;
import com.moemoe.lalala.view.activity.OldDocActivity;
import com.moemoe.lalala.view.adapter.FeedAdapter;
import com.moemoe.lalala.view.adapter.OldDocAdapter;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by yi on 2017/9/4.
 */

public class OldDocFragment extends BaseFragment implements OldDocContract.View{

    @BindView(R.id.list)
    PullAndLoadView mListDocs;

    @Inject
    OldDocPresenter mPresenter;
    private OldDocAdapter mAdapter;
    private boolean isLoading = false;

    public static OldDocFragment newInstance(String userId){
        OldDocFragment fragment = new OldDocFragment();
        Bundle bundle = new Bundle();
        bundle.putString("id",userId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_onepull;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerOldDocComponent.builder()
                .oldDocModule(new OldDocModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        final String userId = getArguments().getString("id");
        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mListDocs.setLoadMoreEnabled(false);
        mListDocs.setLayoutManager(new LinearLayoutManager(getContext()));
        mListDocs.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.bg_f6f6f6));
        mAdapter= new OldDocAdapter();
        mListDocs.getRecyclerView().addItemDecoration(new MenuVItemDecoration((int) getResources().getDimension(R.dimen.y24)));
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                DocResponse docResponse = mAdapter.getItem(position);
                Intent i = new Intent(getContext(),NewDocDetailActivity.class);
                i.putExtra("uuid",docResponse.getId());
                startActivity(i);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mListDocs.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                isLoading = true;
                if(mAdapter.getList().size() == 0){
                    mPresenter.loadOldDocList(userId,0);
                }else {
                    mPresenter.loadOldDocList(userId,mAdapter.getItem(mAdapter.getList().size() - 1).getTimestamp());
                }
            }

            @Override
            public void onRefresh() {
                isLoading = true;
                mPresenter.loadOldDocList(userId,0);
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
        mPresenter.loadOldDocList(userId,0);
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
    public void loadOldDocListSuccess(ArrayList<DocResponse> list, boolean isPull) {
        isLoading = false;
        mListDocs.setComplete();
        mListDocs.setLoadMoreEnabled(true);
        if(isPull){
            mAdapter.setList(list);
        }else {
            mAdapter.addList(list);
        }
    }

    @Override
    public void onBannerLoadSuccess(ArrayList<BannerEntity> bannerEntities) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}
