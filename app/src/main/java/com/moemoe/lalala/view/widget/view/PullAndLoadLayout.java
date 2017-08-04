package com.moemoe.lalala.view.widget.view;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.view.activity.ClassActivity;
import com.moemoe.lalala.view.widget.recycler.PullCallback;
import com.moemoe.lalala.view.widget.recycler.ScrollDirection;

/**
 * Created by Haru on 2016/8/4 0004.
 */
public class PullAndLoadLayout extends FrameLayout {
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ScrollView mScrollView;
    private View mClass;
    private ProgressBar mProgressBar;
    private PullCallback mPullCallback;
    private DraggableLayout mFsdLayout;
    protected ScrollDirection mCurScrollingDirection;
    private boolean mIsLoadMoreEnabled = false;
    private int yDown = 0;
    private int lastY = 0;

    public PullAndLoadLayout(Context context) {
        this(context, null);
    }

    public PullAndLoadLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullAndLoadLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(R.layout.loadscrollview,this,true);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mScrollView = (ScrollView) findViewById(R.id.scroll);
        mFsdLayout = (DraggableLayout) findViewById(R.id.fsd_layout);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mClass = findViewById(R.id.tv_class);
        init();
    }

    private void init() {
        mCurScrollingDirection = ScrollDirection.SAME;
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (null != mPullCallback && !mPullCallback.isLoading()) {
                    mPullCallback.onRefresh();
                }
            }
        });
        mClass.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                Intent i = new Intent(getContext(), ClassActivity.class);
                getContext().startActivity(i);
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                yDown = (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                lastY = (int) ev.getRawY();
                if(yDown - lastY > 60){
                    mCurScrollingDirection = ScrollDirection.UP;
                    if(mScrollView.getScrollY() >= (mScrollView.getChildAt(0).getHeight() - getMeasuredHeight())){
                        if (null != mPullCallback && mIsLoadMoreEnabled && !mPullCallback.isLoading() && !mPullCallback.hasLoadedAllItems()) {
                            mProgressBar.setVisibility(VISIBLE);
                            mPullCallback.onLoadMore();
                        }
                    }
                }else if(yDown - lastY < 0){
                    mCurScrollingDirection = ScrollDirection.DOWN;
                }
                break;
            case MotionEvent.ACTION_UP:
                mCurScrollingDirection = ScrollDirection.SAME;
                yDown = 0;
                lastY = 0;
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    public void setComplete() {
        mProgressBar.setVisibility(GONE);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public void initLoad() {
        if (null != mPullCallback) {
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
            });
            mPullCallback.onRefresh();
        }
    }

    public SwipeRefreshLayout getSwipeRefreshLayout(){
        return mSwipeRefreshLayout;
    }

    public DraggableLayout getFsdLayout(){
        return mFsdLayout;
    }

    public void setPullCallback(PullCallback mPullCallback) {
        this.mPullCallback = mPullCallback;
    }

    public void isLoadMoreEnabled(boolean mIsLoadMoreEnabled) {
        this.mIsLoadMoreEnabled = mIsLoadMoreEnabled;
    }
}
