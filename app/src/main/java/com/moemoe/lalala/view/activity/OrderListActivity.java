package com.moemoe.lalala.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerOrderListComponent;
import com.moemoe.lalala.di.modules.OrderListModule;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.OrderEntity;
import com.moemoe.lalala.presenter.OrderListContract;
import com.moemoe.lalala.presenter.OrderListPresenter;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.StartActivityConstant;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.adapter.OrderListAdapter;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by yi on 2017/1/19.
 */

public class OrderListActivity extends BaseAppCompatActivity implements OrderListContract.View{

    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_toolbar_title)
    TextView mTitle;
    @BindView(R.id.rv_list)
    PullAndLoadView mListDocs;

    @Inject
    OrderListPresenter mPresenter;

    private OrderListAdapter mAdapter;

    private boolean isLoading = false;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_bar_list;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerOrderListComponent.builder()
                .orderListModule(new OrderListModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
        mTitle.setText("订单状态");
        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mAdapter = new OrderListAdapter();
        mListDocs.getRecyclerView().setHasFixedSize(true);
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        mListDocs.setLayoutManager(new LinearLayoutManager(this));
        mListDocs.setLoadMoreEnabled(false);
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    protected void initListeners() {
        mIvBack.setVisibility(View.VISIBLE);
        mIvBack.setImageResource(R.drawable.btn_back_black_normal);
        mIvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent i = new Intent(OrderListActivity.this, OrderActivity.class);
                OrderEntity entity = mAdapter.getItem(position);
                i.putExtra("order",entity);
                i.putExtra("show_top",entity.getStatus() != 2);
                i.putExtra("show_status",true);
                i.putExtra("position", position);
                startActivityForResult(i, StartActivityConstant.REQ_ORDER_ACTIVITY);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mListDocs.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                isLoading = true;
                mPresenter.loadOrderList(mAdapter.getList().size());
            }

            @Override
            public void onRefresh() {
                isLoading = true;
                mPresenter.loadOrderList(0);
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
        mPresenter.loadOrderList(0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == StartActivityConstant.REQ_ORDER_ACTIVITY && resultCode == RESULT_OK){
            String type = data.getStringExtra("type");
            int position = data.getIntExtra("position", -1);
            if(position < 0){
                return;
            }
            if("pay".equals(type)){
                mAdapter.getList().get(position).setStatus(2);
                mAdapter.notifyItemChanged(position);
            }else if("cancel".equals(type)){
                mAdapter.getList().remove(position);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onDestroy() {
        if(mPresenter != null) mPresenter.release();
        super.onDestroy();
    }

    @Override
    public void onFailure(int code, String msg) {
        ErrorCodeUtils.showErrorMsgByCode(this, code, msg);
    }

    @Override
    public void onLoadOrderListSuccess(ArrayList<OrderEntity> entities, boolean isPull) {
        mListDocs.setComplete();
        isLoading = false;
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
}
