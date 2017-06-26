package com.moemoe.lalala.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerBagFavoriteComponent;
import com.moemoe.lalala.di.modules.BagFavoriteModule;
import com.moemoe.lalala.model.entity.BagDirEntity;
import com.moemoe.lalala.presenter.BagFavoriteContract;
import com.moemoe.lalala.presenter.BagFavoritePresenter;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.adapter.OnItemClickListener;
import com.moemoe.lalala.view.adapter.PersonListAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by yi on 2017/1/19.
 */

public class BagBuyActivity extends BaseAppCompatActivity implements BagFavoriteContract.View{

    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_toolbar_title)
    TextView mTitle;
    @BindView(R.id.tv_menu)
    TextView mTvSelect;
    @BindView(R.id.rv_list)
    PullAndLoadView mListDocs;

    @Inject
    BagFavoritePresenter mPresenter;

    private PersonListAdapter mAdapter;
    private boolean isSelect;
    private boolean isLoading = false;
    private HashMap<Integer,BagDirEntity> mSelectMap;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_bag_favorite;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerBagFavoriteComponent.builder()
                .bagFavoriteModule(new BagFavoriteModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mTitle.setText("书包购买");
        mTvSelect.setVisibility(View.VISIBLE);
        mTvSelect.getPaint().setFakeBoldText(true);
        ViewUtils.setRightMargins(mTvSelect, DensityUtil.dip2px(this,18));
        mTvSelect.setText("选择");
        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mAdapter = new PersonListAdapter(this,7);
        mListDocs.getRecyclerView().setHasFixedSize(true);
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        mListDocs.setLayoutManager(new LinearLayoutManager(this));
        mListDocs.setLoadMoreEnabled(false);
        isSelect = false;
        mSelectMap = new HashMap<>();
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    protected void initListeners() {
        mIvBack.setVisibility(View.VISIBLE);
        mIvBack.setImageResource(R.drawable.btn_back_black_normal);
        mIvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if(isSelect){
                    isSelect = !isSelect;
                    mTvSelect.setText("选择");
                    mAdapter.setCanDelete(isSelect);
                    mAdapter.notifyDataSetChanged();
                }else {
                    finish();
                }
            }
        });
        mTvSelect.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if(!isSelect){
                    isSelect = !isSelect;
                    mTvSelect.setText("删除");
                    mSelectMap.clear();
                    mAdapter.setCanDelete(isSelect);
                    mAdapter.notifyDataSetChanged();
                }else {
                    if(mSelectMap.size() > 0) mPresenter.deleteFavoriteList(mSelectMap);
                }
            }
        });
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                BagDirEntity entity = (BagDirEntity) mAdapter.getItem(position);
                if(!isSelect){
                    Intent i = new Intent(BagBuyActivity.this,FolderActivity.class);
                    i.putExtra("info",entity);
                    i.putExtra("show_more",true);
                    i.putExtra(UUID, entity.getUserId());
                    startActivity(i);
                }else {
                    if(entity.isSelect()){
                        mSelectMap.remove(position);
                        entity.setSelect(false);
                    }else {
                        mSelectMap.put(position,entity);
                        entity.setSelect(true);
                    }
                    mAdapter.notifyItemChanged(position);
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
                mPresenter.getFavoriteList(mAdapter.getItemCount());
            }

            @Override
            public void onRefresh() {
                isLoading = true;
                mPresenter.getFavoriteList(0);
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
        mPresenter.getFavoriteList(0);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void loadListSuccess(ArrayList<BagDirEntity> entities, boolean isPull) {
        isLoading = false;
        if(entities.size() == 0){
            mListDocs.setLoadMoreEnabled(false);
        }else {
            mListDocs.setLoadMoreEnabled(true);
        }
        mListDocs.setComplete();
        if(isPull){
            mAdapter.setData(entities);
        }else {
            mAdapter.addData(entities);
        }
    }

    @Override
    public void deleteSuccess() {
        for(BagDirEntity entity : mSelectMap.values()){
            mAdapter.getList().remove(entity);
        }
        mAdapter.notifyDataSetChanged();
        mSelectMap.clear();
    }

    @Override
    public void onFailure(int code, String msg) {
        isLoading = false;
        mListDocs.setComplete();
    }

    @Override
    protected void onDestroy() {
        if(mPresenter != null)mPresenter.release();
        super.onDestroy();
    }
}
