package com.moemoe.lalala.view.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerCommentComponent;
import com.moemoe.lalala.di.modules.CommentModule;
import com.moemoe.lalala.model.entity.CoinDetailEntity;
import com.moemoe.lalala.presenter.CommentContract;
import com.moemoe.lalala.presenter.CommentPresenter;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.view.adapter.OnItemClickListener;
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

    @BindView(R.id.tv_toolbar_title)
    TextView mTitle;
    @BindView(R.id.iv_back)
    View mIvBack;
    @BindView(R.id.rv_list)
    PullAndLoadView mListDocs;

    @Inject
    CommentPresenter mPresenter;

    private PersonListAdapter mAdapter;
    private boolean mIsLoading = false;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_bag_favorite;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerCommentComponent.builder()
                .commentModule(new CommentModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mAdapter = new PersonListAdapter(this,3);
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        mListDocs.setLayoutManager(new LinearLayoutManager(this));
        mListDocs.getRecyclerView().setHasFixedSize(true);
        mTitle.setTextColor(ContextCompat.getColor(this,R.color.main_cyan));
        mTitle.setText("节操记录");
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    protected void initListeners() {
        mIvBack.setVisibility(View.VISIBLE);
        mIvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Object item = mAdapter.getItem(position);
                if(item instanceof CoinDetailEntity){
                    if(!TextUtils.isEmpty(((CoinDetailEntity) item).getSchema())){
                        String schema = ((CoinDetailEntity) item).getSchema();
                        if(schema.contains(getString(R.string.label_doc_path)) && !schema.contains("uuid")){
                            String begin = schema.substring(0,schema.indexOf("?") + 1);
                            String id = schema.substring(schema.indexOf("?") + 1);
                            schema = begin + "uuid=" + id + "&from_name=节操记录";
                        }
                        Uri uri = Uri.parse(schema);
                        IntentUtils.toActivityFromUri(CoinDetailActivity.this, uri, view);
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
            mListDocs.setLoadMoreEnabled(false);
        }else {
            mListDocs.setLoadMoreEnabled(true);
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

    @Override
    protected void onDestroy() {
        if(mPresenter != null)mPresenter.release();
        super.onDestroy();
    }
}
