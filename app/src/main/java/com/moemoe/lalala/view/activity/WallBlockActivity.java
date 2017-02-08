package com.moemoe.lalala.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.view.adapter.TabFragmentPagerAdapter;
import com.moemoe.lalala.view.fragment.ClassFragment;
import com.moemoe.lalala.view.fragment.WallBlockFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by yi on 2016/12/2.
 */

public class WallBlockActivity extends BaseAppCompatActivity {

    @BindView(R.id.iv_back)
    ImageView IvBack;
    @BindView(R.id.pager_person_data)
    ViewPager mDataPager;
    @BindView(R.id.indicator_person_data)
    TabLayout mPageIndicator;
    @BindView(R.id.tv_simple_label)
    TextView mSimpleLabel;
    private ClassFragment classFragment;
    private boolean mCurTag;

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
        classFragment = new ClassFragment();
        WallBlockFragment wallBlockFragment = new WallBlockFragment();
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(classFragment);
        fragmentList.add(wallBlockFragment);
        List<String> titles = new ArrayList<>();
        titles.add(getString(R.string.label_class));
        titles.add(getString(R.string.label_square));
        TabFragmentPagerAdapter mAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager(),fragmentList,titles);
        mDataPager.setAdapter(mAdapter);
        mPageIndicator.setupWithViewPager(mDataPager);
        mSimpleLabel.setVisibility(View.VISIBLE);
        mSimpleLabel.setSelected(AppSetting.SUB_TAG);
        mCurTag = AppSetting.SUB_TAG;
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
        mSimpleLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(classFragment != null){
                    AppSetting.SUB_TAG = !AppSetting.SUB_TAG;
                    mCurTag = AppSetting.SUB_TAG;
                    mSimpleLabel.setSelected(AppSetting.SUB_TAG);
                    PreferenceUtils.setSimpleLabel(WallBlockActivity.this,AppSetting.SUB_TAG);
                    classFragment.changeLabelAdapter();
                }
            }
        });
    }

    @Override
    protected void initData() {

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == CreateNormalDocActivity.RESPONSE_CODE){
            if(classFragment != null){
                classFragment.onActivityResult(requestCode,resultCode,data);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mCurTag != AppSetting.SUB_TAG){
            if(classFragment != null){
                mSimpleLabel.setSelected(AppSetting.SUB_TAG);
                classFragment.changeLabelAdapter();
            }
            mCurTag = AppSetting.SUB_TAG;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
