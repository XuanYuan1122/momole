package com.moemoe.lalala.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.moemoe.lalala.utils.ViewUtils;
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

    @BindView(R.id.iv_back)
    View mIvBack;
    @BindView(R.id.tv_toolbar_title)
    TextView mTvTitle;
    @BindView(R.id.rv_past)
    PullAndLoadView mPastPv;

    @Inject
    ColumnPresenter mPresenter;
    private ColumnDetailAdapter mPastAdapter;
    private String mBarId;
    private boolean mIsLoading = false;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_column_detail;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
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
        mTvTitle.setTextColor(ContextCompat.getColor(this,R.color.main_cyan));
        mTvTitle.setText(title);

        mPastPv.setLoadMoreEnabled(true);
        mPastPv.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mPastPv.setLayoutManager(new LinearLayoutManager(this));
        mPastAdapter = new ColumnDetailAdapter(this);
        mPastPv.getRecyclerView().setAdapter(mPastAdapter);
        mPastPv.getRecyclerView().addItemDecoration(new SimpleBorderDividerItemDecoration(getResources().getDimensionPixelOffset(R.dimen.size_7),0));
        mPastPv.setVisibility(View.VISIBLE);
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
                    if(id.contains(getString(R.string.label_doc_path)) && !id.contains("uuid")){
                        String begin = id.substring(0,id.indexOf("?") + 1);
                        String uuid = id.substring(id.indexOf("?") + 1);
                        id = begin + "uuid=" + uuid + "&from_name=" + mTvTitle.getText().toString();
                    }
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
                mPresenter.requestPastFresh(mBarId,-mPastAdapter.getDataCount() - 1);
            }

            @Override
            public void onRefresh() {
                mIsLoading = true;
                mPresenter.requestPastFresh(mBarId,-1);
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
        mPresenter.requestPastFresh(mBarId,-1);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onDestroy() {
        if(mPresenter != null)mPresenter.release();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onFailure(int code,String msg) {
        mIsLoading = false;
        mPastPv.setComplete();
        ErrorCodeUtils.showErrorMsgByCode(ColumnDetailActivity.this,code,msg);
    }

    @Override
    public void loadColumnPastData(ArrayList<CalendarDayItemEntity> calendarDayItemEntities) {
        mIsLoading = false;
        mPastPv.setComplete();
        mPastAdapter.setData(calendarDayItemEntities,false);
    }
}
