package com.moemoe.lalala.view.widget.recycler;

/**
 * Created by Haru on 2016/4/23 0023.
 */
public interface PullCallback {
    void onLoadMore();
    void onRefresh();
    boolean isLoading();
    boolean hasLoadedAllItems();
}
