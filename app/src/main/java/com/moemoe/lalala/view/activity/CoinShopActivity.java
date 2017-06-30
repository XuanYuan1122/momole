package com.moemoe.lalala.view.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.view.adapter.CoinShopAdapter;
import com.moemoe.lalala.view.adapter.OnItemClickListener;
import com.moemoe.lalala.view.widget.netamenu.BottomMenuFragment;
import com.moemoe.lalala.view.widget.netamenu.MenuItem;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by yi on 2017/6/26.
 */

public class CoinShopActivity extends BaseAppCompatActivity {

    @BindView(R.id.ll_root)
    LinearLayout mLlRoot;
    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_toolbar_title)
    TextView mTitle;
    @BindView(R.id.iv_menu_list)
    ImageView mIvMenu;
    @BindView(R.id.rv_list)
    PullAndLoadView mListDocs;

    private CoinShopAdapter mAdapter;
    private boolean isLoading = false;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_bar_list;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        //top time
        TextView tvTop = new TextView(this);
        tvTop.setTextColor(ContextCompat.getColor(this,R.color.white));
        tvTop.setTextSize(TypedValue.COMPLEX_UNIT_DIP,12);
        tvTop.setBackgroundColor(ContextCompat.getColor(this,R.color.main_cyan));
        tvTop.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(this,45)));
        tvTop.setText("[50元 话费充值] 刷新还有: 17小时");
        mLlRoot.addView(tvTop,1);
        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mListDocs.getRecyclerView().setHasFixedSize(true);
        mAdapter = new CoinShopAdapter(this);
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        mListDocs.setLayoutManager(new LinearLayoutManager(this));
        mListDocs.setLoadMoreEnabled(false);

    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {
        mIvBack.setVisibility(View.VISIBLE);
        mIvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mTitle.setTextColor(ContextCompat.getColor(this,R.color.main_cyan));
        mTitle.setText(getString(R.string.label_coin_shop));
        mIvMenu.setVisibility(View.VISIBLE);
        final BottomMenuFragment bottomFragment = new BottomMenuFragment();
        ArrayList<MenuItem> items = new ArrayList<>();
        MenuItem item = new MenuItem(0, getString(R.string.label_bag_buy_list));
        items.add(item);
        bottomFragment.setShowTop(false);
        bottomFragment.setMenuItems(items);
        bottomFragment.setMenuType(BottomMenuFragment.TYPE_VERTICAL);
        bottomFragment.setmClickListener(new BottomMenuFragment.MenuItemClickListener() {
            @Override
            public void OnMenuItemClick(int itemId) {
                if(itemId == 0){
                    //TODO 跳转购买记录界面
                }
            }
        });
        mIvMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomFragment.show(getSupportFragmentManager(),"BagMenu");
            }
        });
    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void initData() {
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mListDocs.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                isLoading = true;
            }

            @Override
            public void onRefresh() {
                isLoading = true;
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
    }
}
