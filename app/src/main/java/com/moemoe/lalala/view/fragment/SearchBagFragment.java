package com.moemoe.lalala.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.app.RxBus;
import com.moemoe.lalala.di.components.DaggerPersonalListComponent;
import com.moemoe.lalala.di.modules.PersonalListModule;
import com.moemoe.lalala.event.DirBuyEvent;
import com.moemoe.lalala.event.SearchChangedEvent;
import com.moemoe.lalala.model.entity.BagDirEntity;
import com.moemoe.lalala.model.entity.FolderType;
import com.moemoe.lalala.model.entity.ShowFolderEntity;
import com.moemoe.lalala.presenter.PersonaListPresenter;
import com.moemoe.lalala.presenter.PersonalListContract;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.GridItemDecoration;
import com.moemoe.lalala.view.activity.NewFileCommonActivity;
import com.moemoe.lalala.view.activity.NewFileManHuaActivity;
import com.moemoe.lalala.view.activity.NewFileXiaoshuoActivity;
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

public class SearchBagFragment extends BaseFragment  implements PersonalListContract.View{

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
  //  private boolean notFirst;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_simple_pulltorefresh_list;
    }

    public static SearchBagFragment newInstance(){
        SearchBagFragment fragment = new SearchBagFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
       // if(savedInstanceState != null || notFirst){
       //     return;
      //  }
        DaggerPersonalListComponent.builder()
                .personalListModule(new PersonalListModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mListDocs.setVisibility(View.VISIBLE);
        mLlShow.setVisibility(View.GONE);
        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mAdapter = new PersonListAdapter(getContext(),11);
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(),2);
        mListDocs.setLayoutManager(layoutManager);
        mListDocs.getRecyclerView().addItemDecoration(new GridItemDecoration((int)getResources().getDimension(R.dimen.x20)));
        mListDocs.setLoadMoreEnabled(false);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ShowFolderEntity entity = (ShowFolderEntity) mAdapter.getItem(position);
                if(entity.getType().equals(FolderType.ZH.toString())){
                    NewFileCommonActivity.startActivity(getContext(),FolderType.ZH.toString(),entity.getFolderId(),entity.getCreateUser());
                }else if(entity.getType().equals(FolderType.TJ.toString())){
                    NewFileCommonActivity.startActivity(getContext(),FolderType.TJ.toString(),entity.getFolderId(),entity.getCreateUser());
                }else if(entity.getType().equals(FolderType.MH.toString())){
                    NewFileManHuaActivity.startActivity(getContext(),FolderType.MH.toString(),entity.getFolderId(),entity.getCreateUser());
                }else if(entity.getType().equals(FolderType.XS.toString())){
                    NewFileXiaoshuoActivity.startActivity(getContext(),FolderType.XS.toString(),entity.getFolderId(),entity.getCreateUser());
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
                mPresenter.doRequest(mKeyWord,mCurPage,8);
            }

            @Override
            public void onRefresh() {
                isLoading = true;
                mCurPage = 1;
                mPresenter.doRequest(mKeyWord,mCurPage,8);
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
     //   notFirst = true;
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
                        mPresenter.doRequest(mKeyWord,mCurPage,8);
                    }
                }, new Consumer<Throwable>() {

                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        Disposable buySubscription = RxBus.getInstance()
                .toObservable(DirBuyEvent.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .subscribe(new Consumer<DirBuyEvent>() {
                    @Override
                    public void accept(DirBuyEvent dirBuyEvent) throws Exception {
                        if(dirBuyEvent.getPosition() != -1){
                            Object o = mAdapter.getItem(dirBuyEvent.getPosition());
                            ((BagDirEntity)o).setBuy(dirBuyEvent.isBuy());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        RxBus.getInstance().unSubscribe(this);
        RxBus.getInstance().addSubscription(this, subscription);
        RxBus.getInstance().addSubscription(this, buySubscription);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void release(){
        if(mPresenter != null) mPresenter.release();
        RxBus.getInstance().unSubscribe(this);
        super.release();
    }
}
