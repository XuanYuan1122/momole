package com.moemoe.lalala.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.app.RxBus;
import com.moemoe.lalala.di.components.DaggerPersonalListComponent;
import com.moemoe.lalala.di.modules.PersonalListModule;
import com.moemoe.lalala.event.AtUserEvent;
import com.moemoe.lalala.event.SearchChangedEvent;
import com.moemoe.lalala.model.entity.PersonFollowEntity;
import com.moemoe.lalala.presenter.PersonaListPresenter;
import com.moemoe.lalala.presenter.PersonalListContract;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.view.activity.NewPersonalActivity;
import com.moemoe.lalala.view.adapter.OnItemClickListener;
import com.moemoe.lalala.view.adapter.PersonListAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by yi on 2016/12/15.
 */

public class SearchUserFragment extends BaseFragment  implements PersonalListContract.View{

    @BindView(R.id.list)
    PullAndLoadView mListDocs;
    @BindView(R.id.ll_not_show)
    View mLlShow;
    @Inject
    PersonaListPresenter mPresenter;
    private PersonListAdapter mAdapter;
    private boolean isLoading = false;
    private String mKeyWord;
    private int mCurPage = 1;
    private ArrayList<Object> mCurList;
    private boolean notFirst;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_simple_pulltorefresh_list;
    }

    public static SearchUserFragment newInstance(boolean only){
        SearchUserFragment fragment = new SearchUserFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("only",only);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        if(savedInstanceState != null || notFirst){
            return;
        }
        DaggerPersonalListComponent.builder()
                .personalListModule(new PersonalListModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        final boolean only = getArguments().getBoolean("only");
        mListDocs.setVisibility(View.VISIBLE);
        mLlShow.setVisibility(View.GONE);
        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mAdapter = new PersonListAdapter(getContext(),1);
        if(mCurList != null && mCurList.size() > 0){
            mAdapter.setData(mCurList);
        }
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        mListDocs.setLayoutManager(new LinearLayoutManager(getContext()));
        mListDocs.setLoadMoreEnabled(false);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Object o = mAdapter.getItem(position);
                if(o instanceof PersonFollowEntity){
                    PersonFollowEntity entity = (PersonFollowEntity) o;
                    if(!only){
                        if(!entity.getUserId().equals(PreferenceUtils.getUUid())){
                            Intent i = new Intent(getContext(), NewPersonalActivity.class);
                            i.putExtra("uuid",entity.getUserId());
                            startActivity(i);
                        }
                    }else {
                        RxBus.getInstance().post(new AtUserEvent(entity.getUserId(),entity.getUserName()));
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
                mPresenter.doRequest(mKeyWord,mCurPage,9);
            }

            @Override
            public void onRefresh() {
                isLoading = true;
                mCurPage = 1;
                mPresenter.doRequest(mKeyWord,mCurPage,9);
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
        subscribeSearchChangedEvent();
        notFirst = true;
    }

    @Override
    public void onSuccess(Object o,boolean isPull) {
        isLoading = false;
        mListDocs.setComplete();
        mCurPage++;
        if(((ArrayList<Object>) o).size() == 0){
            mListDocs.setLoadMoreEnabled(false);
        }else {
            mListDocs.setLoadMoreEnabled(true);
        }
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

    private void subscribeSearchChangedEvent() {
        Subscription subscription = RxBus.getInstance()
                .toObservable(SearchChangedEvent.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .subscribe(new Action1<SearchChangedEvent>() {
                    @Override
                    public void call(SearchChangedEvent event) {
                        mKeyWord = event.getKeyWord();
                        mCurPage = 1;
                        mPresenter.doRequest(mKeyWord,mCurPage,9);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
        RxBus.getInstance().unSubscribe(this);
        RxBus.getInstance().addSubscription(this, subscription);
    }

    @Override
    public void onDestroyView() {
        mCurList = mAdapter.getList();
        mPresenter.release();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unSubscribe(this);
    }
}
