package com.moemoe.lalala.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 *
 * Created by Haru on 2016/4/28 0028.
 */
public class StartDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public StartDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = space;
    }
}
