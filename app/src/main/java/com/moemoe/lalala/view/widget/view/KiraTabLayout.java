package com.moemoe.lalala.view.widget.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.IntDef;
import android.support.annotation.LayoutRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moemoe.lalala.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 自定义tabLayout用于多种布局
 * Created by yi on 2018/1/11.
 */

public class KiraTabLayout extends HorizontalScrollView implements ViewPager.OnPageChangeListener{

    private Context mContext;
    private ViewPager mViewPager;
    private LinearLayout mTabsContainer;//tab主容器
    private int mCurrentTab;
    private float mCurrentPositionOffset;
    private int mTabCount;
    private Rect mIndicatorRect = new Rect();//指示器绘制
    private Rect mTabRect = new Rect();//滚动居中
    private GradientDrawable mIndicatorDrawable = new GradientDrawable();

    private Paint mRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mDividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mTrianglePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Path mTrianglePath = new Path();

    private static final int STYLE_NORMAL = 0;
    private static final int STYLE_TRIANGLE = 1;
    private static final int STYLE_BLOCK = 2;
    private int mIndicatorStyle = STYLE_NORMAL;

    @IntDef(flag = true,value = {
            STYLE_NORMAL,
            STYLE_TRIANGLE,
            STYLE_BLOCK
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface IndicatorStyle{}

    private float mTabPadding;
    private boolean mTabSpaceEqual;//item 等分
    private float mTabWidth;

    /**
     * indicator 相关属性
     */
    private int mIndicatorColor;
    private float mIndicatorHeight;
    private float mIndicatorWidth;
    private float mIndicatorCornerRadius;
    private float mIndicatorMarginStart;
    private float mIndicatorMarginTop;
    private float mIndicatorMarginEnd;
    private float mIndicatorMarginBottom;
    private int mIndicatorGravity;
    private boolean mIndicatorWidthEqualTitle;

    /**
     * underline 相关属性
     */
    private int mUnderlineColor;
    private float mUnderlineHeight;
    private int mUnderlineGravity;

    /**
     * divider 相关属性
     */
    private int mDividerColor;
    private float mDividerWidth;
    private float mDividerPadding;

    /**
     * title
     */
    private static final int TEXT_BOLD_NONE = 0;
    private static final int TEXT_BOLD_WHEN_SELECT = 1;
    private static final int TEXT_BOLD_BOTH = 2;

    /**
     * default dot
     */
    private int mDotBgColor;
    private int mDotTextColor;
    private float mDotTextSize;
    private int mDotWidth;

    @IntDef(flag = true,value = {
            TEXT_BOLD_NONE,
            TEXT_BOLD_WHEN_SELECT,
            TEXT_BOLD_BOTH
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface TextBold{}

    private float mTextSize;
    private int mTextSelectColor;
    private int mTextUnSelectColor;
    private int mTextBold = TEXT_BOLD_NONE;
    private boolean mTextAllCaps;

    @LayoutRes
    private int mTabLayoutId;

    private int mLastScrollX;
    private int mHeight;
    private boolean mSnapOntabClick;

    private OnTabSelectedListener mListener;

    public interface OnTabSelectedListener {
        void onTabSelect(View tabView,int position);
        void onTabReselect(View tabView,int position);
    }

    public KiraTabLayout(Context context) {
        this(context,null);
    }

    public KiraTabLayout(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public KiraTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFillViewport(true);
        setWillNotDraw(false);//重写onDraw
        setClipChildren(false);
        setClipToPadding(false);

        this.mContext = context;
        mTabsContainer = new LinearLayout(context);
        addView(mTabsContainer);//scrollview 必须且只能有一个子view

        obtainAttributes(context,attrs);

        //get layout_height
        String height = attrs.getAttributeValue("http://schemas.android.com/apk/res/android","layout_height");
        if(!(ViewGroup.LayoutParams.MATCH_PARENT + "").equals(height)
                && !(ViewGroup.LayoutParams.WRAP_CONTENT + "").equals(height)){
            int[] systemAttrs = {android.R.attr.layout_height};
            TypedArray a = context.obtainStyledAttributes(attrs,systemAttrs);
            mHeight = a.getDimensionPixelSize(0,ViewGroup.LayoutParams.WRAP_CONTENT);
            a.recycle();
        }
    }

    /**
     *  读取xml中属性
     */
    private void obtainAttributes(Context context,AttributeSet attrs){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.KiraTabLayout);

        mIndicatorStyle = a.getInt(R.styleable.KiraTabLayout_tl_indicator_style,STYLE_NORMAL);
        mIndicatorColor = a.getColor(R.styleable.KiraTabLayout_tl_indicator_color, ContextCompat.getColor(context,R.color.main_cyan));
        mIndicatorHeight = a.getDimension(R.styleable.KiraTabLayout_tl_indicator_height,2);
        mIndicatorWidth = a.getDimension(R.styleable.KiraTabLayout_tl_indicator_width,-1);
        mIndicatorCornerRadius = a.getDimension(R.styleable.KiraTabLayout_tl_indicator_corner_radius,0);
        mIndicatorMarginStart = a.getDimension(R.styleable.KiraTabLayout_tl_indicator_margin_start,0);
        mIndicatorMarginTop = a.getDimension(R.styleable.KiraTabLayout_tl_indicator_margin_top,0);
        mIndicatorMarginEnd = a.getDimension(R.styleable.KiraTabLayout_tl_indicator_margin_end,0);
        mIndicatorMarginBottom = a.getDimension(R.styleable.KiraTabLayout_tl_indicator_margin_bottom,0);
        mIndicatorGravity = a.getInt(R.styleable.KiraTabLayout_tl_indicator_gravity, Gravity.BOTTOM);
        mIndicatorWidthEqualTitle = a.getBoolean(R.styleable.KiraTabLayout_tl_indicator_width_equal_title,false);

        mUnderlineColor = a.getColor(R.styleable.KiraTabLayout_tl_underline_color,ContextCompat.getColor(context,R.color.main_cyan));
        mUnderlineHeight = a.getDimension(R.styleable.KiraTabLayout_tl_underline_height,0);
        mUnderlineGravity = a.getInt(R.styleable.KiraTabLayout_tl_underline_gravity,Gravity.BOTTOM);

        mDividerColor = a.getColor(R.styleable.KiraTabLayout_tl_divider_color,ContextCompat.getColor(context,R.color.gray_e8e8e8));
        mDividerWidth = a.getDimension(R.styleable.KiraTabLayout_tl_divider_width,0);
        mDividerPadding = a.getDimension(R.styleable.KiraTabLayout_tl_divider_padding,0);

        mTextSize = a.getDimensionPixelSize(R.styleable.KiraTabLayout_tl_text_size,getResources().getDimensionPixelSize(R.dimen.x24));
        mTextSelectColor = a.getColor(R.styleable.KiraTabLayout_tl_textSelectColor,ContextCompat.getColor(context,R.color.main_cyan));
        mTextUnSelectColor = a.getColor(R.styleable.KiraTabLayout_tl_textUnSelectColor,ContextCompat.getColor(context,R.color.main_cyan_50));
        mTextBold = a.getInt(R.styleable.KiraTabLayout_tl_textBold,TEXT_BOLD_NONE);
        mTextAllCaps = a.getBoolean(R.styleable.KiraTabLayout_tl_textAllCaps,false);

        mTabSpaceEqual = a.getBoolean(R.styleable.KiraTabLayout_tl_tab_space_equal,false);
        mTabWidth = a.getDimension(R.styleable.KiraTabLayout_tl_tab_width,-1);
        mTabPadding = a.getDimension(R.styleable.KiraTabLayout_tl_tab_padding,0);

        mTabLayoutId = a.getResourceId(R.styleable.KiraTabLayout_tl_tab_layout,R.layout.item_tab_normal);

        mDotBgColor = a.getColor(R.styleable.KiraTabLayout_tl_dot_bg_color,ContextCompat.getColor(context,R.color.main_red));
        mDotTextColor = a.getColor(R.styleable.KiraTabLayout_tl_dot_text_color,ContextCompat.getColor(context,R.color.white));
        mDotTextSize = a.getDimension(R.styleable.KiraTabLayout_tl_dot_text_size,mTextSize / 2);
        mDotWidth = a.getDimensionPixelSize(R.styleable.KiraTabLayout_tl_dot_width,0);
        a.recycle();
    }

    public void setViewPager(ViewPager vp){
        if(vp == null || vp.getAdapter() == null){
            throw new IllegalStateException("Viewpager or ViewPager Adapter can not be null");
        }
        this.mViewPager = vp;
        this.mViewPager.removeOnPageChangeListener(this);
        this.mViewPager.addOnPageChangeListener(this);
        notifyDataSetChanged();
    }

    public void notifyDataSetChanged(){
        mTabsContainer.removeAllViews();
        this.mTabCount = mViewPager.getAdapter().getCount();
        View tabView;
        for(int i = 0;i < mTabCount;i++){
            tabView = View.inflate(mContext,mTabLayoutId,null);
            String title = mViewPager.getAdapter().getPageTitle(i).toString();
            addTab(i,title,tabView);
        }
        updateTabStyles();
    }

    private void addTab(final int position,String title,View tabView){
        TextView tv_title = tabView.findViewById(R.id.tv_tab_title);
        if(tv_title == null){
            throw new IllegalStateException("title textView id must be tv_tab_title");
        }
        if(!TextUtils.isEmpty(title)){
            tv_title.setText(title);
        }

        tabView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position != -1){
                    if(mViewPager.getCurrentItem() != position){
                        if(mSnapOntabClick){
                            mViewPager.setCurrentItem(position,false);
                        }else {
                            mViewPager.setCurrentItem(position);
                        }
                        if(mListener != null){
                            mListener.onTabSelect(v,position);
                        }
                    }else {
                        if(mListener != null){
                            mListener.onTabReselect(v,position);
                        }
                    }
                }
            }
        });

