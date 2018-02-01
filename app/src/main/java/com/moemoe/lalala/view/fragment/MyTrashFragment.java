package com.moemoe.lalala.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;

import com.moemoe.lalala.di.components.DaggerTrashListComponent;
import com.moemoe.lalala.di.modules.TrashListModule;
import com.moemoe.lalala.model.entity.TrashEntity;
import com.moemoe.lalala.presenter.TrashListContract;
import com.moemoe.lalala.presenter.TrashListPresenter;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.view.activity.TrashDetailActivity;
import com.moemoe.lalala.view.activity.TrashFavoriteActivity;
import com.moemoe.lalala.view.adapter.OnItemClickListener;
import com.moemoe.lalala.view.adapter.TrashFavoriteAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by yi on 2016/12/14.
 */

public class MyTrashFragment extends BaseFragment implements TrashListContract.View{

    @BindView(R.id.list)
    PullAndLoadView mListDocs;

    @Inject
    TrashListPresenter mPresenter;
    private TrashFavoriteAdapter mAdapter;
    private boolean isLoading = false;
    private String mType;
    private int mListType;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_one_pulltorefresh_list;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerTrashListComponent.builder()
                .trashListModule(new TrashListModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mType = getArguments().getString(TrashFavoriteActivity.EXTRA_TYPE);
        mListType = getArguments().getInt(TrashFavoriteActivity.EXTRA_LIST_TYPE);
        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mAdapter = new TrashFavoriteAdapter(getContext(),mType);
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        mListDocs.getRecyclerView().setHasFixedSize(true);
        mListDocs.setLayoutManager(new LinearLayoutManager(getContext()));
        mListDocs.setLoadMoreEnabled(false);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                TrashEntity entity = mAdapter.getItem(position);
                Intent i = new Intent(getContext(),TrashDetailActivity.class);
                i.putExtra("type",mType);
               // i.putExtra("id",entity.getDustbinId());
                i.putExtra("item",entity);
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
                mPresenter.doRequest(mAdapter.getItemCount(),mType,mListType);
            }

            @Override
            public void onRefresh() {
                isLoading = true;
                mPresenter.doRequest(0,mType,mListType);
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
        mPresenter.doRequest(0,mType,mListType);
    }

    public void release(){
        if(mPresenter != null) mPresenter.release();
        super.release();
    }

    @Override
    public void onSuccess(ArrayList<TrashEntity> entities,boolean pull) {
        isLoading = false;
        if(mListType == 1){
            for(TrashEntity entity : entities){
                entity.setMark(true);
            }
        }
        mListDocs.setComplete();
        if(entities.size() > 0){
            mListDocs.setLoadMoreEnabled(true);
        }
        if(pull){
            mAdapter.setData(entities);
        }else {
            mAdapter.addData(entities);
        }
    }

    @Override
    public void onFailure(int code,String msg) {
        isLoading = false;
        mListDocs.setComplete();
        ErrorCodeUtils.showErrorMsgByCode(getContext(),code,msg);
    }
}
