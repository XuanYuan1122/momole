package com.moemoe.lalala;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.moemoe.lalala.utils.PreferenceManager;
import com.moemoe.lalala.utils.ResourceUtils;

import org.xutils.common.util.DensityUtil;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;

/**
 * Created by Haru on 2016/8/25.
 */
@ContentView(R.layout.ac_guide)
public class GuideActivity extends BaseActivity {
    public static final String EXTRA_BY_USER = "by_user";
    public static final String EXTRA_HAVE_CHOSE = "have_chose";
    public static final String EXTRA_PLOT = "plot";
    @ViewInject(R.id.pager_class_banner)
    private ViewPager mViewPager;
    @ViewInject(R.id.ll_point_group)
    private LinearLayout mLlPointGroup;
    @ViewInject(R.id.view_pressed_point)
    private View mPressedPoint;
    @ViewInject(R.id.ll_point_container)
    private View mLlPoint;
    @ViewInject(R.id.ll_select_container)
    private View mLlSelect;
    @ViewInject(R.id.iv_yes)
    private ImageView mIvYes;
    @ViewInject(R.id.iv_no)
    private ImageView mIvNo;
    @ViewInject(R.id.iv_word)
    private ImageView mIvWord;
    private int mWidth;
    private boolean mBCallByUser = false;
    private boolean mHaveChose = false;
    private int[] mResId = {R.drawable.bg_welcome_1,R.drawable.bg_welcome_2,R.drawable.bg_welcome_3,R.drawable.bg_welcome_4,R.drawable.bg_welcome_5,R.drawable.bg_welcome_6,R.drawable.bg_welcome_7};
    private String[] mPlot;
    private int mCurPosition;
    private GestureDetector gestureDetector; // 用户滑动

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
                        if(PreferenceManager.isLogin(GuideActivity.this)){
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
    protected void initView() {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        String bCallByUser = mIntent.getStringExtra(EXTRA_BY_USER);
        mBCallByUser = bCallByUser != null && bCallByUser.equals("true");
        String haveChose = mIntent.getStringExtra(EXTRA_HAVE_CHOSE);
        mHaveChose = !(haveChose != null && haveChose.equals("false"));
        String plot = mIntent.getStringExtra(EXTRA_PLOT);
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
                        if(PreferenceManager.isLogin(GuideActivity.this)){
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
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DensityUtil.dip2px(10), DensityUtil.dip2px(10));
            if (i > 0) {
                params.leftMargin = DensityUtil.dip2px(4);
            }
            view.setLayoutParams(params);
            mLlPointGroup.addView(view);
        }
        mViewPager.setAdapter(new BannerAdapter(imageViews));
        mViewPager.setCurrentItem(4);
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
                int len = 4 * mWidth;
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mPressedPoint.getLayoutParams();
                params.leftMargin = len;
                mPressedPoint.setLayoutParams(params);
            }
        });
        if(mHaveChose){
            mIvNo.setVisibility(View.VISIBLE);
            mIvYes.setVisibility(View.VISIBLE);
            mIvWord.setVisibility(View.VISIBLE);
        }else {
            mIvNo.setVisibility(View.GONE);
            mIvYes.setVisibility(View.GONE);
            mIvWord.setVisibility(View.GONE);
        }
        mIvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIvWord.setSelected(true);
                ObjectAnimator outR = ObjectAnimator.ofFloat(mIvNo,"rotation",0,45).setDuration(100);
                outR.setInterpolator(new LinearInterpolator());
                ObjectAnimator outT = ObjectAnimator.ofFloat(mIvNo,"translationY",0,mIvNo.getHeight() + mIvWord.getHeight() + DensityUtil.dip2px(32)).setDuration(500);
                outT.setInterpolator(new AccelerateInterpolator());
                AnimatorSet set = new AnimatorSet();
                set.play(outR).with(outT);
                set.start();
            }
        });
        mIvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBCallByUser){
                    tryLoginFirst(null);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //初始化首页数据
                            finish();
                            overridePendingTransition(0, 0);
                        }
                    }, 800);
                }else {
                    go2Login();
                }
            }
        });
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
        mPreferInfo.setAppFirstLaunch(false);
        mPreferInfo.setVersion2FirstLaunch(false);
        mPreferInfo.setVersionCode(getString(R.string.app_version_code));
        mPreferMng.saveFirstLaunch();
        mPreferMng.saveNewVersionFirstLaunch();
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
        //ActivityCompat.finishAfterTransition(this);
        overridePendingTransition(0, 0);
    }
}
