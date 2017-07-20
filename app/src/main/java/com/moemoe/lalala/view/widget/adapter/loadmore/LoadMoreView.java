package com.moemoe.lalala.view.widget.adapter.loadmore;

import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;

import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;

/**
 * Created by yi on 2017/7/3.
 */

public abstract class LoadMoreView {

    public static final int STATUS_DEFAULT = 1;
    public static final int STATUS_LOADING = 2;
    public static final int STATUS_FAIL = 3;
    public static final int STATUS_END = 4;

    private int mLoadMoreStatus = STATUS_DEFAULT;
    private boolean mLoadMoreEndGone = false;

    public int getmLoadMoreStatus() {
        return mLoadMoreStatus;
    }

    public void setmLoadMoreStatus(int mLoadMoreStatus) {
        this.mLoadMoreStatus = mLoadMoreStatus;
    }

    public boolean ismLoadMoreEndGone() {
        if(getLoadEndViewId()==0){
            return true;
        }
        return mLoadMoreEndGone;
    }

    public void setmLoadMoreEndGone(boolean mLoadMoreEndGone) {
        this.mLoadMoreEndGone = mLoadMoreEndGone;
    }

    public void convert(ClickableViewHolder holder){
        switch (mLoadMoreStatus){
            case STATUS_LOADING:
                visibleLoading(holder, true);
                visibleLoadFail(holder, false);
                visibleLoadEnd(holder, false);
                break;
            case STATUS_FAIL:
                visibleLoading(holder, false);
                visibleLoadFail(holder, true);
                visibleLoadEnd(holder, false);
                break;
            case STATUS_END:
                visibleLoading(holder, false);
                visibleLoadFail(holder, false);
                visibleLoadEnd(holder, true);
                break;
            case STATUS_DEFAULT:
                visibleLoading(holder, false);
                visibleLoadFail(holder, false);
                visibleLoadEnd(holder, false);
                break;
        }
    }

    private void visibleLoading(ClickableViewHolder holder, boolean visible) {
        holder.setVisible(getLoadingViewId(), visible);
    }

    private void visibleLoadFail(ClickableViewHolder holder, boolean visible) {
        holder.setVisible(getLoadFailViewId(), visible);
    }

    private void visibleLoadEnd(ClickableViewHolder holder, boolean visible) {
        final int loadEndViewId=getLoadEndViewId();
        if (loadEndViewId != 0) {
            holder.setVisible(loadEndViewId, visible);
        }
    }

    public abstract @LayoutRes int getLayoutId();

    protected abstract @IdRes int getLoadingViewId();

    protected abstract @IdRes int getLoadFailViewId();

    protected abstract @IdRes int getLoadEndViewId();
}
