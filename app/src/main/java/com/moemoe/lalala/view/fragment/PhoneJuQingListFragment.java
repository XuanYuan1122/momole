package com.moemoe.lalala.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerPhoneJuQingComponent;
import com.moemoe.lalala.di.modules.PhoneJuQingModule;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.JuQingEntity;
import com.moemoe.lalala.presenter.PhoneJuQingListContract;
import com.moemoe.lalala.presenter.PhoneJuQingListPresenter;
import com.moemoe.lalala.view.adapter.PhoneJuQingListAdapter;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by yi on 2017/9/4.
 */

public class PhoneJuQingListFragment extends BaseFragment implements PhoneJuQingListContract.View{

    @BindView(R.id.list)
    PullAndLoadView mListDocs;

    @Inject
    PhoneJuQingListPresenter mPresenter;
    private PhoneJuQingListAdapter mAdapter;
    private boolean isLoading = false;
    private int mFilter;
    private int mType;

    public static PhoneJuQingListFragment newInstance(int type){
        PhoneJuQingListFragment fragment = new PhoneJuQingListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type",type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_onepull;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerPhoneJuQingComponent.builder()
                .phoneJuQingModule(new PhoneJuQingModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mType = getArguments().getInt("type");
        mFilter = 0;
        mListDocs.getSwipeRefreshLayout().setEnabled(false);
        mListDocs.setLoadMoreEnabled(false);
        mListDocs.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter= new PhoneJuQingListAdapter();
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mListDocs.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                isLoading = true;
                mPresenter.loadUserList(mType,mFilter,mAdapter.getItemCount());
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
        mPresenter.loadUserList(mType,mFilter,0);
    }

    @Override
    public void onFailure(int code, String msg) {
        isLoading = false;
        mListDocs.setComplete();
    }

    /**
     *
     * @param type 0全部 1.攻略中 2.已完成 3.未解锁
     */
    public void changeFilter(int type){
        if(mPresenter != null){
            if(mFilter == type){
                return;
            }
            mFilter = type;
            mPresenter.loadUserList(mType,mFilter,0);
        }
    }

    @Override
    public void onLoadUserListSuccess(ArrayList<JuQingEntity> entities, boolean isPull) {
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
