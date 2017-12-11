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
import com.moemoe.lalala.event.SearchChangedEvent;
import com.moemoe.lalala.model.entity.PersonFollowEntity;
import com.moemoe.lalala.presenter.PersonaListPresenter;
import com.moemoe.lalala.presenter.PersonalListContract;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.view.activity.PersonalV2Activity;
import com.moemoe.lalala.view.adapter.OnItemClickListener;
import com.moemoe.lalala.view.adapter.PersonListAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by yi on 2016/12/15.
 */

public class SearchUserByKiraFragment extends BaseFragment  implements PersonalListContract.View{

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

    @Override
    protected int getLayoutId() {
        return R.layout.ac_simple_pulltorefresh_list;
    }

    public static SearchUserByKiraFragment newInstance(){
        return new SearchUserByKiraFragment();
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerPersonalListComponent.builder()
                .personalListModule(new PersonalListModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mListDocs.setVisibility(View.VISIBLE);
        mLlShow.setVisibility(View.GONE);
        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mAdapter = new PersonListAdapter(getContext(),1);
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        mListDocs.setLayoutManager(new LinearLayoutManager(getContext()));
        mListDocs.setLoadMoreEnabled(false);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Object o = mAdapter.getItem(position);
                if(o instanceof PersonFollowEntity){
                    PersonFollowEntity entity = (PersonFollowEntity) o;
                    if(!entity.getUserId().equals(PreferenceUtils.getUUid())){
                        Intent i = new Intent(getContext(), PersonalV2Activity.class);
                        i.putExtra("uuid",entity.getUserId());
                        startActivity(i);
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
                mPresenter.doRequest(mKeyWord,mCurPage,11);
            }

            @Override
            public void onRefresh() {
                isLoading = true;
                mCurPage = 1;
                mPresenter.doRequest(mKeyWord,mCurPage,11);
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
        Disposable subscription = RxBus.getInstance()
                .toObservable(SearchChangedEvent.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .subscribe(new Consumer<SearchChangedEvent>() {
                    @Override
                    public void accept(SearchChangedEvent searchChangedEvent) throws Exception {
                        mKeyWord = searchChangedEvent.getKeyWord();
                        mCurPage = 1;
                        mPresenter.doRequest(mKeyWord,mCurPage,11);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        RxBus.getInstance().unSubscribe(this);
        RxBus.getInstance().addSubscription(this, subscription);
    }

    public void release(){
        if(mPresenter != null) mPresenter.release();
        RxBus.getInstance().unSubscribe(this);
        super.release();
    }
}
