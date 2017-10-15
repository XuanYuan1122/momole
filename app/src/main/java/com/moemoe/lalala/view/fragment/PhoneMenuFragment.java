package com.moemoe.lalala.view.fragment;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.moemoe.lalala.R;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.view.activity.PhoneMainActivity;
import com.moemoe.lalala.view.adapter.TabFragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by yi on 2017/9/4.
 */

public class PhoneMenuFragment extends BaseFragment {

    public static final String TAG = "PhoneMenuFragment";

    @BindView(R.id.tab_layout)
    SegmentTabLayout mTabLayout;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_toolbar_title)
    TextView mTvTitle;

    private TabFragmentPagerAdapter mAdapter;

    public static PhoneMenuFragment newInstance(){
        return new PhoneMenuFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_phone_menu;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mIvBack.setVisibility(View.VISIBLE);
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mIvBack.setImageResource(R.drawable.btn_phone_back);
        mTvTitle.setTextColor(ContextCompat.getColor(getContext(),R.color.main_cyan));
        mTvTitle.setText("通讯录");
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

    public void release(){
        if(mAdapter != null) mAdapter.release();
        super.release();
    }

    @Override
    public void onBackPressed() {
        ((PhoneMainActivity)getContext()).finishCurFragment();
    }
}
