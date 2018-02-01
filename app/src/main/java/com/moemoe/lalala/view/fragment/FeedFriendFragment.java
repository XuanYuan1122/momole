package com.moemoe.lalala.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerFeedFriendComponent;
import com.moemoe.lalala.di.modules.FeedFriendModule;
import com.moemoe.lalala.event.FollowUserEvent;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.DiscoverEntity;
import com.moemoe.lalala.model.entity.FeedRecommendUserEntity;
import com.moemoe.lalala.model.entity.NewDynamicEntity;
import com.moemoe.lalala.model.entity.SimpleUserEntity;
import com.moemoe.lalala.presenter.FeedFriendContract;
import com.moemoe.lalala.presenter.FeedFriendPresenter;
import com.moemoe.lalala.utils.FeedRecommendUserDecoration;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.adapter.DiscoverAdapter;
import com.moemoe.lalala.view.adapter.FeedRecommendUserAdapter;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

/**
 *
 * Created by yi on 2017/9/4.
 */

public class FeedFriendFragment extends BaseFragment implements FeedFriendContract.View{

    @BindView(R.id.list)
    PullAndLoadView mListDocs;

    @Inject
    FeedFriendPresenter mPresenter;

    private View topView;
    private DiscoverAdapter mAdapter;
    private boolean isLoading = false;
    private FeedRecommendUserAdapter recommendUserAdapter;
    private boolean isLoadTop;

    public static FeedFriendFragment newInstance(){
        return new FeedFriendFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_onepull;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerFeedFriendComponent.builder()
                .feedFriendModule(new FeedFriendModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mListDocs.setLoadMoreEnabled(false);
        mListDocs.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter= new DiscoverAdapter();
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        mListDocs.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                isLoading = true;
                if(mAdapter.getItemCount() > 0){
                    mPresenter.loadDiscoverList(mAdapter.getList().get(mAdapter.getList().size() - 1).getTimestamp());
                }
            }

            @Override
            public void onRefresh() {
                isLoading = true;
                mPresenter.loadRecommendUserList();
                mPresenter.loadDiscoverList(0);
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
        mPresenter.loadRecommendUserList();
        mPresenter.loadDiscoverList(0);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onFailure(int code, String msg) {
        isLoading = false;
        mListDocs.setComplete();
    }

    public void release(){
        if(mPresenter != null) mPresenter.release();
        EventBus.getDefault().unregister(this);
        super.release();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onLikeDynamicSuccess(boolean isLike, int position) {
        NewDynamicEntity entity = new Gson().fromJson(mAdapter.getList().get(position).getObj(),NewDynamicEntity.class);
        entity.setThumb(isLike);
        if(isLike){
            SimpleUserEntity userEntity = new SimpleUserEntity();
            userEntity.setUserName(PreferenceUtils.getAuthorInfo().getUserName());
            userEntity.setUserId(PreferenceUtils.getUUid());
            userEntity.setUserIcon(PreferenceUtils.getAuthorInfo().getHeadPath());
            entity.getThumbUsers().add(0,userEntity);
            entity.setThumbs(entity.getThumbs() + 1);
        }else {
            for(SimpleUserEntity userEntity : entity.getThumbUsers()){
                if(userEntity.getUserId().equals(PreferenceUtils.getUUid())){
                    entity.getThumbUsers().remove(userEntity);
                    break;
                }
            }
            entity.setThumbs(entity.getThumbs() - 1);
        }
        Gson gson = new Gson();
        JsonObject newObj = gson.toJsonTree(entity).getAsJsonObject();
        mAdapter.getList().get(position).setObj(newObj);
        if(mAdapter.getHeaderLayoutCount() != 0){
            mAdapter.notifyItemChanged(position + 1);
        }else {
            mAdapter.notifyItemChanged(position);
        }
    }

    @Override
    public void onLoadRecommendUserListSuccess(ArrayList<FeedRecommendUserEntity> entities) {
        if(entities.size() > 0){
            if(topView == null){
                topView = LayoutInflater.from(getContext()).inflate(R.layout.item_h_top_list, null);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.topMargin = (int) getResources().getDimension(R.dimen.y24);
                topView.setLayoutParams(lp);
                mAdapter.addHeaderView(topView);
            }
            TextView fromName = topView.findViewById(R.id.tv_from_name);
            fromName.setText("推荐关注");
            RecyclerView listView = topView.findViewById(R.id.rv_list);
            listView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
            if(!isLoadTop){
                listView.addItemDecoration(new FeedRecommendUserDecoration());
                isLoadTop = true;
            }
            recommendUserAdapter = new FeedRecommendUserAdapter();
            listView.setAdapter(recommendUserAdapter);
            recommendUserAdapter.setList(entities);
            recommendUserAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    ViewUtils.toPersonal(getContext(),recommendUserAdapter.getItem(position).getUserId());
                }

                @Override
                public void onItemLongClick(View view, int position) {

                }
            });
        }else {
            if(topView != null){
                mAdapter.removeHeaderView(topView);
                topView = null;
            }
        }
    }

    @Override
    public void onFollowUserSuccess(boolean isFollow, int position) {
        if(recommendUserAdapter != null) {
            recommendUserAdapter.getItem(position).setFollow(isFollow);
            recommendUserAdapter.notifyItemChanged(position);
        }
    }

    @Override
    public void onLoadDiscoverListSuccess(ArrayList<DiscoverEntity> entities, boolean isPull) {
        isLoading = false;
        mListDocs.setComplete();
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

    public void likeDynamic(String id,boolean isLie,int position){
        mPresenter.likeDynamic(id, isLie, position);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void followUserEvent(FollowUserEvent event){
        mPresenter.followUser(event.getUserId(),event.isFollow(),event.getPosition());
    }
}
