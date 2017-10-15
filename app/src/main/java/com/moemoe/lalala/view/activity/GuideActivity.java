package com.moemoe.lalala.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.moemoe.lalala.R;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.ResourceUtils;
import com.moemoe.lalala.utils.ViewUtils;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * 引导页
 * Created by yi on 2016/11/28.
 */

public class GuideActivity extends BaseAppCompatActivity {

    public static final String EXTRA_BY_USER = "by_user";
    public static final String EXTRA_HAVE_CHOSE = "have_chose";
    public static final String EXTRA_PLOT = "plot";
    @BindView(R.id.pager_class_banner)
    ViewPager mViewPager;
    @BindView(R.id.ll_point_group)
    LinearLayout mLlPointGroup;
    @BindView(R.id.view_pressed_point)
    View mPressedPoint;
    @BindView(R.id.ll_point_container)
    View mLlPoint;
    private int mWidth;
    private boolean mBCallByUser = false;
    private boolean mHaveChose = false;
    private int[] mResId = {R.drawable.bg_welcome_1,R.drawable.bg_welcome_2,R.drawable.bg_welcome_3,R.drawable.bg_welcome_4,R.drawable.bg_welcome_5,R.drawable.bg_welcome_6,R.drawable.bg_welcome_7};
    private String[] mPlot;
    private int mCurPosition;
    private GestureDetector gestureDetector; // 用户滑动

    @Override
    protected int getLayoutId() {
        return R.layout.ac_guide;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (gestureDetector.onTouchEvent(event)) {
            event.setAction(MotionEvent.ACTION_CANCEL);
        }
        return super.dispatchTouchEvent(event);
    }

    private class GuideViewTouch extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (mCurPosition == 6) {
                if (Math.abs(e1.getX() - e2.getX()) > Math.abs(e1.getY() - e2.getY()) && (e1.getX() - e2.getX() <= (-13) || e1.getX() - e2.getX() >= 13)) {
                    if (e1.getX() - e2.getX() >= 13) {
                        if(PreferenceUtils.isLogin()){
                            goToMain();
                        }else {
                            go2Login();
                        }
                        return true;
                    }
                }
            }
            return false;
        }
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ViewUtils.setStatusBarLight(getWindow(), null);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        String bCallByUser = getIntent().getStringExtra(EXTRA_BY_USER);
        mBCallByUser = bCallByUser != null && bCallByUser.equals("true");
        String haveChose = getIntent().getStringExtra(EXTRA_HAVE_CHOSE);
        mHaveChose = !(haveChose != null && haveChose.equals("false"));
        String plot = getIntent().getStringExtra(EXTRA_PLOT);
        if(!TextUtils.isEmpty(plot)){
            mPlot = plot.split(",");
        }
        if(mPlot != null){
            mResId = null;
            mResId = new int[mPlot.length];
            for (int i = 0;i < mPlot.length;i++){
                mResId[i] = ResourceUtils.getResource(this,mPlot[i]);
            }
        }
        gestureDetector = new GestureDetector(this,new GuideViewTouch());
        ArrayList<ImageView> imageViews = new ArrayList<>();
        mLlPointGroup.removeAllViews();
        for(int i=0;i<mResId.length;i++) {
            ImageView image = new ImageView(this);
            image.setImageResource(mResId[i]);
            image.setScaleType(ImageView.ScaleType.FIT_XY);
            ViewGroup.LayoutParams params1 = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            image.setLayoutParams(params1);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int i = mViewPager.getCurrentItem();
                    if(i < mViewPager.getAdapter().getCount() - 1){
                        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                    }else {
                        if(PreferenceUtils.isLogin()){
                            goToMain();
                        }else {
                            go2Login();
                        }
                    }
                }
            });
            imageViews.add(image);
            View view = new View(this);
            view.setBackgroundResource(R.drawable.icon_class_banner_switch_white);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DensityUtil.dip2px(this,10), DensityUtil.dip2px(this,10));
            if (i > 0) {
                params.leftMargin = DensityUtil.dip2px(this,4);
            }
            view.setLayoutParams(params);
            mLlPointGroup.addView(view);
        }
        mViewPager.setAdapter(new BannerAdapter(imageViews));
        mViewPager.setCurrentItem(0);
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    protected void initListeners() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int len = (int) (mWidth * positionOffset) + position * mWidth;
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mPressedPoint.getLayoutParams();
                params.leftMargin = len;
                mPressedPoint.setLayoutParams(params);
            }

            @Override
            public void onPageSelected(int position) {
                mCurPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mLlPointGroup.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mLlPointGroup.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mWidth = mLlPointGroup.getChildAt(1).getLeft()
                        - mLlPointGroup.getChildAt(0).getLeft();
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mPressedPoint.getLayoutParams();
                params.leftMargin = 0;
                mPressedPoint.setLayoutParams(params);
            }
        });
    }

    @Override
    protected void initData() {

    }

    public class BannerAdapter extends PagerAdapter {

        private ArrayList<ImageView> images;

        BannerAdapter(ArrayList<ImageView> images){
            this.images = images;
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(images.get(position));
            return images.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    private void goToMain(){
        saveLaunch();
        Intent i = new Intent(this,MapActivity.class);
        startActivity(i);
        finish();
    }

    private void saveLaunch(){
        PreferenceUtils.setAppFirstLaunch(this,false);
        PreferenceUtils.setVersion2FirstLaunch(this,false);
        PreferenceUtils.setVersionCode(this,getString(R.string.app_version_code));
    }

    private void go2Login(){
        saveLaunch();
        Bundle bundle = new Bundle();
        bundle.putBoolean(LoginActivity.EXTRA_KEY_FIRST_RUN, true);
        Intent i = new Intent(this,LoginActivity.class);
        i.putExtra(LoginActivity.EXTRA_KEY_SETTING, true);
        i.putExtras(bundle);
        startActivity(i);
        finish();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }
}
