package com.moemoe.lalala.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerColumnComponent;
import com.moemoe.lalala.di.modules.ColumnModule;
import com.moemoe.lalala.model.entity.CalendarDayItemEntity;
import com.moemoe.lalala.presenter.ColumnContract;
import com.moemoe.lalala.presenter.ColumnPresenter;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.view.adapter.ColumnDetailAdapter;
import com.moemoe.lalala.view.adapter.OnItemClickListener;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;
import com.moemoe.lalala.view.widget.recycler.SimpleBorderDividerItemDecoration;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by yi on 2016/11/30.
 */

public class ColumnDetailActivity extends BaseAppCompatActivity implements ColumnContract.View{

    public static final String EXTRA_TITLE = "name";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tv_toolbar_title)
    TextView mTvTitle;
    @BindView(R.id.rv_future)
    PullAndLoadView mFuturePv;
    @BindView(R.id.rv_past)
    PullAndLoadView mPastPv;
    @BindView(R.id.tv_time)
    TextView mTvTime;
    @BindView(R.id.ll_time_root)
    View mLlTimeRoot;

    @Inject
    ColumnPresenter mPresenter;
    private ColumnDetailAdapter mFutureAdapter;
    private ColumnDetailAdapter mPastAdapter;
    private boolean mIsPast = true;
    private String mBarId;
    private boolean mIsLoading = false;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_column_detail;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        Intent i = getIntent();
        if(i == null){
            finish();
            return;
        }
        mBarId = i.getStringExtra(UUID);
        String title = i.getStringExtra(EXTRA_TITLE);
        DaggerColumnComponent.builder()
                .columnModule(new ColumnModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mTvTitle.setText(title);
        mFuturePv.setLoadMoreEnabled(false);
        mFuturePv.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mFuturePv.setLayoutManager(new LinearLayoutManager(this));
        mFutureAdapter = new ColumnDetailAdapter(this);
        mFuturePv.getRecyclerView().setAdapter(mFutureAdapter);

        mPastPv.setLoadMoreEnabled(true);
        mPastPv.getSwipeRefreshLayout().setEnabled(false);
        mPastPv.setLayoutManager(new LinearLayoutManager(this));
        mPastAdapter = new ColumnDetailAdapter(this);
        mPastPv.getRecyclerView().setAdapter(mPastAdapter);
        mPastPv.getRecyclerView().addItemDecoration(new SimpleBorderDividerItemDecoration(getResources().getDimensionPixelOffset(R.dimen.size_7),0));
        mFuturePv.setVisibility(View.GONE);
        mPastPv.setVisibility(View.VISIBLE);
        mTvTime.setText(R.string.label_coming_soon);
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    protected void initListeners() {
        mToolbar.setNavigationOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mPastAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Object bean = mPastAdapter.getItem(position);
                String id = null;
                if (bean instanceof CalendarDayItemEntity.CalendarData) {
                    CalendarDayItemEntity.CalendarData doc = (CalendarDayItemEntity.CalendarData) bean;
                    id = doc.getSchema();
                }
                if (!TextUtils.isEmpty(id)) {
                    Uri uri = Uri.parse(id);
                    IntentUtils.toActivityFromUri(ColumnDetailActivity.this, uri,view);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mFuturePv.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {

            }

            @Override
            public void onRefresh() {
                mIsLoading = true;
                mPresenter.requestFutureFresh(mBarId,-mFutureAdapter.getItemCount()-1);
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
        mPastAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Object bean = mPastAdapter.getItem(position);
                String id = null;
                if (bean instanceof CalendarDayItemEntity.CalendarData) {
                    CalendarDayItemEntity.CalendarData doc = (CalendarDayItemEntity.CalendarData) bean;
                    id = doc.getSchema();
                }
                if (!TextUtils.isEmpty(id)) {
                    Uri uri = Uri.parse(id);
                    IntentUtils.toActivityFromUri(ColumnDetailActivity.this, uri,view);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mPastPv.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                mIsLoading = true;
                mPresenter.requestPastFresh(mBarId,mPastAdapter.getItemCount());
            }

            @Override
            public void onRefresh() {

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

        mLlTimeRoot.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                mIsPast = !mIsPast;
                if(mIsPast){
                    mPastPv.setVisibility(View.VISIBLE);
                    mFuturePv.setVisibility(View.GONE);
                    mTvTime.setText(R.string.label_coming_soon);
                }else {
                    mPastPv.setVisibility(View.GONE);
                    mFuturePv.setVisibility(View.VISIBLE);
                    mTvTime.setText(R.string.label_review_past);
                    if(mPastAdapter.getItemCount() == 0) mPresenter.requestFutureFresh(mBarId,-1);
                }
            }
        });

        mFuturePv.getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if(!isFinishing())Glide.with(ColumnDetailActivity.this).resumeRequests();
                }else {
                    if(!isFinishing())Glide.with(ColumnDetailActivity.this).pauseRequests();
                }
            }
        });

        mPastPv.getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if(!isFinishing())Glide.with(ColumnDetailActivity.this).resumeRequests();
                }else {
                    if(!isFinishing())Glide.with(ColumnDetailActivity.this).pauseRequests();
                }
            }
        });
        mPresenter.requestPastFresh(mBarId,0);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onDestroy() {
        mPresenter.release();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        Glide.with(this).resumeRequests();
        super.onResume();
    }

    @Override
    protected void onPause() {
        Glide.with(this).pauseRequests();
        super.onPause();
    }

    @Override
    public void onFailure(int code,String msg) {
        mIsLoading = false;
        mFuturePv.setComplete();
        mPastPv.setComplete();
        ErrorCodeUtils.showErrorMsgByCode(ColumnDetailActivity.this,code,msg);
    }

    @Override
    public void loadColumnFutureData(ArrayList<CalendarDayItemEntity> calendarDayItemEntities) {
        mIsLoading = false;
        mFuturePv.setComplete();
        mFutureAdapter.setData(calendarDayItemEntities,true);
    }

    @Override
    public void loadColumnPastData(ArrayList<CalendarDayItemEntity> calendarDayItemEntities) {
        mIsLoading = false;
        mPastPv.setComplete();
        mPastAdapter.setData(calendarDayItemEntities,false);
    }
}
