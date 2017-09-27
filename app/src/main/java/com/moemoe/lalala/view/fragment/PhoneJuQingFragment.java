package com.moemoe.lalala.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.TabEntity;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.view.activity.DynamicActivity;
import com.moemoe.lalala.view.activity.PhoneMainActivity;
import com.moemoe.lalala.view.activity.TagControlActivity;
import com.moemoe.lalala.view.adapter.TabFragmentPagerAdapter;
import com.moemoe.lalala.view.widget.netamenu.BottomMenuFragment;
import com.moemoe.lalala.view.widget.netamenu.MenuItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.moemoe.lalala.utils.StartActivityConstant.REQ_DELETE_TAG;

/**
 * Created by yi on 2017/9/4.
 */

public class PhoneJuQingFragment extends BaseFragment{

    public static final String TAG = "PhoneJuQingFragment";

    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_menu)
    TextView mTvMenu;
    @BindView(R.id.indicator_person_data)
    CommonTabLayout mPageIndicator;
    @BindView(R.id.view_pager)
    ViewPager mDataPager;

    private TabFragmentPagerAdapter mAdapter;
    private BottomMenuFragment fragment;

    public static PhoneJuQingFragment newInstance(){
        return new PhoneJuQingFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_phone_juqing;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        fragment = new BottomMenuFragment();
        mIvBack.setVisibility(View.VISIBLE);
        mIvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                onBackPressed();
            }
        });
        String[] mTitles = {"主线","支线","日常"};
        ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
        for (String title : mTitles) {
            mTabEntities.add(new TabEntity(title, R.drawable.ic_personal_bag,R.drawable.ic_personal_bag));
        }
        List<BaseFragment> fragmentList = new ArrayList<>();
        fragmentList.add(PhoneJuQingListFragment.newInstance("main"));
        fragmentList.add(PhoneJuQingListFragment.newInstance("sec"));
        fragmentList.add(PhoneJuQingListFragment.newInstance("day"));
        List<String> titles = new ArrayList<>();
        titles.add("主线");
        titles.add("支线");
        titles.add("日常");
        mPageIndicator.setTabData(mTabEntities);
        mAdapter = new TabFragmentPagerAdapter(getChildFragmentManager(),fragmentList,titles);
        mDataPager.setAdapter(mAdapter);
        mPageIndicator.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                mDataPager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {
            }
        });
        mDataPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mPageIndicator.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mTvMenu.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                showMenu();
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

    private void showMenu(){
        ArrayList<MenuItem> items = new ArrayList<>();
        MenuItem item = new MenuItem(1,"全部");
        items.add(item);
        item = new MenuItem(2,"攻略中");
        items.add(item);
        item = new MenuItem(3,"已完成");
        items.add(item);
        item = new MenuItem(4,"未解锁");
        items.add(item);
        fragment.setShowTop(false);
        fragment.setMenuItems(items);
        fragment.setMenuType(BottomMenuFragment.TYPE_VERTICAL);
        fragment.setmClickListener(new BottomMenuFragment.MenuItemClickListener() {
            @Override
            public void OnMenuItemClick(int itemId) {
                if(itemId == 1){
                    mTvMenu.setText("全部");
                    //TODO 根据选择加载list
                } else if(itemId == 2){
                    mTvMenu.setText("攻略中");
                } else if(itemId == 3){
                    mTvMenu.setText("已完成");
                } else if(itemId == 4){
                    mTvMenu.setText("未解锁");
                }
            }
        });
        fragment.show(getChildFragmentManager(),"DynamicActivity");
    }
}
