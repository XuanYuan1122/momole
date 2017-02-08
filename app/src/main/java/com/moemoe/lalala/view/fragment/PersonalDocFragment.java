package com.moemoe.lalala.view.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplicationLike;
import com.moemoe.lalala.di.components.DaggerPersonalListComponent;
import com.moemoe.lalala.di.modules.PersonalListModule;
import com.moemoe.lalala.model.entity.PersonDocEntity;
import com.moemoe.lalala.presenter.PersonaListPresenter;
import com.moemoe.lalala.presenter.PersonalListContract;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.view.adapter.OnItemClickListener;
import com.moemoe.lalala.view.adapter.PersonListAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by yi on 2016/12/15.
 */

public class PersonalDocFragment extends BaseFragment implements PersonalListContract.View{

    @BindView(R.id.list)
    PullAndLoadView mListDocs;
    @Inject
    PersonaListPresenter mPresenter;
    private PersonListAdapter mAdapter;
    private boolean isLoading = false;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_simple_pulltorefresh_list;
    }

    public static PersonalDocFragment newInstance(String id){
        PersonalDocFragment fragment = new PersonalDocFragment();
        Bundle bundle = new Bundle();
        bundle.putString("uuid",id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        if(savedInstanceState != null){
            return;
        }
        DaggerPersonalListComponent.builder()
                .personalListModule(new PersonalListModule(this))
                .netComponent(MoeMoeApplicationLike.getInstance().getNetComponent())
                .build()
                .inject(this);
        final String id = getArguments().getString("uuid");
        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mAdapter = new PersonListAdapter(getContext(),0);
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        mListDocs.setLayoutManager(new LinearLayoutManager(getContext()));
        mListDocs.isLoadMoreEnabled(false);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Object object = mAdapter.getItem(position);
                if (object != null) {
                    if (object instanceof PersonDocEntity) {
                        PersonDocEntity bean = (PersonDocEntity) object;
                        if (!TextUtils.isEmpty(bean.getSchema())) {
                            Uri uri = Uri.parse(bean.getSchema());
                            IntentUtils.toActivityFromUri(getActivity(), uri,view);
                        }
                    }
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
                mPresenter.doRequest(id,mAdapter.getItemCount(),1);
            }

            @Override
            public void onRefresh() {
                isLoading = true;
                mPresenter.doRequest(id,0,1);
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
        mPresenter.doRequest(id,0,1);
    }

    @Override
    public void onSuccess(Object o,boolean isPull) {
        isLoading = false;
        if(((ArrayList<Object>) o).size() == 0){
            mListDocs.isLoadMoreEnabled(false);
        }else {
            mListDocs.isLoadMoreEnabled(true);
        }
        mListDocs.setComplete();
        if(isPull){
            mAdapter.setData((ArrayList<Object>) o);
        }else {
            mAdapter.addData((ArrayList<Object>) o);
        }
    }

    @Override
    public void onFailure(int code,String msg) {
        isLoading = false;
        mListDocs.setComplete();
    }
}
