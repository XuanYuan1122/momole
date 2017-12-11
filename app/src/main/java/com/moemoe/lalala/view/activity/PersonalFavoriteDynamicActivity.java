package com.moemoe.lalala.view.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerFeedComponent;
import com.moemoe.lalala.di.modules.FeedModule;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.BannerEntity;
import com.moemoe.lalala.model.entity.Comment24Entity;
import com.moemoe.lalala.model.entity.DiscoverEntity;
import com.moemoe.lalala.model.entity.NewDynamicEntity;
import com.moemoe.lalala.model.entity.ShowFolderEntity;
import com.moemoe.lalala.model.entity.SimpleUserEntity;
import com.moemoe.lalala.model.entity.XianChongEntity;
import com.moemoe.lalala.presenter.FeedContract;
import com.moemoe.lalala.presenter.FeedPresenter;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.adapter.FeedAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by yi on 2017/9/26.
 */

public class PersonalFavoriteDynamicActivity extends BaseAppCompatActivity implements FeedContract.View{

    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_toolbar_title)
    TextView mTitle;
    @BindView(R.id.rv_list)
    PullAndLoadView mListDocs;
    @Inject
    FeedPresenter mPresenter;
    private FeedAdapter mAdapter;
    private boolean isLoading = false;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_bar_list;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
        DaggerFeedComponent.builder()
                .feedModule(new FeedModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mAdapter = new FeedAdapter();
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        mListDocs.setLayoutManager(new LinearLayoutManager(PersonalFavoriteDynamicActivity.this));
        mListDocs.setLoadMoreEnabled(false);
        mListDocs.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                isLoading = true;
                mPresenter.loadList(mAdapter.getList().size());
            }

            @Override
            public void onRefresh() {
                isLoading = true;
                mPresenter.loadList(0);
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
        mPresenter.loadList(0);
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {
        mIvBack.setVisibility(View.VISIBLE);
        mIvBack.setImageResource(R.drawable.btn_back_black_normal);
        mIvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mTitle.setText("收藏");
    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void initData() {

    }

    @Override
    public void onFailure(int code, String msg) {
        isLoading = false;
        mListDocs.setComplete();
    }

    @Override
    protected void onDestroy() {
        if(mPresenter != null) mPresenter.release();
        super.onDestroy();
    }

    public void likeDynamic(String id,boolean isLie,int position){
        mPresenter.likeDynamic(id, isLie, position);
    }

    @Override
    public void onLoadListSuccess(ArrayList<NewDynamicEntity> resList, boolean isPull) {
        isLoading = false;
        mListDocs.setComplete();
        if (resList.size() >= ApiService.LENGHT){
            mListDocs.setLoadMoreEnabled(true);
        }else {
            mListDocs.setLoadMoreEnabled(false);
        }
        if(isPull){
            mAdapter.setList(resList);
        }else {
            mAdapter.addList(resList);
        }
    }

    @Override
    public void onBannerLoadSuccess(ArrayList<BannerEntity> bannerEntities) {

    }

    @Override
    public void onLoadXianChongSuccess(ArrayList<XianChongEntity> entities) {

    }

    @Override
    public void onLoadFolderSuccess(ArrayList<ShowFolderEntity> entities) {

    }

    @Override
    public void onLoadCommentSuccess(Comment24Entity entity) {

    }

    @Override
    public void onLikeDynamicSuccess(boolean isLike, int position) {
        mAdapter.getList().get(position).setThumb(isLike);
        if(isLike){
            SimpleUserEntity userEntity = new SimpleUserEntity();
            userEntity.setUserName(PreferenceUtils.getAuthorInfo().getUserName());
            userEntity.setUserId(PreferenceUtils.getUUid());
            userEntity.setUserIcon(PreferenceUtils.getAuthorInfo().getHeadPath());
            mAdapter.getList().get(position).getThumbUsers().add(0,userEntity);
            mAdapter.getList().get(position).setThumbs(mAdapter.getList().get(position).getThumbs() + 1);
        }else {
            for(SimpleUserEntity userEntity : mAdapter.getList().get(position).getThumbUsers()){
                if(userEntity.getUserId().equals(PreferenceUtils.getUUid())){
                    mAdapter.getList().get(position).getThumbUsers().remove(userEntity);
                    break;
                }
            }
            mAdapter.getList().get(position).setThumbs(mAdapter.getList().get(position).getThumbs() - 1);
        }
        mAdapter.notifyItemChanged(position);
    }

    @Override
    public void onLoadDiscoverListSuccess(ArrayList<DiscoverEntity> entities, boolean isPull) {

    }
}
