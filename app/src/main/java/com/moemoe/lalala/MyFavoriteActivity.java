package com.moemoe.lalala;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.app.annotation.ContentView;
import com.app.annotation.FindView;
import com.moemoe.lalala.fragment.PgcFragment;
import com.moemoe.lalala.fragment.UgcFragment;
import com.moemoe.lalala.view.NoDoubleClickListener;

/**
 * Created by Haru on 2016/7/21 0021.
 */
@ContentView(R.layout.ac_msg)
public class MyFavoriteActivity extends BaseActivity {

    @FindView(R.id.iv_back)
    private ImageView mIvBack;
    /**
     * 消息中心pager
     */
    @FindView(R.id.pager_person_data)
    private ViewPager mDataPager;
    private TabFragmentPagerAdapter mAdapter;
    /**
     * 顶上条目
     */
    @FindView(R.id.indicator_person_data)
    private TabLayout mPageIndicator;
    private Fragment mFragUgc, mFragPgc;
    private String mUserId;

    @Override
    protected void initView() {
        if(mIntent != null){
            mUserId = mIntent.getStringExtra(EXTRA_KEY_UUID);
        }
        mIvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager());
        mDataPager.setAdapter(mAdapter);
        mPageIndicator.setupWithViewPager(mDataPager);
    }

    private class TabFragmentPagerAdapter extends FragmentPagerAdapter {

        private String[] mPagerTitle;
        private int mSize;

        private static final int POS_UGC = 0;
        private static final int POS_PGC = 1;

        public TabFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
            mSize = 2;
            mPagerTitle = new String[]{getString(R.string.label_doc),
                    getString(R.string.label_pgc)};
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mPagerTitle[position];
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return super.instantiateItem(container, position);
        }

        @Override
        public Fragment getItem(int pos) {

            Fragment fragment = null;
            if (pos == POS_UGC) {
                if(mFragUgc == null){
                    mFragUgc = new UgcFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("uuid",mUserId);
                    mFragUgc.setArguments(bundle);
                }
                fragment = mFragUgc;
            } else if (pos == POS_PGC) {
                if (mFragPgc == null) {
                    mFragPgc = new PgcFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("uuid",mUserId);
                    mFragPgc.setArguments(bundle);
                }
                fragment = mFragPgc;
            }
            return fragment;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            boolean res = view == ((Fragment) object).getView();
            return res;
        }

        @Override
        public int getCount() {
            return mSize;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            Fragment fragment = ((Fragment) object);
            container.removeView(fragment.getView());
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            super.destroyItem(container, position, object);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == CreateNormalDocActivity.RESPONSE_CODE){
            if(mFragUgc != null){
                mFragUgc.onActivityResult(requestCode,resultCode,data);
            }
        }
    }
}
