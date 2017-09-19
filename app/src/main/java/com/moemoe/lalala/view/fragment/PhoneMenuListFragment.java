package com.moemoe.lalala.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerPhoneMenuListComponent;
import com.moemoe.lalala.di.modules.PhoneMenuListModule;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.PhoneMenuEntity;
import com.moemoe.lalala.presenter.PhoneMenuListContract;
import com.moemoe.lalala.presenter.PhoneMenuListPresenter;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.view.activity.NewPersonalActivity;
import com.moemoe.lalala.view.adapter.PhoneMenuListAdapter;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by yi on 2017/9/4.
 */

public class PhoneMenuListFragment extends BaseFragment implements PhoneMenuListContract.View{

    @BindView(R.id.list)
    PullAndLoadView mListDocs;
    @Inject
    PhoneMenuListPresenter mPresenter;
    private PhoneMenuListAdapter mAdapter;
    private boolean isLoading = false;

    public static PhoneMenuListFragment newInstance(String type){
        PhoneMenuListFragment fragment = new PhoneMenuListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type",type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_onepull;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerPhoneMenuListComponent.builder()
                .phoneMenuListModule(new PhoneMenuListModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        final String type = getArguments().getString("type");
        mListDocs.getSwipeRefreshLayout().setEnabled(false);
        mListDocs.setLoadMoreEnabled(false);
        mListDocs.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter= new PhoneMenuListAdapter();
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                PhoneMenuEntity entity = mAdapter.getItem(position);
                if(!entity.getUserId().equals(PreferenceUtils.getUUid())){
                    Intent i = new Intent(getContext(), NewPersonalActivity.class);
                    i.putExtra("uuid",entity.getUserId());
                    startActivity(i);
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
                mPresenter.loadUserList(type,mAdapter.getItemCount());
            }

            @Override
            public void onRefresh() {
                isLoading = true;
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
        mPresenter.loadUserList(type,0);
    }

    @Override
    public void onFailure(int code, String msg) {
        isLoading = false;
        mListDocs.setComplete();
    }

    @Override
    public void onLoadUserListSuccess(ArrayList<PhoneMenuEntity> entities, boolean isPull) {
        isLoading = false;
        mListDocs.setComplete();
        if (entities.size() >= ApiService.LENGHT){
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

    public void release(){
        if(mPresenter != null) mPresenter.release();
        super.release();
    }
}
