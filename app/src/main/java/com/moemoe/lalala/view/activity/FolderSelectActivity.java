package com.moemoe.lalala.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerBagComponent;
import com.moemoe.lalala.di.modules.BagModule;
import com.moemoe.lalala.model.entity.BagDirEntity;
import com.moemoe.lalala.model.entity.BagEntity;
import com.moemoe.lalala.model.entity.FileEntity;
import com.moemoe.lalala.model.entity.ShowFolderEntity;
import com.moemoe.lalala.presenter.BagContract;
import com.moemoe.lalala.presenter.BagPresenter;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.FolderDecoration;
import com.moemoe.lalala.utils.GridItemDecoration;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.adapter.BagCollectionTopAdapter;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by yi on 2017/1/20.
 */

public class FolderSelectActivity extends BaseAppCompatActivity implements BagContract.View{

    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_toolbar_title)
    TextView mTitle;
    @BindView(R.id.tv_menu)
    TextView mTvDone;
    @BindView(R.id.rv_list)
    PullAndLoadView mListDocs;

    @Inject
    BagPresenter mPresenter;
    private BagCollectionTopAdapter mAdapter;
    private boolean isLoading = false;
    private String mSelectId;
    private String mFolderId;
    private String mFolderType;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_bar_list;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
        DaggerBagComponent.builder()
                .bagModule(new BagModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mFolderId = getIntent().getStringExtra("folderId");
        mFolderType = getIntent().getStringExtra("folderType");
        mTvDone.setVisibility(View.VISIBLE);
        mTvDone.getPaint().setFakeBoldText(true);
        ViewUtils.setRightMargins(mTvDone,DensityUtil.dip2px(this,18));
        mTvDone.setText(getString(R.string.label_done));
        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mAdapter = new BagCollectionTopAdapter();
        mAdapter.setSelect(true);
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        GridLayoutManager layoutManager = new GridLayoutManager(this,3);
        mListDocs.getRecyclerView().addItemDecoration(new FolderDecoration());
        mListDocs.setLayoutManager(layoutManager);
        mListDocs.getRecyclerView().addItemDecoration(new GridItemDecoration(DensityUtil.dip2px(this,10)));
        mListDocs.setLoadMoreEnabled(false);
        mTitle.setText("选择文件夹");

    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    protected void onDestroy() {
        if(mPresenter != null)mPresenter.release();
        super.onDestroy();
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
        mTvDone.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                Intent i = new Intent();
                i.putExtra("folderId",mSelectId);
                setResult(RESULT_OK,i);
                finish();
            }
        });
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ShowFolderEntity entity = mAdapter.getItem(position);
                if(TextUtils.isEmpty(mFolderId) || !mFolderId.equals(entity.getFolderId())){
                    mSelectId = entity.getFolderId();
                    for(ShowFolderEntity temp : mAdapter.getList()){
                        temp.setSelect(false);
                    }
                    entity.setSelect(true);
                    mAdapter.notifyDataSetChanged();
                }else {
                    showToast("不能选择当前文件夹");
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mListDocs.getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if(!isFinishing()) Glide.with(FolderSelectActivity.this).resumeRequests();
                } else {
                    if(!isFinishing())Glide.with(FolderSelectActivity.this).pauseRequests();
                }
            }
        });
        mListDocs.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                isLoading = true;
                mPresenter.getFolderList(PreferenceUtils.getUUid(),mAdapter.getItemCount(),mFolderType);
            }

            @Override
            public void onRefresh() {
                isLoading = true;
                mPresenter.getFolderList(PreferenceUtils.getUUid(),0,mFolderType);
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
        mPresenter.getFolderList(PreferenceUtils.getUUid(),0,mFolderType);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void openOrModifyBagSuccess() {

    }

    @Override
    public void loadBagInfoSuccess(BagEntity entity) {

    }

    @Override
    public void loadFolderListSuccess(ArrayList<ShowFolderEntity> entities, boolean isPull) {
        isLoading = false;
        mListDocs.setComplete();
        mListDocs.setLoadMoreEnabled(true);
        if(isPull){
            mAdapter.setList(entities);
        }else {
            mAdapter.addList(entities);
        }
    }

    @Override
    public void createFolderSuccess() {

    }

    @Override
    public void uploadFolderSuccess() {

    }

    @Override
    public void loadFolderItemListSuccess(ArrayList<FileEntity> entities, boolean isPull) {

    }

    @Override
    public void onCheckSize(boolean isOk) {

    }

    @Override
    public void onBuyFolderSuccess() {

    }

    @Override
    public void deleteFolderSuccess() {

    }

    @Override
    public void modifyFolderSuccess() {

    }

    @Override
    public void onFollowOrUnFollowFolderSuccess(boolean follow) {

    }

    @Override
    public void onLoadFolderSuccess(BagDirEntity entity) {

    }

    @Override
    public void onLoadFolderFail() {

    }

    @Override
    public void onFailure(int code, String msg) {

    }
}
