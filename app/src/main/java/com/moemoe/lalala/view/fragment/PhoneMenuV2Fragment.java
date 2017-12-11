package com.moemoe.lalala.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;

import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.moemoe.lalala.R;
import com.moemoe.lalala.view.activity.IPhoneFragment;
import com.moemoe.lalala.view.activity.SearchActivity;
import com.moemoe.lalala.view.adapter.TabFragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 *
 * Created by yi on 2017/9/4.
 */

public class PhoneMenuV2Fragment extends BaseFragment implements IPhoneFragment{

    @BindView(R.id.tab_layout)
    SegmentTabLayout mTabLayout;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    private TabFragmentPagerAdapter mAdapter;

    public static PhoneMenuV2Fragment newInstance(){
        return new PhoneMenuV2Fragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_phone_menu_v2;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        String[] mTitles = {"互相关注", "我的关注", "我的粉丝"};
        mTabLayout.setTabData(mTitles);
        List<String> titles = new ArrayList<>();
        titles.add(mTitles[0]);
        titles.add(mTitles[1]);
        titles.add(mTitles[2]);
        ArrayList<BaseFragment> fragmentList = new ArrayList<>();
        fragmentList.add(PhoneMenuListFragment.newInstance("both"));
        fragmentList.add(PhoneMenuListFragment.newInstance("follow"));
        fragmentList.add(PhoneMenuListFragment.newInstance("fans"));
        mAdapter = new TabFragmentPagerAdapter(getChildFragmentManager(),fragmentList,titles);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(1);
        mTabLayout.setCurrentTab(1);
        mTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                mViewPager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {
            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mTabLayout.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mAdapter != null) mAdapter.release();
    }

    @Override
    public String getTitle() {
        return "通讯录";
    }

    @Override
    public int getTitleColor() {
        return R.color.main_cyan;
    }

    @Override
    public int getBack() {
        return R.drawable.btn_phone_back;
    }

    @Override
    public int getMenu() {
        return R.drawable.btn_search_kira;
    }

    @Override
    public void onMenuClick() {
        Intent i3 = new Intent(getContext(),SearchActivity.class);
        i3.putExtra("show_type",SearchActivity.SHOW_KIRA);
        startActivity(i3);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
