package com.moemoe.lalala.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerFeedBagComponent;
import com.moemoe.lalala.di.modules.FeedBagModule;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.FolderType;
import com.moemoe.lalala.model.entity.ShowFolderEntity;
import com.moemoe.lalala.presenter.FeedBagContract;
import com.moemoe.lalala.presenter.FeedBagPresenter;
import com.moemoe.lalala.utils.GridDecoration;
import com.moemoe.lalala.utils.TopDecoration;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.adapter.FeedBagAdapter;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

/**
 *
 * Created by yi on 2018/1/18.
 */

public class FeedBagTypeListActivity extends BaseAppCompatActivity implements FeedBagContract.View{

    @Override
    public void onFailure(int code, String msg) {
        mIsLoading = false;
        mListDocs.setComplete();
    }

    public static void startActivity(Context context,String type){
        Intent i = new Intent(context,FeedBagTypeListActivity.class);
        i.putExtra("type",type);
        context.startActivity(i);
    }

    @BindView(R.id.iv_back)
    View mIvBack;
    @BindView(R.id.tv_toolbar_title)
    TextView mTitle;
    @BindView(R.id.rv_list)
    PullAndLoadView mListDocs;

    @Inject
    FeedBagPresenter mPresenter;

    private FeedBagAdapter mAdapter;
    private boolean mIsLoading = false;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_bar_list;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerFeedBagComponent.builder()
                .feedBagModule(new FeedBagModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
        final String type = getIntent().getStringExtra("type");
        if(type.equals(FolderType.ZH.toString())){
            mTitle.setText("综合");
        }else if(type.equals(FolderType.TJ.toString())){
            mTitle.setText("图集");
        }else if(type.equals(FolderType.MH.toString())){
            mTitle.setText("漫画");
        }else if(type.equals(FolderType.XS.toString())){
            mTitle.setText("小说");
        }else if(type.equals(FolderType.YY.toString())){
            mTitle.setText("音乐");
        }else if(type.equals(FolderType.SP.toString())){
            mTitle.setText("视频");
        }

        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        int padding = getResources().getDimensionPixelSize(R.dimen.x24);
        mListDocs.setPadding(padding,0,padding,0);
        mListDocs.getRecyclerView().setLayoutManager(new GridLayoutManager(this,2));
        mListDocs.getRecyclerView().addItemDecoration(new TopDecoration(getResources().getDimensionPixelSize(R.dimen.y24)));
        mListDocs.getRecyclerView().addItemDecoration(new GridDecoration(2));
        mListDocs.getRecyclerView().setHasFixedSize(true);
        mAdapter = new FeedBagAdapter();
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ShowFolderEntity entity = mAdapter.getItem(position);
                if(entity.getType().equals(FolderType.ZH.toString())){
                    NewFileCommonActivity.startActivity(FeedBagTypeListActivity.this,FolderType.ZH.toString(),entity.getFolderId(),entity.getCreateUser());
                }else if(entity.getType().equals(FolderType.TJ.toString())){
                    NewFileCommonActivity.startActivity(FeedBagTypeListActivity.this,FolderType.TJ.toString(),entity.getFolderId(),entity.getCreateUser());
                }else if(entity.getType().equals(FolderType.MH.toString())){
                    NewFileManHuaActivity.startActivity(FeedBagTypeListActivity.this,FolderType.MH.toString(),entity.getFolderId(),entity.getCreateUser());
                }else if(entity.getType().equals(FolderType.XS.toString())){
                    NewFileXiaoshuoActivity.startActivity(FeedBagTypeListActivity.this,FolderType.XS.toString(),entity.getFolderId(),entity.getCreateUser());
                }else if(entity.getType().equals(FolderType.YY.toString())){
                    //TODO 跳转音乐
                }else if(entity.getType().equals(FolderType.SP.toString())){
                    //TODO 跳转视频
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        mListDocs.setLoadMoreEnabled(false);
        mListDocs.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                mIsLoading = true;
                mPresenter.loadFeedBagList(type,mAdapter.getItemCount());
            }

            @Override
            public void onRefresh() {
                mIsLoading = true;
                mPresenter.loadFeedBagList(type,0);
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
        mPresenter.loadFeedBagList(type,0);
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
    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onDestroy() {
        if(mPresenter != null) mPresenter.release();
        super.onDestroy();
    }

    @Override
    public void onLoadHotBagSuccess(ArrayList<ShowFolderEntity> entities) {

    }

    @Override
    public void onLoadFeedBagListSuccess(ArrayList<ShowFolderEntity> entities, boolean isPull) {
        mIsLoading = false;
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
}
