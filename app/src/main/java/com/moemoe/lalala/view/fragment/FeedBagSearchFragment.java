package com.moemoe.lalala.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerFeedBagSearchComponent;
import com.moemoe.lalala.di.modules.FeedBagSearchModule;
import com.moemoe.lalala.event.SearchChangedEvent;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.FolderType;
import com.moemoe.lalala.model.entity.ShowFolderEntity;
import com.moemoe.lalala.presenter.FeedBagSearchContract;
import com.moemoe.lalala.presenter.FeedBagSearchPresenter;
import com.moemoe.lalala.utils.GridDecoration;
import com.moemoe.lalala.utils.TopDecoration;
import com.moemoe.lalala.view.activity.FileMovieActivity;
import com.moemoe.lalala.view.activity.NewDocDetailActivity;
import com.moemoe.lalala.view.activity.NewFileCommonActivity;
import com.moemoe.lalala.view.activity.NewFileManHuaActivity;
import com.moemoe.lalala.view.activity.NewFileXiaoshuoActivity;
import com.moemoe.lalala.view.adapter.FeedBagAdapter;
import com.moemoe.lalala.view.adapter.OnItemClickListener;
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
 * Created by yi on 2016/12/15.
 */

public class FeedBagSearchFragment extends BaseFragment  implements FeedBagSearchContract.View{

    @BindView(R.id.list)
    PullAndLoadView mListDocs;

    @Inject
    FeedBagSearchPresenter mPresenter;
    private FeedBagAdapter mAdapter;
    private boolean isLoading = false;
    private String mKeyWord;
    private String type;
    private int mCurPage = 1;

    @Override
    protected int getLayoutId() {
        return R.layout.frag_onepull;
    }

    public static FeedBagSearchFragment newInstance(String type){
        FeedBagSearchFragment fragment = new FeedBagSearchFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type",type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerFeedBagSearchComponent.builder()
                .feedBagSearchModule(new FeedBagSearchModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        type = getArguments().getString("type");
        mListDocs.setVisibility(View.VISIBLE);
        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mAdapter = new FeedBagAdapter();
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        mListDocs.getRecyclerView().setLayoutManager(new GridLayoutManager(getContext(),2));
        mListDocs.getRecyclerView().addItemDecoration(new TopDecoration(getResources().getDimensionPixelSize(R.dimen.y24)));
        mListDocs.getRecyclerView().addItemDecoration(new GridDecoration(2));
        mListDocs.getRecyclerView().setHasFixedSize(true);
        mListDocs.setLoadMoreEnabled(false);
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ShowFolderEntity entity = mAdapter.getItem(position);
                if(entity.getType().equals(FolderType.ZH.toString())){
                    NewFileCommonActivity.startActivity(getContext(),FolderType.ZH.toString(),entity.getFolderId(),entity.getCreateUser());
                }else if(entity.getType().equals(FolderType.TJ.toString())){
                    NewFileCommonActivity.startActivity(getContext(),FolderType.TJ.toString(),entity.getFolderId(),entity.getCreateUser());
                }else if(entity.getType().equals(FolderType.MH.toString())){
                    NewFileManHuaActivity.startActivity(getContext(),FolderType.MH.toString(),entity.getFolderId(),entity.getCreateUser());
                }else if(entity.getType().equals(FolderType.XS.toString())){
                    NewFileXiaoshuoActivity.startActivity(getContext(),FolderType.XS.toString(),entity.getFolderId(),entity.getCreateUser());
                }else if(entity.getType().equals(FolderType.WZ.toString())){
                    Intent i = new Intent(getContext(), NewDocDetailActivity.class);
                    i.putExtra("uuid",entity.getFolderId());
                    startActivity(i);
                }else if(FolderType.SP.toString().equals(entity.getType())){
                    FileMovieActivity.startActivity(getContext(),FolderType.SP.toString(),entity.getFolderId(),entity.getCreateUser());
                }else if(FolderType.YY.toString().equals(entity.getType())){
                    FileMovieActivity.startActivity(getContext(),FolderType.YY.toString(),entity.getFolderId(),entity.getCreateUser());
                }else if(FolderType.MOVIE.toString().equals(entity.getType())){
                    //TODO 跳转详情
                }else if(FolderType.MUSIC.toString().equals(entity.getType())){

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
                mPresenter.loadList(type,mKeyWord,mCurPage);
            }

            @Override
            public void onRefresh() {
                isLoading = true;
                mCurPage = 1;
                mPresenter.loadList(type,mKeyWord,mCurPage);
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
        EventBus.getDefault().register(this);
    }

    @Override
    public void onFailure(int code,String msg) {
        isLoading = false;
        mListDocs.setComplete();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void searchChangedEvent(SearchChangedEvent event){
        mKeyWord = event.getKeyWord();
        mCurPage = 1;
        mPresenter.loadList(type,mKeyWord,mCurPage);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void release(){
        if(mPresenter != null) mPresenter.release();
        EventBus.getDefault().unregister(this);
        super.release();
    }

    @Override
    public void onLoadListSuccess(ArrayList<ShowFolderEntity> entities, boolean isPull) {
        isLoading = false;
        mListDocs.setComplete();
        mCurPage++;
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
}
