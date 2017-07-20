package com.moemoe.lalala.view.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.gyf.barlibrary.ImmersionBar;
import com.moemoe.lalala.R;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.view.adapter.TabFragmentPagerAdapter;
import com.moemoe.lalala.view.fragment.MyTrashFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by yi on 2016/12/2.
 */

public class TrashFavoriteActivity extends BaseAppCompatActivity {

    public static String EXTRA_TYPE = "type";
    public static String EXTRA_LIST_TYPE = "list_type";
    @BindView(R.id.iv_back)
    ImageView IvBack;
    @BindView(R.id.pager_person_data)
    ViewPager mDataPager;
    @BindView(R.id.indicator_person_data)
    TabLayout mPageIndicator;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_msg;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        if(savedInstanceState != null){
            String FRAGMENTS_TAG = "android:support:fragments";
            savedInstanceState.remove(FRAGMENTS_TAG);
        }
        ImmersionBar.with(this)
                .statusBarView(R.id.top_view)
                .statusBarDarkFont(true,0.2f)
                .init();
        if(getIntent()== null) {
            finish();
            return;
        }
        String type = getIntent().getStringExtra(EXTRA_TYPE);
        MyTrashFragment myTrashFragment = new MyTrashFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_TYPE,type);
        bundle.putInt(EXTRA_LIST_TYPE,0);
        myTrashFragment.setArguments(bundle);
        MyTrashFragment favoriteFragment = new MyTrashFragment();
        bundle = new Bundle();
        bundle.putString(EXTRA_TYPE,type);
        bundle.putInt(EXTRA_LIST_TYPE,1);
        favoriteFragment.setArguments(bundle);
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(myTrashFragment);
        fragmentList.add(favoriteFragment);
        List<String> titles = new ArrayList<>();
        titles.add(getString(R.string.label_mine));
        titles.add(getString(R.string.label_favorite));
        TabFragmentPagerAdapter mAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager(),fragmentList,titles);
        mDataPager.setAdapter(mAdapter);
        mPageIndicator.setupWithViewPager(mDataPager);
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    protected void initListeners() {
        IvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
