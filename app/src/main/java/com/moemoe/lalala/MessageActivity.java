package com.moemoe.lalala;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.EdgeEffectCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.app.annotation.ContentView;
import com.app.annotation.FindView;
import com.moemoe.lalala.fragment.MyCommentFragment;
import com.moemoe.lalala.fragment.NewsFragment;
import com.moemoe.lalala.view.NoDoubleClickListener;

import java.lang.reflect.Field;

/**
 * Created by Haru on 2016/7/27 0027.
 */
@ContentView(R.layout.ac_msg)
public class MessageActivity extends BaseActivity {

    @FindView(R.id.iv_back)
    private ImageView IvBack;
    @FindView(R.id.pager_person_data)
    private ViewPager mDataPager;
    @FindView(R.id.indicator_person_data)
    private TabLayout mPageIndicator;
    private TabFragmentPagerAdapter mAdapter;
    private EdgeEffectCompat mLeftEdge;
    private EdgeEffectCompat mRightEdge;

    @Override
    protected void initView() {
        IvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager());
        mDataPager.setAdapter(mAdapter);
        mPageIndicator.setupWithViewPager(mDataPager);
        try {
            Field leftEdgeField = mDataPager.getClass().getDeclaredField("mLeftEdge");
            Field rightEdgeField = mDataPager.getClass().getDeclaredField("mRightEdge");
            if(leftEdgeField != null && rightEdgeField != null){
                leftEdgeField.setAccessible(true);
                rightEdgeField.setAccessible(true);
                mLeftEdge = (EdgeEffectCompat) leftEdgeField.get(mDataPager);
                mRightEdge = (EdgeEffectCompat) rightEdgeField.get(mDataPager);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mDataPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                //去除边界阴影
                if (mLeftEdge != null && mRightEdge != null) {
                    mLeftEdge.finish();
                    mRightEdge.finish();
                    mLeftEdge.setSize(0, 0);
                    mRightEdge.setSize(0, 0);
                }
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    private class TabFragmentPagerAdapter extends FragmentPagerAdapter {

        private String[] mPagerTitle;
        private int mSize;

        private static final int POS_CLUB = 0;
        private static final int POS_DOC = 1;

        private Fragment mFragClub, mFragDoc;

        public TabFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
            mSize = 2;
            mPagerTitle = new String[]{getString(R.string.label_reply),
                    getString(R.string.label_tongzhi)};
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
            if (pos == POS_CLUB) {
                if(mFragClub == null){
                    mFragClub = new MyCommentFragment();
                }
                fragment = mFragClub;
            } else if (pos == POS_DOC) {
                if (mFragDoc == null) {
                    mFragDoc = new NewsFragment();
                }
                fragment = mFragDoc;
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
}
