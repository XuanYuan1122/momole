package com.moemoe.lalala.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Haru on 2016/4/28 0028.
 */
public class MenuVItemDecoration extends RecyclerView.ItemDecoration {

    private int topSpace;

    public MenuVItemDecoration(int space) {
        this.topSpace = topSpace;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.top = topSpace;
        if(parent.getChildLayoutPosition(view) == 0){
            outRect.top = topSpace;
        }
    }
}
