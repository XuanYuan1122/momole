package com.moemoe.lalala.view.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.event.SystemMessageEvent;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.adapter.TabFragmentPagerAdapter;
import com.moemoe.lalala.view.fragment.BaseFragment;
import com.moemoe.lalala.view.fragment.FeedBagFragment;
import com.moemoe.lalala.view.fragment.FeedDynamicV3Fragment;
import com.moemoe.lalala.view.fragment.FeedFollowV3Fragment;
import com.moemoe.lalala.view.widget.view.KiraTabLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * feed流主界面
 * Created by yi on 2018/1/10.
 */

public class FeedV3Activity extends BaseAppCompatActivity {

    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.indicator_person_data)
    KiraTabLayout mPageIndicator;
    @BindView(R.id.iv_msg)
    ImageView mIvMsg;
    @BindView(R.id.tv_msg_dot)
    TextView mTvMsgDot;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    private TabFragmentPagerAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_feed_v3;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        if(savedInstanceState != null){
            String FRAGMENTS_TAG = "android:support:fragments";
            savedInstanceState.remove(FRAGMENTS_TAG);
        }
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
        clickEvent("dongtai");
        List<BaseFragment> fragmentList = new ArrayList<>();
        fragmentList.add(FeedFollowV3Fragment.newInstance());
        fragmentList.add(FeedDynamicV3Fragment.newInstance());
        fragmentList.add(FeedBagFragment.newInstance());

        List<String> titles = new ArrayList<>();
        titles.add(getString(R.string.label_follow));
        titles.add(getString(R.string.label_dynamic));
        titles.add(getString(R.string.label_bag));

        mAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager(),fragmentList,titles);
        mViewPager.setAdapter(mAdapter);
        mPageIndicator.setViewPager(mViewPager);
        mViewPager.setCurrentItem(2);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {
        mIvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mIvMsg.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                FeedNoticeActivity.startActivity(FeedV3Activity.this);
            }
        });
        int num = PreferenceUtils.hasMsg(this);
        if(num > 0){
            mTvMsgDot.setVisibility(View.VISIBLE);
            if(num > 99) num = 99;
            mTvMsgDot.setText(String.valueOf(num));
        }
    }

    @Override
    protected void initListeners() {
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseTime();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTime();
    }

    @Override
    protected void onDestroy() {
        stayEvent("dongtai");
        EventBus.getDefault().unregister(this);
        if(mAdapter != null){
            mAdapter.release();
        }
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void systemMsgEvent(SystemMessageEvent event){
        int num = PreferenceUtils.hasMsg(this);
        if(num > 0){
            mTvMsgDot.setVisibility(View.VISIBLE);
            if(num > 99) num = 99;
            mTvMsgDot.setText(String.valueOf(num));
        }
    }
}
