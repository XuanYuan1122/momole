package com.moemoe.lalala.view.widget.recycler;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.moemoe.lalala.R;

/**
 * Created by Haru on 2016/4/23 0023.
 */
public class PullAndLoadView extends FrameLayout {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private PullCallback mPullCallback;
    private RecyclerViewPositionHelper mRecyclerViewHelper;
    protected ScrollDirection mCurScrollingDirection;
    protected int mPrevFirstVisibleItem = 0;
    private int mLoadMoreOffset = 1;
    private boolean mIsLoadMoreEnabled = false;
    private int yDown = 0;
    private int lastY = 0;

    public PullAndLoadView(Context context) {
      this(context, null);
    }

    public PullAndLoadView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullAndLoadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(R.layout.loadview,this,true);
        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        mRecyclerView = findViewById(R.id.recyclerView);
        mProgressBar = findViewById(R.id.progressBar);
        init();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
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
                    int firstVisible = mRecyclerViewHelper.findFirstVisibleItemPosition();
                    int lastPosition = mRecyclerViewHelper.findLastCompletelyVisibleItemPosition();
                    int lastvisible = mRecyclerViewHelper.findLastVisibleItemPosition();

                    if (mIsLoadMoreEnabled && (lastPosition == lastvisible || firstVisible == lastvisible) && (mCurScrollingDirection == ScrollDirection.UP)) {
                        if (mPullCallback != null && !mPullCallback.isLoading() && !mPullCallback.hasLoadedAllItems()) {
                            final int totalItemCount = mRecyclerViewHelper.getItemCount();
                            final int firstVisibleItem = mRecyclerViewHelper.findFirstVisibleItemPosition();
                            final int visibleItemCount = Math.abs(mRecyclerViewHelper.findLastVisibleItemPosition() - firstVisibleItem);
                            final int lastAdapterPosition = totalItemCount - 1;
                            final int lastVisiblePosition = (firstVisibleItem + visibleItemCount) - 1;
                            if (lastVisiblePosition >= (lastAdapterPosition - mLoadMoreOffset)) {
                                if (null != mPullCallback) {
                                    mProgressBar.setVisibility(VISIBLE);
                                    mPullCallback.onLoadMore();
                                }
                            }
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

    private void init() {
        mCurScrollingDirection = ScrollDirection.SAME;
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (null != mPullCallback) {
                    mPullCallback.onRefresh();
                }
            }
        });
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
//                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                    if(getContext() instanceof BaseAppCompatActivity){
//                        if(!((BaseAppCompatActivity) getContext()).isFinishing()) Glide.with(getContext()).resumeRequests();
//                    }
//                } else {
//                    if(getContext() instanceof BaseAppCompatActivity){
//                        if(!((BaseAppCompatActivity) getContext()).isFinishing()) Glide.with(getContext()).pauseRequests();
//                    }
//                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
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

    public RecyclerView getRecyclerView() {
        return this.mRecyclerView;
    }

    public void setLayoutManager(RecyclerView.LayoutManager layoutManager){
        this.mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerViewHelper = RecyclerViewPositionHelper.createHelper(mRecyclerView);
    }

    public SwipeRefreshLayout getSwipeRefreshLayout(){
        return mSwipeRefreshLayout;
    }

    public void setPullCallback(PullCallback mPullCallback) {
        this.mPullCallback = mPullCallback;
    }

    public void setLoadMoreOffset(int mLoadMoreOffset) {
        this.mLoadMoreOffset = mLoadMoreOffset;
    }

    public void setLoadMoreEnabled(boolean mIsLoadMoreEnabled) {
        this.mIsLoadMoreEnabled = mIsLoadMoreEnabled;
    }

    public boolean isLoadMoreEnabled(){
        return mIsLoadMoreEnabled;
    }
}
