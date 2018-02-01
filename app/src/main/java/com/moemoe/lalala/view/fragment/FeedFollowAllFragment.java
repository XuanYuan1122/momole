package com.moemoe.lalala.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerFeedFollowAllComponent;
import com.moemoe.lalala.di.modules.FeedFollowAllModule;
import com.moemoe.lalala.event.RefreshListEvent;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.FeedFollowType1Entity;
import com.moemoe.lalala.model.entity.FolderType;
import com.moemoe.lalala.model.entity.ShowFolderEntity;
import com.moemoe.lalala.presenter.FeedFollowAllContract;
import com.moemoe.lalala.presenter.FeedFollowAllPresenter;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.view.activity.FileMovieActivity;
import com.moemoe.lalala.view.activity.NewDocDetailActivity;
import com.moemoe.lalala.view.activity.NewFileCommonActivity;
import com.moemoe.lalala.view.activity.NewFileManHuaActivity;
import com.moemoe.lalala.view.activity.NewFileXiaoshuoActivity;
import com.moemoe.lalala.view.adapter.FeedFollowAllAdapter;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * feed流关注所有
 * Created by yi on 2018/1/11.
 */

public class FeedFollowAllFragment extends BaseFragment implements FeedFollowAllContract.View {

    @BindView(R.id.tv_notice)
    TextView mTvNotice;
    @BindView(R.id.list)
    PullAndLoadView mList;

    @Inject
    FeedFollowAllPresenter mPresenter;

    private FeedFollowAllAdapter mAdapter;
    private boolean isLoading = false;

    public static FeedFollowAllFragment newInstance(){
        return new FeedFollowAllFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_feed_follow_all;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerFeedFollowAllComponent.builder()
                .feedFollowAllModule(new FeedFollowAllModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);

        mList.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mList.setLoadMoreEnabled(false);
        mList.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter = new FeedFollowAllAdapter();
        mList.getRecyclerView().setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                FeedFollowType1Entity entity = mAdapter.getItem(position);
                if(entity.getType().equals(FolderType.ZH.toString())){
                    NewFileCommonActivity.startActivity(getContext(),FolderType.ZH.toString(),entity.getId(),entity.getUserId());
                }else if(entity.getType().equals(FolderType.TJ.toString())){
                    NewFileCommonActivity.startActivity(getContext(),FolderType.TJ.toString(),entity.getId(),entity.getUserId());
                }else if(entity.getType().equals(FolderType.MH.toString())){
                    NewFileManHuaActivity.startActivity(getContext(),FolderType.MH.toString(),entity.getId(),entity.getUserId());
                }else if(entity.getType().equals(FolderType.XS.toString())){
                    NewFileXiaoshuoActivity.startActivity(getContext(),FolderType.XS.toString(),entity.getId(),entity.getUserId());
                }else if(entity.getType().equals(FolderType.YY.toString())){
                    FileMovieActivity.startActivity(getContext(),FolderType.YY.toString(),entity.getId(),entity.getUserId());
                }else if(entity.getType().equals(FolderType.SP.toString())){
                    FileMovieActivity.startActivity(getContext(),FolderType.SP.toString(),entity.getId(),entity.getUserId());
                }else if(entity.getType().equals(FolderType.WZ.toString())){
                    Intent i = new Intent(getContext(), NewDocDetailActivity.class);
                    i.putExtra("uuid", entity.getId());
                    startActivity(i);
                }else if("MOVIE".equals(entity.getType())){
                    //TODO 视频详情页
                }else if("MUSIC".equals(entity.getType())){

                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mList.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                isLoading = true;
                mPresenter.loadList(mAdapter.getItemCount());
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
        isLoading = true;
        mPresenter.loadList(0);
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshList(RefreshListEvent event){
        if("follow_all".equals(event.getTypeId())){
            mPresenter.loadList(0);
        }
    }

    @Override
    public void onFailure(int code, String msg) {
        isLoading = false;
        mList.setComplete();
    }

    @Override
    public void onLoadListSuccess(ArrayList<FeedFollowType1Entity> entities,boolean isPull) {
        isLoading = false;
        mList.setComplete();
        if(entities.size() >= ApiService.LENGHT){
            mList.setLoadMoreEnabled(true);
        }else {
            mList.setLoadMoreEnabled(false);
        }
        if(isPull){
            showUpdateNum(entities);
            mAdapter.setList(entities);
        }else {
            mAdapter.addList(entities);
        }
    }

    private void showUpdateNum(ArrayList<FeedFollowType1Entity> entities){
        int num = 0;
        if(entities.size() > 0){
            int position = entities.get(0).getPosition();
            int lastPosition = PreferenceUtils.getLastFeedPosition(getContext(),"follow_all");
            num = position - lastPosition;
            PreferenceUtils.setLastFeedPosition(getContext(),"follow_all",position);
        }
        if(num > 0){
            mTvNotice.setVisibility(View.VISIBLE);
            mTvNotice.setText(String.format(getString(R.string.label_update_num),num));
            Observable.timer(2, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Long>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Long aLong) {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {
                            mTvNotice.setVisibility(View.GONE);
                        }
                    });
        }
    }

    @Override
    public void release() {
        if(mPresenter != null){
            mPresenter.release();
        }
        EventBus.getDefault().unregister(this);
    }
}
