package com.moemoe.lalala.view.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerFeedShowAllComponent;
import com.moemoe.lalala.di.modules.FeedShowAllModule;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.FeedFollowType1Entity;
import com.moemoe.lalala.model.entity.FolderType;
import com.moemoe.lalala.model.entity.ShowFolderEntity;
import com.moemoe.lalala.model.entity.TagFileDelRequest;
import com.moemoe.lalala.presenter.FeedShowAllContract;
import com.moemoe.lalala.presenter.FeedShowAllPresenter;
import com.moemoe.lalala.utils.GridDecoration;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.TopDecoration;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.adapter.FeedShowAllAdapter;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import butterknife.BindView;

/**
 *
 * Created by yi on 2018/1/18.
 */
@SuppressWarnings("unchecked")
public class FeedShowAllActivity extends BaseAppCompatActivity implements FeedShowAllContract.View{

    public static final int TYPE_VERTICAL = 0;
    public static final int TYPE_GRID = 1;
    private String id;
    private String type;

    @Override
    public void onFailure(int code, String msg) {
        mIsLoading = false;
        mListDocs.setComplete();
    }


    @Override
    public void onLoadListSuccess(ArrayList<ShowFolderEntity> entities, boolean isPull) {
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

    @SuppressWarnings("unchecked")
    @Override
    public void onLoadList2Success(ArrayList<FeedFollowType1Entity> entities, boolean isPull) {
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

    @Override
    public void onDelFileSuccess() {
        mAdapter.getList().removeAll(mSelectMap.values());
        mAdapter.notifyDataSetChanged();
        mSelectMap.clear();
    }

    @IntDef(flag = true,value = {
            TYPE_VERTICAL,
            TYPE_GRID
    })
    @Retention(RetentionPolicy.SOURCE)
    private  @interface ShowType{}

    public static void startActivity(Context context,String title,String id,String type,@ShowType int showType,int spanCount,boolean isAdmin){
        Intent i = new Intent(context,FeedShowAllActivity.class);
        i.putExtra("title",title);
        i.putExtra(UUID,id);
        i.putExtra("type",type);
        i.putExtra("showType",showType);
        i.putExtra("spanCount",spanCount);
        i.putExtra("isAdmin",isAdmin);
        context.startActivity(i);
    }

    @BindView(R.id.iv_back)
    View mIvBack;
    @BindView(R.id.tv_toolbar_title)
    TextView mTitle;
    @BindView(R.id.tv_menu)
    TextView mTvEdit;
    @BindView(R.id.rv_list)
    PullAndLoadView mListDocs;

    @Inject
    FeedShowAllPresenter mPresenter;

    private FeedShowAllAdapter mAdapter;
    private boolean mIsLoading = false;
    private boolean mIsSelect;
    private HashMap<Integer,Object> mSelectMap;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_bar_list;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerFeedShowAllComponent.builder()
                .feedShowAllModule(new FeedShowAllModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));

        mSelectMap = new HashMap<>();
        id = getIntent().getStringExtra(UUID);
        type = getIntent().getStringExtra("type");
        int showType = getIntent().getIntExtra("showType",TYPE_VERTICAL);
        int spanCount = getIntent().getIntExtra("spanCount",2);
        boolean mIsAdmin = getIntent().getBooleanExtra("isAdmin",false);
        mListDocs.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        if(showType == TYPE_VERTICAL){
            mListDocs.setLayoutManager(new LinearLayoutManager(this));
        }else if(showType == TYPE_GRID){
            mListDocs.setLayoutManager(new GridLayoutManager(this,spanCount));
            mListDocs.getRecyclerView().setBackgroundColor(Color.WHITE);
            mListDocs.getRecyclerView().addItemDecoration(new TopDecoration(getResources().getDimensionPixelSize(R.dimen.y24)));
        }
        mListDocs.getRecyclerView().addItemDecoration(new GridDecoration(spanCount));
        mListDocs.getRecyclerView().setHasFixedSize(true);
        if(FolderType.MOVIE.toString().equals(type) || FolderType.MH.toString().equals(type)){
            mAdapter = new FeedShowAllAdapter<ShowFolderEntity>(type);
        }else {
            mAdapter = new FeedShowAllAdapter<FeedFollowType1Entity>(type);
        }
        if(mIsAdmin){
            if(!FolderType.WZ.toString().equals(type)){
                mTvEdit.setVisibility(View.GONE);
            }else {
                mTvEdit.setVisibility(View.VISIBLE);
            }
        }else {
            mTvEdit.setVisibility(View.GONE);
        }

        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Object item = mAdapter.getItem(position);
                if(mIsSelect){
                    if(mSelectMap.containsKey(position)){
                        mSelectMap.remove(position);
                    }else {
                        mSelectMap.put(position,item);
                    }
                    if(item instanceof ShowFolderEntity){
                        ShowFolderEntity entity = (ShowFolderEntity) item;
                        entity.setSelect(!entity.isSelect());
                    }else {
                        FeedFollowType1Entity entity = (FeedFollowType1Entity) item;
                        entity.setSelect(!entity.isSelect());
                    }
                    mAdapter.notifyItemChanged(position);
                }else {

                    if(item instanceof ShowFolderEntity){
                        ShowFolderEntity entity = (ShowFolderEntity) item;
                        if(entity.getType().equals("ZH")){
                            NewFileCommonActivity.startActivity(FeedShowAllActivity.this, FolderType.ZH.toString(),entity.getFolderId(),entity.getCreateUser());
                        }else if(entity.getType().equals("TJ")){
                            NewFileCommonActivity.startActivity(FeedShowAllActivity.this,FolderType.TJ.toString(),entity.getFolderId(),entity.getCreateUser());
                        }else if(entity.getType().equals("MH")){
                            NewFileManHuaActivity.startActivity(FeedShowAllActivity.this,FolderType.MH.toString(),entity.getFolderId(),entity.getCreateUser());
                        }else if(entity.getType().equals("XS")){
                            NewFileXiaoshuoActivity.startActivity(FeedShowAllActivity.this,FolderType.XS.toString(),entity.getFolderId(),entity.getCreateUser());
                        }else if(entity.getType().equals(FolderType.WZ.toString())){
                            Intent i = new Intent(FeedShowAllActivity.this,NewDocDetailActivity.class);
                            i.putExtra(UUID,entity.getFolderId());
                            startActivity(i);
                        }
                    }else {
                        //TODO 跳转文章详情 或音乐详情
                        FeedFollowType1Entity entity = (FeedFollowType1Entity) item;
                        if(FolderType.MUSIC.toString().equals(entity.getType())){

                        }else if(FolderType.WZ.toString().equals(entity.getType())){
                            Intent i = new Intent(FeedShowAllActivity.this,NewDocDetailActivity.class);
                            i.putExtra(UUID,entity.getId());
                            startActivity(i);
                        }
                    }
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
                mPresenter.loadList(type,id,mAdapter.getItemCount());
            }

            @Override
            public void onRefresh() {
                mIsLoading = true;
                mPresenter.loadList(type,id,0);
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
        mPresenter.loadList(type,id,0);
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
        mTitle.setText(getIntent().getStringExtra("title"));
        ViewUtils.setRightMargins(mTvEdit,getResources().getDimensionPixelSize(R.dimen.x36));
        mTvEdit.setText("管理");
        mTvEdit.setTextColor(ContextCompat.getColor(FeedShowAllActivity.this,R.color.main_cyan));
        mTvEdit.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if(mIsSelect){
                    if(mSelectMap.size() > 0){
                        TagFileDelRequest request = new TagFileDelRequest();
                        request.setTagId(id);
                        for(Object item : mSelectMap.values()){
                            if(item instanceof ShowFolderEntity){
                                ShowFolderEntity entity = (ShowFolderEntity) item;
                                if(FolderType.MOVIE.toString().equals(type)){
                                    request.addRemove(entity.getUuid(),entity.getFolderId());
                                }else {
                                    request.addRemove(entity.getCreateUser(),entity.getFolderId());
                                }
                            }else {
                                FeedFollowType1Entity entity = (FeedFollowType1Entity) item;
                                request.addRemove(entity.getFolderId(),entity.getId());
                            }
                        }
                        mPresenter.delFile(type,request);
                    }

                }else {
                    mTvEdit.setText("删除");
                }
                mIsSelect = !mIsSelect;
                mAdapter.setShowSelect(mIsSelect);
                mAdapter.notifyDataSetChanged();
            }
        });
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
}
