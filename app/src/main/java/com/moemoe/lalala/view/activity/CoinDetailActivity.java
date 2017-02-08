package com.moemoe.lalala.view.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplicationLike;
import com.moemoe.lalala.di.components.DaggerCommentComponent;
import com.moemoe.lalala.di.modules.CommentModule;
import com.moemoe.lalala.presenter.CommentContract;
import com.moemoe.lalala.presenter.CommentPresenter;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.view.adapter.PersonListAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by yi on 2016/12/28.
 */

public class CoinDetailActivity extends BaseAppCompatActivity implements CommentContract.View{

    @BindView(R.id.rl_bar)
    View mRlRoot;
    @BindView(R.id.tv_title)
    TextView mTitle;
    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.list)
    PullAndLoadView mListDocs;

    @Inject
    CommentPresenter mPresenter;

    private PersonListAdapter mAdapter;
    private boolean mIsLoading = false;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_pulltorefresh_list;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerCommentComponent.builder()
                .commentModule(new CommentModule(this))
                .netComponent(MoeMoeApplicationLike.getInstance().getNetComponent())
                .build()
                .inject(this);
        mRlRoot.setVisibility(View.VISIBLE);
        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mAdapter = new PersonListAdapter(this,3);
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        mListDocs.setLayoutManager(new LinearLayoutManager(this));
        mListDocs.getRecyclerView().setHasFixedSize(true);
        mTitle.setText("节操记录");
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    protected void initListeners() {
        mIvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mListDocs.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                mIsLoading = true;
                mPresenter.doRequest(mAdapter.getItemCount(),5);
            }

            @Override
            public void onRefresh() {
                mIsLoading = true;
                mPresenter.doRequest(0,5);
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
    }

    @Override
    protected void initData() {
        mPresenter.doRequest(0,5);
    }

    @Override
    public void onSuccess(Object entities, boolean pull) {
        mIsLoading = false;
        mListDocs.setComplete();
        if(((ArrayList<Object>) entities).size() == 0){
            mListDocs.isLoadMoreEnabled(false);
        }else {
            mListDocs.isLoadMoreEnabled(true);
        }
        if(pull){
            mAdapter.setData((ArrayList<Object>) entities);
        }else {
            mAdapter.addData((ArrayList<Object>) entities);
        }
    }

    @Override
    public void onChangeSuccess(Object entities) {

    }

    @Override
    public void onFailure(int code, String msg) {
        mIsLoading = false;
        mListDocs.setComplete();
    }
}
