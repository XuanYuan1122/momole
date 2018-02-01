package com.moemoe.lalala.view.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.moemoe.lalala.R;
import com.moemoe.lalala.view.adapter.TabFragmentPagerAdapter;
import com.moemoe.lalala.view.widget.view.KiraTabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 *  Feed流动态页
 * Created by yi on 2018/1/11.
 */

public class FeedDynamicV3Fragment extends BaseFragment{

    @BindView(R.id.tab_layout)
    KiraTabLayout mTabLayout;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    private TabFragmentPagerAdapter mAdapter;

    public static FeedDynamicV3Fragment newInstance(){
        return new FeedDynamicV3Fragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_feed_dynamic_v3;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

        List<String> titles = new ArrayList<>();
        titles.add("全部");
        titles.add("好友");
        List<BaseFragment> fragmentList = new ArrayList<>();
        fragmentList.add(NewFollowMainFragment.newInstance("ground"));
        fragmentList.add(FeedFriendFragment.newInstance());
        mAdapter = new TabFragmentPagerAdapter(getChildFragmentManager(),fragmentList,titles);
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setViewPager(mViewPager);
    }

    @Override
    public void release() {
        if(mAdapter != null){
            mAdapter.release();
        }
    }
}
