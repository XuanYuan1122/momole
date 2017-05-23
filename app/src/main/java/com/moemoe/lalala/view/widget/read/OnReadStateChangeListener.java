package com.moemoe.lalala.view.widget.read;

/**
 * Created by yi on 2017/3/29.
 */

public interface OnReadStateChangeListener {
    void onChapterChanged(int chapter);

    void onPageChanged(int chapter,int page);

    void onLoadChapterFailure(int chapter);

    void onCenterClick();

    void onFlip();

    void onBookFinish(String bookId);

}
