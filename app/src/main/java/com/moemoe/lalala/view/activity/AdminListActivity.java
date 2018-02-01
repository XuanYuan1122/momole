package com.moemoe.lalala.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.UserTopEntity;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.adapter.UserAdapter;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;

import java.util.ArrayList;

import butterknife.BindView;

/**
 *
 * Created by yi on 2018/1/23.
 */

public class AdminListActivity extends BaseAppCompatActivity{

    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_toolbar_title)
    TextView mTvTitle;
    @BindView(R.id.tv_menu)
    TextView mTvApply;
    @BindView(R.id.rv_list)
    PullAndLoadView mList;

    private UserAdapter mAdapter;

    public static void startActivity(Context context, ArrayList<UserTopEntity> entities,String id){
        Intent i = new Intent(context,AdminListActivity.class);
        i.putParcelableArrayListExtra("list",entities);
        i.putExtra(UUID,id);
        context.startActivity(i);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ac_bar_list;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ArrayList<UserTopEntity> list = getIntent().getParcelableArrayListExtra("list");
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
        mList.getSwipeRefreshLayout().setEnabled(false);
        mList.setLoadMoreEnabled(false);

        mAdapter = new UserAdapter(list);
        mList.setLayoutManager(new LinearLayoutManager(this));
        mList.getRecyclerView().addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        mList.getRecyclerView().setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ViewUtils.toPersonal(AdminListActivity.this,mAdapter.getItem(position).getUserId());
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {
        mIvBack.setVisibility(View.VISIBLE);
        mIvBack.setImageResource(R.drawable.btn_back_black_normal);
        mIvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                onBackPressed();
            }
        });
        mTvTitle.setText("管理员");
        ViewUtils.setRightMargins(mTvApply,(int) getResources().getDimension(R.dimen.x36));
        mTvApply.setVisibility(View.VISIBLE);
        mTvApply.setText("申请");
        mTvApply.setTextColor(ContextCompat.getColor(this,R.color.main_cyan));
        mTvApply.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                ApplyAdminActivity.startActivity(AdminListActivity.this,getIntent().getStringExtra(UUID));
            }
        });
    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void initData() {

    }
}
