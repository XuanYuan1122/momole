package com.moemoe.lalala.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Haru on 2016/4/28 0028.
 */
public class MenuHItemDecoration extends RecyclerView.ItemDecoration {

    private int topSpace;
    private int space;

    public MenuHItemDecoration(int topSpace,int space) {
        this.topSpace = topSpace;
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = space;
        outRect.top = topSpace;
        if(parent.getChildLayoutPosition(view) == 0){
            outRect.left = topSpace;
        }
    }
}