        LinearLayout.LayoutParams lp = mTabSpaceEqual ?
                new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT,1.0f) :
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if(mTabWidth > 0){
            lp = new LinearLayout.LayoutParams((int) mTabWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        mTabsContainer.addView(tabView,position,lp);
    }

    private void updateTabStyles(){
        for(int i = 0;i < mTabCount;i++){
            View v = mTabsContainer.getChildAt(i);
            TextView tv_title = v.findViewById(R.id.tv_tab_title);
            if(tv_title == null){
                throw new IllegalStateException("title textView id must be tv_tab_title");
            }
            tv_title.setTextColor(i == mCurrentTab ? mTextSelectColor : mTextUnSelectColor);
            tv_title.setTextSize(TypedValue.COMPLEX_UNIT_PX,mTextSize);
            tv_title.setPadding((int) mTabPadding,0, (int) mTabPadding,0);
            if(mTextAllCaps){
                tv_title.setText(tv_title.getText().toString().toUpperCase());
            }

            if(mTextBold == TEXT_BOLD_BOTH){
                tv_title.getPaint().setFakeBoldText(true);
            }else if(mTextBold == TEXT_BOLD_NONE){
                tv_title.getPaint().setFakeBoldText(false);
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        this.mCurrentTab = position;
        this.mCurrentPositionOffset = positionOffset;
        scrollToCurrentTab();
        invalidate();
    }

    @Override
    public void onPageSelected(int position) {
        updateTabSelection(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    /**
     * 滚动到当前tab，并居中显示
     */
    private void scrollToCurrentTab(){
        if(mTabCount <= 0){
            return;
        }
        int offset = (int) (mCurrentPositionOffset * mTabsContainer.getChildAt(mCurrentTab).getWidth());
        int newScrollX = mTabsContainer.getChildAt(mCurrentTab).getLeft() + offset;
        if(mCurrentTab > 0 || offset > 0){
            newScrollX -= getWidth() / 2 - getPaddingStart();
            calcIndicatorRect();
            newScrollX += ((mTabRect.right - mTabRect.left) / 2);
        }

        if(newScrollX != mLastScrollX){
            mLastScrollX = newScrollX;
            scrollTo(newScrollX,0);
        }
    }

    private void updateTabSelection(int position){
        for(int i = 0;i < mTabCount;i++){
            View tabView = mTabsContainer.getChildAt(i);
            final boolean isSelect = i == position;
            TextView tv_title = tabView.findViewById(R.id.tv_tab_title);
            if(tv_title == null){
                throw new IllegalStateException("title textView id must be tv_tab_title");
            }
            tv_title.setTextColor(isSelect ? mTextSelectColor : mTextUnSelectColor);
            if(mTextBold == TEXT_BOLD_WHEN_SELECT){
                tv_title.getPaint().setFakeBoldText(isSelect);
            }
        }
    }

    private float margin;
    private Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//msg dot

    private void calcIndicatorRect(){
        View currentTabView = mTabsContainer.getChildAt(mCurrentTab);
        float left = currentTabView.getLeft();
        float right = currentTabView.getRight();

        if(mIndicatorStyle == STYLE_NORMAL && mIndicatorWidthEqualTitle){
            TextView tv_title = currentTabView.findViewById(R.id.tv_tab_title);
            mTextPaint.setTextSize(mTextSize);
            float textWidth = mTextPaint.measureText(tv_title.getText().toString());
            margin = (right - left - textWidth) / 2;
        }

        if(mCurrentTab < mTabCount - 1){
            View nextTabView = mTabsContainer.getChildAt(this.mCurrentTab + 1);
            float nextTabLeft = nextTabView.getLeft();
            float nextTabRight = nextTabView.getRight();

            left = left + mCurrentPositionOffset * (nextTabLeft - left);
            right = right + mCurrentPositionOffset * (nextTabRight - right);

            if (mIndicatorStyle == STYLE_NORMAL && mIndicatorWidthEqualTitle) {
                TextView tv_title = nextTabView.findViewById(R.id.tv_tab_title);
                mTextPaint.setTextSize(mTextSize);
                float nextTextWidth = mTextPaint.measureText(tv_title.getText().toString());
                float nextMargin = (nextTabRight - nextTabLeft - nextTextWidth) / 2;
                margin = margin + mCurrentPositionOffset * (nextMargin - margin);
            }
        }

        mIndicatorRect.left = (int) left;
        mIndicatorRect.right = (int) right;

        if (mIndicatorStyle == STYLE_NORMAL && mIndicatorWidthEqualTitle) {
            mIndicatorRect.left = (int) (left + margin - 1);
            mIndicatorRect.right = (int) (right - margin - 1);
        }

        mTabRect.left = (int) left;
        mTabRect.right = (int) right;

        if (mIndicatorWidth < 0) {   //indicatorWidth小于0时,原jpardogo's PagerSlidingTabStrip

        } else {//indicatorWidth大于0时,圆角矩形以及三角形
            float indicatorLeft = currentTabView.getLeft() + (currentTabView.getWidth() - mIndicatorWidth) / 2;

            if (this.mCurrentTab < mTabCount - 1) {
                View nextTab = mTabsContainer.getChildAt(this.mCurrentTab + 1);
                indicatorLeft = indicatorLeft + mCurrentPositionOffset * (currentTabView.getWidth() / 2 + nextTab.getWidth() / 2);
            }

            mIndicatorRect.left = (int) indicatorLeft;
            mIndicatorRect.right = (int) (mIndicatorRect.left + mIndicatorWidth);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(isInEditMode() || mTabCount <= 0){
            return;
        }
        int height = getHeight();
        int paddingStart = getPaddingStart();

        //draw divider
        if(mDividerWidth > 0){
            mDividerPaint.setStrokeWidth(mDividerWidth);
            mDividerPaint.setColor(mDividerColor);
            for(int i = 0;i < mTabCount - 1;i++){
                View tab = mTabsContainer.getChildAt(i);
                canvas.drawLine(paddingStart + tab.getRight(),mDividerPadding , paddingStart + tab.getRight(),height - mDividerPadding,mDividerPaint);
            }
        }

        //draw underline
        if(mUnderlineHeight > 0){
            mRectPaint.setColor(mUnderlineColor);
            if(mUnderlineGravity == Gravity.BOTTOM){
                canvas.drawRect(paddingStart,height - mUnderlineHeight,mTabsContainer.getWidth() + paddingStart,height,mRectPaint);
            }else {
                canvas.drawRect(paddingStart,0,mTabsContainer.getWidth() + paddingStart,mUnderlineHeight,mRectPaint);
            }
        }

        //draw indicator
        calcIndicatorRect();
        if(mIndicatorStyle == STYLE_TRIANGLE){
            if(mIndicatorHeight > 0){
                mTrianglePaint.setColor(mIndicatorColor);
                mTrianglePath.reset();
                mTrianglePath.moveTo(paddingStart + mIndicatorRect.left, height);
                mTrianglePath.lineTo(paddingStart + mIndicatorRect.left / 2 + mIndicatorRect.right / 2, height - mIndicatorHeight);
                mTrianglePath.lineTo(paddingStart + mIndicatorRect.right, height);
                mTrianglePath.close();
                canvas.drawPath(mTrianglePath, mTrianglePaint);
            }
        }else if(mIndicatorStyle == STYLE_BLOCK){
            if (mIndicatorHeight < 0) {
                mIndicatorHeight = height - mIndicatorMarginTop - mIndicatorMarginBottom;
            }
            if (mIndicatorHeight > 0) {
                if (mIndicatorCornerRadius < 0 || mIndicatorCornerRadius > mIndicatorHeight / 2) {
                    mIndicatorCornerRadius = mIndicatorHeight / 2;
                }

                mIndicatorDrawable.setColor(mIndicatorColor);
                mIndicatorDrawable.setBounds(paddingStart + (int) mIndicatorMarginStart + mIndicatorRect.left,
                        (int) mIndicatorMarginTop, (int) (paddingStart + mIndicatorRect.right - mIndicatorMarginEnd),
                        (int) (mIndicatorMarginTop + mIndicatorHeight));
                mIndicatorDrawable.setCornerRadius(mIndicatorCornerRadius);
                mIndicatorDrawable.draw(canvas);
            }
        }else {
            if (mIndicatorHeight > 0) {
                mIndicatorDrawable.setColor(mIndicatorColor);
                if (mIndicatorGravity == Gravity.BOTTOM) {
                    mIndicatorDrawable.setBounds(paddingStart + (int) mIndicatorMarginStart + mIndicatorRect.left,
                            height - (int) mIndicatorHeight - (int) mIndicatorMarginBottom,
                            paddingStart + mIndicatorRect.right - (int) mIndicatorMarginEnd,
                            height - (int) mIndicatorMarginBottom);
                } else {
                    mIndicatorDrawable.setBounds(paddingStart + (int) mIndicatorMarginStart + mIndicatorRect.left,
                            (int) mIndicatorMarginTop,
                            paddingStart + mIndicatorRect.right - (int) mIndicatorMarginEnd,
                            (int) mIndicatorHeight + (int) mIndicatorMarginTop);
                }
                mIndicatorDrawable.setCornerRadius(mIndicatorCornerRadius);
                mIndicatorDrawable.draw(canvas);
            }
        }
    }

    private SparseArray<Boolean> mInitSetMap = new SparseArray<>();

    /**
     * 显示未读消息
     * @param position 位置
     * @param num 小于等于0显示点，大于0显示具体数字
     */
    public void showMsg(int position,int num){
        if(position >= mTabCount || position < 0){
            return;
        }
        View tabView = mTabsContainer.getChildAt(position);
        TextView msgView = tabView.findViewById(R.id.tv_tab_msg_dot);
        if(msgView != null){
            msgView.setVisibility(VISIBLE);
            msgView.setTextColor(mDotTextColor);
            msgView.setTextSize(TypedValue.COMPLEX_UNIT_PX,mDotTextSize);
            setMsg(msgView,num);
            if (mInitSetMap.get(position) != null && mInitSetMap.get(position)) {
                return;
            }
            setMsgMargin(position);
            mInitSetMap.put(position, true);
        }
    }

    public void showMsg(int position){
        showMsg(position,0);
    }

    private void setMsg(TextView msg,int num){
        RelativeLayout.LayoutParams lp;
        GradientDrawable dotBg = new GradientDrawable();
        dotBg.setColor(mDotBgColor);
        if(num > 0){
            if(num > 99){
                num = 99;//最多显示99
            }
            lp = new RelativeLayout.LayoutParams(mDotWidth,mDotWidth);
            msg.setText("" + num);
            dotBg.setCornerRadius(mDotWidth / 2);
        }else {
            int size = getResources().getDimensionPixelSize(R.dimen.y5);
            lp = new RelativeLayout.LayoutParams(size,size);
            msg.setText("");
            dotBg.setCornerRadius(size / 2);
        }
        msg.setLayoutParams(lp);
        msg.setBackground(dotBg);
    }

    public void hideMsg(int position){
        if(position > mTabCount - 1 || position < 0){
            return;
        }
        View tabView = mTabsContainer.getChildAt(position);
        TextView msgView = tabView.findViewById(R.id.tv_tab_msg_dot);
        if(msgView != null){
            msgView.setVisibility(GONE);
        }
    }

    private void setMsgMargin(int position){
        View tabView = mTabsContainer.getChildAt(position);
        TextView msgView = tabView.findViewById(R.id.tv_tab_msg_dot);
        if(msgView != null){
            TextView tv_tab_title = tabView.findViewById(com.flyco.tablayout.R.id.tv_tab_title);
            mTextPaint.setTextSize(mTextSize);
            float textWidth = mTextPaint.measureText(tv_tab_title.getText().toString());
            float textHeight = mTextPaint.descent() - mTextPaint.ascent();
            MarginLayoutParams lp = (MarginLayoutParams) msgView.getLayoutParams();
            lp.topMargin = mHeight > 0 ? (int) (mHeight - textHeight) / 2 - msgView.getHeight() / 2 : 0;
            msgView.setLayoutParams(lp);
        }
    }

    /**
     * 自定义tabItem时
     */
    public View getTabView(int position){
        if(position > mTabCount - 1 || position < 0){
            return null;
        }
        return mTabsContainer.getChildAt(position);
    }
}
