package com.moemoe.lalala.view.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.moemoe.lalala.R;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.view.adapter.MateSelectAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by yi on 2017/9/6.
 */

public class MateSelectActivity extends BaseAppCompatActivity {

    @BindView(R.id.list)
    PullAndLoadView mListDocs;
    MateSelectAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_one_pulltorefresh_list;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mListDocs.getSwipeRefreshLayout().setEnabled(false);
        mListDocs.setLoadMoreEnabled(false);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mListDocs.setPadding(DensityUtil.dip2px(this,12),DensityUtil.dip2px(this,50),0,0);
        mListDocs.setLayoutManager(manager);
        mAdapter = new MateSelectAdapter();
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        ArrayList<Integer> list = new ArrayList<>();
        list.add(R.drawable.btn_deskmate_chose_mei);
        list.add(R.drawable.btn_deskmate_chose_len);
        list.add(R.drawable.btn_deskmate_chose_sha);
        mAdapter.setList(list);

    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void initData() {

    }
}
